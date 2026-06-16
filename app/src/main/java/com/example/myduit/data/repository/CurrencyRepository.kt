package com.example.myduit.data.repository

import com.example.myduit.data.remote.CurrencyApiService
import com.example.myduit.data.remote.CurrencyResponse
import javax.inject.Inject

class CurrencyRepository @Inject constructor(
    private val apiService: CurrencyApiService
) {
    suspend fun getRate(base: String): CurrencyResponse {
        return apiService.getRates(base)
    }
}
