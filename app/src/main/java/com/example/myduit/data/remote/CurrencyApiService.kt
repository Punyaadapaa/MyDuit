package com.example.myduit.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface CurrencyApiService {
    @GET("v6/latest/{base}")
    suspend fun getRates(@Path("base") base: String): CurrencyResponse
}
