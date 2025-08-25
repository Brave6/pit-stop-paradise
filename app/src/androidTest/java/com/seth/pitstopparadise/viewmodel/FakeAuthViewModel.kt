package com.seth.pitstopparadise.viewmodel

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

class FakeAuthViewModel {

    private val _uiMessage = MutableSharedFlow<String>()
    val uiMessage: SharedFlow<String> = _uiMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun login(email: String, password: String) {
        _isLoading.value = true

        if (email == "test@email.com" && password == "password123") {
            _uiMessage.tryEmit("Login success!")
        } else {
            _uiMessage.tryEmit("Login failed")
        }

        _isLoading.value = false
    }
}

