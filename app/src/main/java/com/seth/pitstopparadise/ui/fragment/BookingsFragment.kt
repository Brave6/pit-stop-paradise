package com.seth.pitstopparadise.ui.fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.seth.pitstopparadise.R
import com.seth.pitstopparadise.databinding.FragmentBookingsBinding
import com.seth.pitstopparadise.viewmodel.BookingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
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

        val params = binding.bottomSpacer.layoutParams
        params.height = 150 // or calculate based on keyboard height
        binding.bottomSpacer.layoutParams = params

        binding.editPhone.setOnFocusChangeListener { _, hasFocus ->
            binding.bottomSpacer.visibility = if (hasFocus) View.VISIBLE else View.GONE
        }
        if (product != null) {
            // Display product info
            Glide.with(requireContext())
                .load(product.imageUrl)
                .into(binding.imageProduct)

            binding.textTitle.text = product.title

            if (product.discountedPrice != null) {
                val original = getString(R.string.price_currency, product.price)
                val discounted = getString(
                    R.string.price_currency_with_duration,
                    product.discountedPrice,
                    product.duration
                )
                val styledText = "<s>$original</s> <b>$discounted</b>"
                binding.textPrice.text =
                    HtmlCompat.fromHtml(styledText, HtmlCompat.FROM_HTML_MODE_LEGACY)
            } else {
                binding.textPrice.text = getString(
                    R.string.price_currency_with_duration,
                    product.price,
                    product.duration
                )
            }

            // Enable confirm booking when fields are valid
            binding.buttonConfirmBooking.isEnabled = false
            setupFieldValidation()

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
            // Handle missing product
            binding.textTitle.text = getString(R.string.no_product_selected)
            binding.textPrice.text = ""
            Glide.with(requireContext())
                .load(R.drawable.pitstop_new)
                .into(binding.imageProduct)

            binding.buttonConfirmBooking.isEnabled = false
        }

        // Set up date & time pickers
        binding.editDate.setOnClickListener { showDatePicker() }
        binding.editTimeSlot.setOnClickListener { showTimePicker() }

        // Observe ViewModel state safely with repeatOnLifecycle
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.bookingState.collect { state ->
                    when (state) {
                        is BookingsViewModel.BookingUiState.Loading -> {
                            binding.buttonConfirmBooking.isEnabled = false
                            binding.buttonConfirmBooking.text = getString(R.string.booking_in_progress)
                        }
                        is BookingsViewModel.BookingUiState.Success -> {
                            binding.buttonConfirmBooking.isEnabled = true
                            binding.buttonConfirmBooking.text = getString(R.string.confirm_booking)

                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                            announceMessage(state.message)

                            findNavController().popBackStack()
                            clearBookingForm()
                            viewModel.resetState()
                        }
                        is BookingsViewModel.BookingUiState.Error -> {
                            binding.buttonConfirmBooking.isEnabled = true
                            binding.buttonConfirmBooking.text = getString(R.string.confirm_booking)

                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                            announceMessage(state.message)

                            viewModel.resetState()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun setupFieldValidation() {
        val fields = listOf(
            binding.editName,
            binding.editPhone,
            binding.editDate,
            binding.editTimeSlot
        )

        fields.forEach { editText ->
            editText.doOnTextChanged { _, _, _, _ ->
                binding.buttonConfirmBooking.isEnabled = fields.all { it.text?.isNotEmpty() == true }
            }
        }
    }

    private fun showDatePicker() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), { _, y, m, d ->
            binding.editDate.setText(getString(R.string.date_format, y, m + 1, d))
        }, year, month, day).show()
    }

    @SuppressLint("DefaultLocale")
    private fun showTimePicker() {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(requireContext(), { _, h, m ->
            val isPM = h >= 12
            val hourFormatted = if (h % 12 == 0) 12 else h % 12
            val amPm = if (isPM) "PM" else "AM"
            val timeFormatted = String.format("%02d:%02d %s", hourFormatted, m, amPm)

            binding.editTimeSlot.setText(timeFormatted)
        }, hour, minute, false).show()
    }

    private fun clearBookingForm() {
        binding.editName.text?.clear()
        binding.editPhone.text?.clear()
        binding.editDate.text?.clear()
        binding.editTimeSlot.text?.clear()
        binding.buttonConfirmBooking.isEnabled = false
    }

    private fun announceMessage(message: String) {
        binding.root.announceForAccessibility(message)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
