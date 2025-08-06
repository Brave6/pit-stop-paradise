package com.seth.pitstopparadise.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.seth.pitstopparadise.databinding.FragmentOffersBinding
import com.seth.pitstopparadise.ui.adapter.OfferAdapter
import com.seth.pitstopparadise.viewmodel.OffersViewModel
import dagger.hilt.android.AndroidEntryPoint

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


        binding.recyclerViewOffers.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewOffers.adapter = adapter

        lifecycleScope.launchWhenStarted {
            viewModel.state.collect { state ->
                when (state) {
                    is OffersViewModel.OffersUiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.textError.visibility = View.GONE
                    }
                    is OffersViewModel.OffersUiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.textError.visibility = View.GONE
                        adapter.submitList(state.offers)
                        Log.d("OffersFragment", "Offers loaded: ${state.offers?.size}")
                    }
                    is OffersViewModel.OffersUiState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.textError.text = state.message
                        binding.textError.visibility = View.VISIBLE
                        Log.e("OffersFragment", "Error: ${state.message}")
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
