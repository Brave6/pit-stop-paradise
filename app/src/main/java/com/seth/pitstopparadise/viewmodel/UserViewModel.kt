package com.seth.pitstopparadise.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seth.pitstopparadise.data.ProfileResponse
import com.seth.pitstopparadise.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    sealed class ProfileUiState {
        object Idle : ProfileUiState()
        object Loading : ProfileUiState()
        data class Success(val profile: ProfileResponse) : ProfileUiState()
        data class Error(val message: String) : ProfileUiState()
        object SessionExpired : ProfileUiState()
    }

    private val _profileState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val profileState: StateFlow<ProfileUiState> = _profileState

    fun fetchProfile() {
        viewModelScope.launch {
            _profileState.value = ProfileUiState.Loading

            val result = userRepository.getUserInfo()

            result.onSuccess { user ->
                _profileState.value = ProfileUiState.Success(
                    ProfileResponse(
                        _id = user.id,
                        email = user.email,
                        username = user.username
                    )
                )
            }.onFailure { error ->
                val message = error.message ?: "Unknown error"
                if (message.contains("Session expired", ignoreCase = true)) {
                    _profileState.value = ProfileUiState.SessionExpired
                } else {
                    _profileState.value = ProfileUiState.Error("Error: $message")
                }
            }
        }
    }

    fun resetState() {
        _profileState.value = ProfileUiState.Idle
    }
}
