package com.seth.pitstopparadise.domain.repository

import com.seth.pitstopparadise.domain.model.Offer
import com.seth.pitstopparadise.retrofit.ApiService
import javax.inject.Inject

class OfferRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getOffers(): List<Offer>? {
        val response = apiService.getOffers()
        return if (response.isSuccessful) response.body() else null
    }
}
