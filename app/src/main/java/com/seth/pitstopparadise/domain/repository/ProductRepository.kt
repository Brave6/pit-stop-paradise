package com.seth.pitstopparadise.domain.repository

import com.seth.pitstopparadise.domain.model.Product
import com.seth.pitstopparadise.retrofit.ApiService
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getProducts(): List<Product>? {
        val response = apiService.getProducts()
        return if (response.isSuccessful) response.body() else null
    }
}
