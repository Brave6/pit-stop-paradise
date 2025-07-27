package com.seth.pitstopparadise.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seth.pitstopparadise.data.RegisterRequest
import com.seth.pitstopparadise.retrofit.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(

    private val apiService: ApiService

) : ViewModel() {

    private val _registerState = MutableStateFlow("")
    val registerState: StateFlow<String> = _registerState

    fun registerUser(request: RegisterRequest) {
        Log.d("RegisterViewModel", "registerUser called with: $request")

        viewModelScope.launch {
            try {
                val response = apiService.register(request)
                Log.d("RegisterViewModel", "API response: $response")

                if (response.isSuccessful) {
                    Log.d("RegisterViewModel", "Registration success")
                    _registerState.value = "Registered successfully"
                } else {
                    val errorMsg = response.errorBody()?.string() ?: response.message()
                    Log.e("RegisterViewModel", "Registration failed: $errorMsg")
                    _registerState.value = "Failed: $errorMsg"
                }
            } catch (e: Exception) {
                Log.e("RegisterViewModel", "Network error: ${e.message}", e)
                _registerState.value = "Network error: ${e.message}"
            }
        }
    }
}
