package com.example.myduit.data.remote

data class CurrencyResponse(
    val result: String,
    val base_code: String,
    val rates: Map<String, Double>
)
