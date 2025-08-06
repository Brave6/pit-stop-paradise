package com.seth.pitstopparadise.ui.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.seth.pitstopparadise.R
import com.seth.pitstopparadise.databinding.FragmentBookingsBinding
import com.seth.pitstopparadise.viewmodel.BookingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class BookingsFragment : Fragment() {

    private var _binding: FragmentBookingsBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<BookingsFragmentArgs>()
    private val viewModel: BookingsViewModel by viewModels()

    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val product = args.product

        if (product != null) {
            // Display product info
            Glide.with(requireContext())
                .load(product.imageUrl)
                .into(binding.imageProduct)

            binding.textTitle.text = product.title
            if (product.discountedPrice != null) {
                val original = "₱${product.price}"
                val discounted = "₱${product.discountedPrice} / ${product.duration}"
                val styledText = "<s>$original</s> <b>$discounted</b>"
                binding.textPrice.text = HtmlCompat.fromHtml(styledText, HtmlCompat.FROM_HTML_MODE_LEGACY)
            } else {
                binding.textPrice.text = "₱${product.price} / ${product.duration}"
            }


            // Enable confirm booking
            binding.buttonConfirmBooking.isEnabled = true

            binding.buttonConfirmBooking.setOnClickListener {
                viewModel.confirmBooking(
                    name = binding.editName.text.toString(),
                    phone = binding.editPhone.text.toString(),
                    date = binding.editDate.text.toString(),
                    time = binding.editTimeSlot.text.toString(),
                    productId = product.id
                )
            }

        } else {
            // Handle the case where no product was passed
            binding.textTitle.text = "No product selected"
            binding.textPrice.text = ""

            Glide.with(requireContext())
                .load(R.drawable.pitstop_new) // Use a local placeholder drawable
                .into(binding.imageProduct)

            // Disable confirm booking
            binding.buttonConfirmBooking.isEnabled = false
        }

        // Set up date and time pickers (these can always be enabled)
        binding.editDate.setOnClickListener { showDatePicker() }
        binding.editTimeSlot.setOnClickListener { showTimePicker() }

        lifecycleScope.launchWhenStarted {
            viewModel.bookingState.collect { state ->
                when (state) {
                    is BookingsViewModel.BookingUiState.Loading -> {
                        binding.buttonConfirmBooking.isEnabled = false
                        binding.buttonConfirmBooking.text = "Booking..."
                    }

                    is BookingsViewModel.BookingUiState.Success -> {
                        binding.buttonConfirmBooking.isEnabled = true
                        binding.buttonConfirmBooking.text = "Confirm Booking"
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack() // This returns to HomeFragment
                        //  clearBookingForm() // clear
                        viewModel.resetState()
                    }

                    is BookingsViewModel.BookingUiState.Error -> {
                        binding.buttonConfirmBooking.isEnabled = true
                        binding.buttonConfirmBooking.text = "Confirm Booking"
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        viewModel.resetState()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun showDatePicker() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), { _, y, m, d ->
            binding.editDate.setText("$y-${m + 1}-$d")
        }, year, month, day).show()
    }

    private fun showTimePicker() {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(requireContext(), { _, h, m ->
            val isPM = h >= 12
            val hourFormatted = if (h % 12 == 0) 12 else h % 12
            val amPm = if (isPM) "PM" else "AM"
            val timeFormatted = String.format("%02d:%02d %s", hourFormatted, m, amPm)

            binding.editTimeSlot.setText(timeFormatted)
        }, hour, minute, false).show() // false = 12-hour format
    }

    private fun clearBookingForm() {
        binding.editName.text?.clear()
        binding.editPhone.text?.clear()
        binding.editDate.text?.clear()
        binding.editTimeSlot.text?.clear()

        // Optional: Clear product info
        binding.textTitle.text = ""
        binding.textPrice.text = ""
        Glide.with(requireContext())
            .load(R.drawable.pitstop_new) // fallback image
            .into(binding.imageProduct)

        binding.buttonConfirmBooking.isEnabled = false
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
