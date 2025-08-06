package com.seth.pitstopparadise.domain.model

import com.google.gson.annotations.SerializedName


data class Booking(
    val _id: String,
    val name: String,
    val phone: String,
    val date: String,
    val time: String,
    val title: String,
    @SerializedName("productId") // <-- maps correctly
    val product: Product?
)


