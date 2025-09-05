package com.seth.pitstopparadise.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seth.pitstopparadise.domain.model.Booking
import com.seth.pitstopparadise.domain.model.User
import com.seth.pitstopparadise.domain.repository.BookingRepository
import com.seth.pitstopparadise.domain.repository.UserRepository
import com.seth.pitstopparadise.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val bookingRepository: BookingRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _userInfo = MutableStateFlow<User?>(null)
    val userInfo: StateFlow<User?> = _userInfo.asStateFlow()

    private val _bookingHistory = MutableStateFlow<List<Booking>>(emptyList())
    val bookingHistory: StateFlow<List<Booking>> = _bookingHistory.asStateFlow()

    private val _logoutComplete = MutableStateFlow(false)
    val logoutComplete: StateFlow<Boolean> = _logoutComplete.asStateFlow()

    private val _sessionExpired = MutableStateFlow(false)
    val sessionExpired: StateFlow<Boolean> = _sessionExpired.asStateFlow()


    fun loadUserInfo() {
        viewModelScope.launch {
            val result = userRepository.getUserInfo()

            result.onSuccess { user ->
                _userInfo.value = user
            }.onFailure { e ->
                if (e.message?.contains("Session expired", ignoreCase = true) == true) {
                    sessionManager.clearToken()
                    _sessionExpired.value = true
                } else {
                    e.printStackTrace()
                }
            }
        }
    }

    fun loadBookingHistory() {
        viewModelScope.launch {
            try {
                val bookings = bookingRepository.getUserBookings()
                _bookingHistory.value = bookings
            } catch (e: Exception) {
                if (e.localizedMessage?.contains("401") == true || e.localizedMessage?.contains("Session expired") == true) {
                    sessionManager.clearToken()
                    _sessionExpired.value = true
                } else {
                    e.printStackTrace()
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                sessionManager.clearToken()
                _logoutComplete.value = true
            } catch (e: Exception) {
                _logoutComplete.value = false
                e.printStackTrace()
            }
        }
    }
}
