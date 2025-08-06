package com.seth.pitstopparadise.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.seth.pitstopparadise.LoginActivity
import com.seth.pitstopparadise.R
import com.seth.pitstopparadise.databinding.FragmentProfileBinding
import com.seth.pitstopparadise.ui.adapter.BookingHistoryAdapter
import com.seth.pitstopparadise.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    private lateinit var bookingAdapter: BookingHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bookingAdapter = BookingHistoryAdapter()
        binding.recyclerBookingHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerBookingHistory.adapter = bookingAdapter

        observeState()

        binding.buttonLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        // Trigger initial load
        viewModel.loadUserInfo()
        viewModel.loadBookingHistory()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.userInfo.collectLatest { user ->
                        user?.let {
                            binding.textUserName.text = it.username
                            binding.textEmail.text = it.email
                        }
                    }
                }

                launch {
                    viewModel.bookingHistory.collectLatest { bookings ->
                        bookingAdapter.submitList(bookings)
                    }
                }

                launch {
                    viewModel.logoutComplete.collectLatest { isComplete ->
                        if (isComplete) {
                            val intent = Intent(requireContext(), LoginActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                            startActivity(intent)
                            requireActivity().finish()
                        }
                    }
                }
            }
        }
    }

    private fun showLogoutConfirmationDialog() {
        val dialogView = layoutInflater.inflate(R.layout.logout_alert, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.setOnShowListener {
            hideSystemUI()
        }

        val proceedButton = dialogView.findViewById<MaterialButton>(R.id.proceedButton)
        val cancelButton = dialogView.findViewById<MaterialButton>(R.id.noButton)

        proceedButton.setOnClickListener {
            viewModel.logout()
            dialog.dismiss()
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun hideSystemUI() {
        requireActivity().window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
