package com.example.myduit.di

import com.example.myduit.data.remote.ApiConstants
import com.example.myduit.data.remote.CurrencyApiService
import com.example.myduit.data.repository.CurrencyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiConstants.CURRENCY_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideCurrencyApiService(retrofit: Retrofit): CurrencyApiService {
        return retrofit.create(CurrencyApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCurrencyRepository(apiService: CurrencyApiService): CurrencyRepository {
        return CurrencyRepository(apiService)
    }
}
