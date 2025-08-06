package com.seth.pitstopparadise.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    @SerializedName("_id")
    val id: String,

    @SerializedName("title")
    val title: String,

    val price: Int,

    val duration: String,

    val description: String,

    val imageUrl: String,

    @SerializedName("discountedPrice")
    val discountedPrice: Int? = null // <-- safe, optional field
) : Parcelable

