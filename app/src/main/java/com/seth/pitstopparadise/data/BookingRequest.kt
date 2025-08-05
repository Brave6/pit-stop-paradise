package com.seth.pitstopparadise.data

data class BookingRequest(
    val name: String,
    val phone: String,
    val date: String,
    val time: String,
    val productId: String
)

data class BookingResponse(
    val message: String
)