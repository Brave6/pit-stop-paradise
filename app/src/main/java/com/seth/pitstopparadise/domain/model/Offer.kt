package com.seth.pitstopparadise.domain.model

data class Offer(
    val id: String,
    val title: String,
    val description: String,
    val discountPercent: Int,
    val validUntil: String,
    val product: Product? // full product details for navigation

)
