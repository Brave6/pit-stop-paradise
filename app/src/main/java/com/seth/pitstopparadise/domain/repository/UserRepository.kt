package com.seth.pitstopparadise.domain.repository

import com.seth.pitstopparadise.domain.model.User
import com.seth.pitstopparadise.retrofit.ApiService
import com.seth.pitstopparadise.utils.SessionManager
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) {
    suspend fun getUserInfo(): Result<User> {
        return try {
            val token = sessionManager.token.first() ?: return Result.failure(Exception("No token found"))

            val response = apiService.getProfile("Bearer $token")

            if (response.isSuccessful) {
                val profile = response.body() ?: return Result.failure(Exception("Empty profile"))
                Result.success(
                    User(
                        id = profile._id,
                        username = profile.username,
                        email = profile.email
                    )
                )
            } else {
                if (response.code() == 401) {
                    sessionManager.clearToken()
                    Result.failure(Exception("Session expired. Please log in again."))
                } else {
                    Result.failure(Exception("Failed to load profile: ${response.code()}"))
                }
            }
        } catch (e: SocketTimeoutException) {
            Result.failure(Exception("Server is taking too long to respond. Please try again."))
        } catch (e: IOException) {
            Result.failure(Exception("Network error. Please check your connection."))
        } catch (e: HttpException) {
            Result.failure(Exception("Unexpected server error: ${e.message()}"))
        } catch (e: Exception) {
            Result.failure(Exception("Unexpected error: ${e.message}"))
        }
    }
}
