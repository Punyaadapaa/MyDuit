package com.example.myduit.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface AppRoute : NavKey

@Serializable
object Login : AppRoute

@Serializable
object Dashboard : AppRoute

@Serializable
data class TransactionDetail(val transactionId: String) : AppRoute