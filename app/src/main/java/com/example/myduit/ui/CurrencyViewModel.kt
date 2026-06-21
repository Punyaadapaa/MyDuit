package com.example.myduit.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myduit.data.model.UiState
import com.example.myduit.data.repository.CurrencyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val repository: CurrencyRepository
) : ViewModel() {

    private val _rateState = MutableStateFlow<UiState<Map<String, Double>>>(UiState.Idle)
    val rateState: StateFlow<UiState<Map<String, Double>>> = _rateState.asStateFlow()

    fun loadRates() {
        viewModelScope.launch {
            _rateState.value = UiState.Loading
            try {
                val response = repository.getRate("USD")
                if (response.rates.isNotEmpty()) {
                    _rateState.value = UiState.Success(response.rates)
                } else {
                    _rateState.value = UiState.Error("Data kurs kosong")
                }
            } catch (e: Exception) {
                _rateState.value = UiState.Error("Gagal ambil kurs, cek koneksi")
            }
        }
    }
}
