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
class AuthViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val apiService: ApiService
) : ViewModel() {

    private val TAG = "AuthViewModel"

    private val _loginState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginUiState.Loading
            Log.d(TAG, "login called with: email=$email, password=$password")
            try {
                val request = LoginRequest(email, password)
                val response = apiService.login(request)
                Log.d(TAG, "API response: $response")

                if (response.isSuccessful) {
                    val token = response.body()?.token.orEmpty()

                    Log.d(TAG, "Login successful. Token: $token")
                    sessionManager.saveToken(token)
                    _loginState.value = LoginUiState.Success // ✅ no email/username
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = "Login failed: code=${response.code()}, message=${errorBody ?: response.message()}"
                    Log.e(TAG, errorMessage)
                    _loginState.value = LoginUiState.Error(errorMessage)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception during login", e)
                _loginState.value = LoginUiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun getToken(): Flow<String?> = sessionManager.token
}

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    object Success : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}
