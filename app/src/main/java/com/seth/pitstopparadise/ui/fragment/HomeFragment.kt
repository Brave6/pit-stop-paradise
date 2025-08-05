package com.seth.pitstopparadise.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.seth.pitstopparadise.databinding.FragmentHomeBinding
import com.seth.pitstopparadise.ui.adapter.ProductAdapter
import com.seth.pitstopparadise.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProductViewModel by viewModels()
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCarousel()

        productAdapter = ProductAdapter { product ->
            val action = HomeFragmentDirections.actionNavHomeToNavBookings(product)
            findNavController().navigate(action)
        }



        binding.productRecyclerView.adapter = productAdapter

        // Load data
        viewModel.fetchProducts()

        // Observe state
        lifecycleScope.launchWhenStarted {
            viewModel.products.collect { products ->
                productAdapter.submitList(products)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupCarousel() {
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.productRecyclerView.layoutManager = layoutManager

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.productRecyclerView)

        var currentPosition = 0

        binding.btnLeft.setOnClickListener {
            currentPosition = (currentPosition - 1).coerceAtLeast(0)
            binding.productRecyclerView.smoothScrollToPosition(currentPosition)
        }

        binding.btnRight.setOnClickListener {
            currentPosition = (currentPosition + 1).coerceAtMost(productAdapter.itemCount - 1)
            binding.productRecyclerView.smoothScrollToPosition(currentPosition)
        }

        // Safe coroutine tied to view lifecycle
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                while (true) {
                    kotlinx.coroutines.delay(3000)
                    if (productAdapter.itemCount > 0) {
                        currentPosition = (currentPosition + 1) % productAdapter.itemCount
                        binding.productRecyclerView.smoothScrollToPosition(currentPosition)
                    }
                }
            }
        }
    }



}
