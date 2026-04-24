package com.example.myduit.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.myduit.Transaction
import com.example.myduit.navigation.LocalBackStack
import com.example.myduit.navigation.Login
import com.example.myduit.navigation.Dashboard
import com.example.myduit.navigation.TransactionDetail
import com.example.myduit.screens.DashboardScreen
import com.example.myduit.screens.LoginScreen
import com.example.myduit.screens.TransactionDetailScreen
import com.example.myduit.ui.theme.MyDuitTheme

@Composable
fun ComposeApp() {
    val backStack = rememberNavBackStack(Login)
    val transactions = remember { mutableStateListOf<Transaction>() }

    CompositionLocalProvider(LocalBackStack provides backStack) {
        MyDuitTheme {
            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator()
                ),
                entryProvider = entryProvider {
                    entry<Login> {
                        LoginScreen()
                    }
                    entry<Dashboard> {
                        DashboardScreen(transactions) { transactions.add(it) }
                    }
                    entry<TransactionDetail> {
                        val tx = transactions.find { t -> t.id == it.transactionId }
                        if (tx != null) TransactionDetailScreen(tx)
                    }
                }
            )
        }
    }
}