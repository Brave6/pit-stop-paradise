package com.seth.pitstopparadise.retrofit

import com.seth.pitstopparadise.data.ApiResponse
import com.seth.pitstopparadise.data.BookingRequest
import com.seth.pitstopparadise.data.BookingResponse
import com.seth.pitstopparadise.data.LoginRequest
import com.seth.pitstopparadise.data.LoginResponse
import com.seth.pitstopparadise.data.ProfileResponse
import com.seth.pitstopparadise.data.RegisterRequest
import com.seth.pitstopparadise.domain.model.Booking
import com.seth.pitstopparadise.domain.model.Offer
import com.seth.pitstopparadise.domain.model.Product
import com.seth.pitstopparadise.domain.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("auth/profile")
    suspend fun getProfile(@Header("Authorization") token: String): Response<ProfileResponse>

    @GET("products")
    suspend fun getProducts(): Response<List<Product>>

    @POST("bookings")
    suspend fun createBooking(@Body request: BookingRequest): Response<BookingResponse>

    @GET("offers")
    suspend fun getOffers(): Response<List<Offer>>

    @GET("bookings")
    suspend fun getUserBookings(@Header("Authorization") token: String): Response<List<Booking>>

}