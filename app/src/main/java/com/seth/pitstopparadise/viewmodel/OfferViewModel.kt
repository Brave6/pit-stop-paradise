package com.seth.pitstopparadise.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seth.pitstopparadise.domain.model.Offer
import com.seth.pitstopparadise.domain.repository.OfferRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OffersViewModel @Inject constructor(
    private val offerRepository: OfferRepository
) : ViewModel() {

    private val _state = MutableStateFlow<OffersUiState>(OffersUiState.Loading)
    val state: StateFlow<OffersUiState> = _state.asStateFlow()

    init {
        fetchOffers()
    }

    fun fetchOffers() {
        viewModelScope.launch {
            try {
                val offers = offerRepository.getOffers()
                if (!offers.isNullOrEmpty()) {
                    _state.value = OffersUiState.Success(offers)
                } else {
                    _state.value = OffersUiState.Error("No offers available.")
                }
            } catch (e: Exception) {
                _state.value = OffersUiState.Error("Failed to load offers: ${e.localizedMessage}")
            }
        }
    }


    sealed class OffersUiState {
        object Loading : OffersUiState()
        data class Success(val offers: List<Offer>?) : OffersUiState()
        data class Error(val message: String) : OffersUiState()
    }
}
