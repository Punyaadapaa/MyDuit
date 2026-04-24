package com.example.myduit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.myduit.navigation.*
import com.example.myduit.screens.*
import com.example.myduit.ui.theme.MyDuitTheme
import java.util.UUID

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val amount: Double,
    val isIncome: Boolean,
    val date: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyDuitTheme {
                val backStack = rememberNavBackStack(Login)
                val transactions = remember { mutableStateListOf<Transaction>() }

                CompositionLocalProvider(LocalBackStack provides backStack) {
                    NavDisplay(
                        backStack = backStack,
                        onBack = { backStack.removeLastOrNull() },
                        entryProvider = { key ->
                            when (key) {
                                is Login -> NavEntry(key) { LoginScreen() }
                                is Dashboard -> NavEntry(key) {
                                    DashboardScreen(transactions) { transactions.add(it) }
                                }
                                is TransactionDetail -> NavEntry(key) {
                                    val tx = transactions.find { it.id == key.transactionId }
                                    if (tx != null) TransactionDetailScreen(tx)
                                }
                                else -> NavEntry(key) {}
                            }
                        }
                    )
                }
            }
        }
    }
}