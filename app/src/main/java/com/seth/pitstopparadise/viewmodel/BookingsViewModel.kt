package com.seth.pitstopparadise.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seth.pitstopparadise.data.BookingRequest
import com.seth.pitstopparadise.domain.repository.BookingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookingsViewModel @Inject constructor(
    private val bookingRepository: BookingRepository
) : ViewModel() {

    sealed class BookingUiState {
        object Idle : BookingUiState()
        object Loading : BookingUiState()
        data class Success(val message: String) : BookingUiState()
        data class Error(val message: String) : BookingUiState()
    }

    private val _bookingState = MutableStateFlow<BookingUiState>(BookingUiState.Idle)
    val bookingState: StateFlow<BookingUiState> = _bookingState

    fun confirmBooking(name: String, phone: String, date: String, time: String, productId: String) {
        if (name.isBlank() || phone.isBlank() || date.isBlank() || time.isBlank()) {
            _bookingState.value = BookingUiState.Error("Please fill in all fields")
            return
        }

        viewModelScope.launch {
            _bookingState.value = BookingUiState.Loading
            try {
                val request = BookingRequest(name, phone, date, time, productId)
                bookingRepository.createBooking(request) // will throw if fails

                _bookingState.value = BookingUiState.Success("Booking created successfully")
            } catch (e: Exception) {
                _bookingState.value = BookingUiState.Error("Error: ${e.localizedMessage}")
            }
        }
    }

    fun resetState() {
        _bookingState.value = BookingUiState.Idle
    }
}

