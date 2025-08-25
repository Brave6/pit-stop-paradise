package com.seth.pitstopparadise.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seth.pitstopparadise.utils.SessionManager
import com.seth.pitstopparadise.data.LoginRequest
import com.seth.pitstopparadise.retrofit.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class AuthViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val apiService: ApiService
) : ViewModel() {

    private val _uiMessage = MutableSharedFlow<String>()
    val uiMessage: SharedFlow<String> = _uiMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val TAG = "AuthViewModel"

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            Log.d(TAG, "login called with: email=$email, password=$password")
            try {
                val request = LoginRequest(email, password)
                val response = apiService.login(request) // ✅ Use injected API service
                Log.d(TAG, "API response: $response")

                if (response.isSuccessful) {
                    val token = response.body()?.token.orEmpty()
                    Log.d(TAG, "Login successful. Token: $token")
                    sessionManager.saveToken(token)
                    _uiMessage.emit("Login success!")
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "Login failed: code=${response.code()}, error=$errorBody")
                    _uiMessage.emit("Login failed: ${errorBody ?: response.message()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception during login", e)
                _uiMessage.emit("Network error: ${e.localizedMessage ?: "Unknown error"}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getToken(): Flow<String?> = sessionManager.token
}
