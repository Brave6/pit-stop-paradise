package com.seth.pitstopparadise.domain.repository

import com.seth.pitstopparadise.domain.model.User
import com.seth.pitstopparadise.retrofit.ApiService
import com.seth.pitstopparadise.utils.SessionManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) {
    suspend fun getUserInfo(): User {
        val token = sessionManager.token.first() ?: throw Exception("No token found")
        val response = apiService.getProfile("Bearer $token")
        if (response.isSuccessful) {
            val profile = response.body() ?: throw Exception("Empty profile")
            return User(
                id = profile._id,
                username = profile.username,
                email = profile.email
            )
        } else {
            throw Exception("Failed to load user profile: ${response.code()}")
        }
    }
}
