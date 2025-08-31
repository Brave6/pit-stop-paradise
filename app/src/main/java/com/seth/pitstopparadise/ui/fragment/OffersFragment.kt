package com.seth.pitstopparadise.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.seth.pitstopparadise.databinding.FragmentOffersBinding
import com.seth.pitstopparadise.ui.adapter.OfferAdapter
import com.seth.pitstopparadise.viewmodel.OffersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OffersFragment : Fragment() {

    private var _binding: FragmentOffersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OffersViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOffersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = OfferAdapter { product ->
            product?.let {
                Log.d("OffersFragment", "Navigating with product: $product")
                val action = OffersFragmentDirections.actionOffersFragmentToBookingsFragment(it)
                findNavController().navigate(action)
            } ?: Toast.makeText(requireContext(), "This offer has no product attached", Toast.LENGTH_SHORT).show()
        }

        binding.swipeRefresh.setOnRefreshListener {
            binding.shimmerOffers.visibility = View.VISIBLE
            binding.shimmerOffers.startShimmer()
            binding.recyclerViewOffers.visibility = View.GONE
            binding.textError.visibility = View.GONE

            viewModel.fetchOffers()

            // stop the default spinner so only shimmer shows
            binding.swipeRefresh.isRefreshing = false
        }



        binding.recyclerViewOffers.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewOffers.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        is OffersViewModel.OffersUiState.Loading -> {
                            binding.shimmerOffers.startShimmer()
                            binding.shimmerOffers.visibility = View.VISIBLE
                            binding.recyclerViewOffers.visibility = View.GONE
                            binding.textError.visibility = View.GONE
                        }
                        is OffersViewModel.OffersUiState.Success -> {
                            binding.shimmerOffers.stopShimmer()
                            binding.shimmerOffers.visibility = View.GONE
                            binding.recyclerViewOffers.visibility = View.VISIBLE
                            binding.textError.visibility = View.GONE
                            adapter.submitList(state.offers)
                            Log.d("OffersFragment", "Offers loaded: ${state.offers?.size}")
                        }
                        is OffersViewModel.OffersUiState.Error -> {
                            binding.shimmerOffers.stopShimmer()
                            binding.shimmerOffers.visibility = View.GONE
                            binding.recyclerViewOffers.visibility = View.GONE
                            binding.textError.text = state.message
                            binding.textError.visibility = View.VISIBLE
                            Log.e("OffersFragment", "Error: ${state.message}")
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
