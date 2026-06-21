package com.example.myduit.di

import android.content.Context
import androidx.room.Room
import com.example.myduit.data.local.AppDatabase
import com.example.myduit.data.local.TransactionDao
import com.example.myduit.data.remote.ApiConstants
import com.example.myduit.data.remote.CurrencyApiService
import com.example.myduit.data.repository.CurrencyRepository
import com.example.myduit.data.repository.TransactionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // ── Retrofit (Currency API) ─────────────────────────────────

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

    // ── Room (Transaction Database) ─────────────────────────────

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "myduit_database"
        )
            .addMigrations(AppDatabase.MIGRATION_1_2, AppDatabase.MIGRATION_2_3, AppDatabase.MIGRATION_3_4)
            .build()
    }

    @Provides
    fun provideCategoryDao(db: AppDatabase): com.example.myduit.data.local.CategoryDao {
        return db.categoryDao()
    }

    @Provides
    @Singleton
    fun provideCategoryRepository(dao: com.example.myduit.data.local.CategoryDao): com.example.myduit.data.repository.CategoryRepository {
        return com.example.myduit.data.repository.CategoryRepository(dao)
    }

    @Provides
    fun provideTransactionDao(db: AppDatabase): TransactionDao {
        return db.transactionDao()
    }

    @Provides
    @Singleton
    fun provideTransactionRepository(dao: TransactionDao): TransactionRepository {
        return TransactionRepository(dao)
    }

    @Provides
    fun provideBudgetDao(db: AppDatabase): com.example.myduit.data.local.BudgetDao {
        return db.budgetDao()
    }

    @Provides
    @Singleton
    fun provideBudgetRepository(dao: com.example.myduit.data.local.BudgetDao): com.example.myduit.data.repository.BudgetRepository {
        return com.example.myduit.data.repository.BudgetRepository(dao)
    }
}
