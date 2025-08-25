package com.seth.pitstopparadise.domain.repository

import com.seth.pitstopparadise.data.BookingRequest
import com.seth.pitstopparadise.data.BookingResponse
import com.seth.pitstopparadise.domain.model.Booking
import com.seth.pitstopparadise.retrofit.ApiService
import com.seth.pitstopparadise.utils.SessionManager
import kotlinx.coroutines.flow.first
import retrofit2.Response
import javax.inject.Inject

class BookingRepository @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) {
    suspend fun createBooking(request: BookingRequest): Response<BookingResponse> {
        val token = sessionManager.token.first() ?: throw Exception("No token found")
        return apiService.createBooking("Bearer $token", request)
    }

    suspend fun getUserBookings(): List<Booking> {
        val token = sessionManager.token.first() ?: throw Exception("No token found")
        val response = apiService.getUserBookings("Bearer $token")
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            throw Exception("Failed to load bookings: ${response.code()}")
        }
    }
}
