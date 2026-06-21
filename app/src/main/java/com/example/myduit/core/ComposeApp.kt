package com.example.myduit.core

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.myduit.navigation.AddTransaction
import com.example.myduit.navigation.Currency
import com.example.myduit.navigation.Dashboard
import com.example.myduit.navigation.ExportReport
import com.example.myduit.navigation.LocalBackStack
import com.example.myduit.navigation.Login
import com.example.myduit.navigation.ManageCategories
import com.example.myduit.navigation.Profile
import com.example.myduit.navigation.Register
import com.example.myduit.navigation.Statistics
import com.example.myduit.navigation.TransactionDetail
import com.example.myduit.screens.AddTransactionScreen
import com.example.myduit.navigation.Report
import com.example.myduit.screens.CurrencyScreen
import com.example.myduit.screens.DashboardScreen
import com.example.myduit.screens.ExportScreen
import com.example.myduit.screens.LoginScreen
import com.example.myduit.screens.ManageCategoriesScreen
import com.example.myduit.screens.ProfileScreen
import com.example.myduit.screens.RegisterScreen
import com.example.myduit.screens.StatisticsScreen
import com.example.myduit.screens.ReportScreen
import com.example.myduit.screens.TransactionDetailScreen
import com.example.myduit.ui.TransactionViewModel
import com.example.myduit.ui.SettingsViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.util.Locale
import com.example.myduit.ui.theme.MyDuitTheme

@Composable
fun ComposeApp() {
    val backStack = rememberNavBackStack(Login)

    val transactionViewModel: TransactionViewModel = hiltViewModel()
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    
    val isDarkModePref by settingsViewModel.isDarkMode.collectAsState()
    val isSystemDark = androidx.compose.foundation.isSystemInDarkTheme()
    val isDarkMode = isDarkModePref ?: isSystemDark

    val language by settingsViewModel.language.collectAsState()
    
    // Set locale dynamically
    val configuration = LocalConfiguration.current
    val locale = Locale(language)
    configuration.setLocale(locale)

    CompositionLocalProvider(
        LocalBackStack provides backStack,
        LocalConfiguration provides configuration
    ) {
        MyDuitTheme(darkTheme = isDarkMode) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground
            ) {
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
                            DashboardScreen(
                                transactionViewModel = transactionViewModel
                            )
                        }
                        entry<TransactionDetail> {
                            TransactionDetailScreen(
                                transactionId = it.transactionId,
                                transactionViewModel = transactionViewModel
                            )
                        }
                        entry<Currency> {
                            CurrencyScreen()
                        }
                        entry<Register> {
                            RegisterScreen()
                        }
                        entry<AddTransaction> {
                            AddTransactionScreen(
                                transactionId = it.transactionId,
                                transactionViewModel = transactionViewModel
                            )
                        }
                        entry<Statistics> {
                            StatisticsScreen(transactionViewModel = transactionViewModel)
                        }
                        entry<ExportReport> {
                            ExportScreen(transactionViewModel = transactionViewModel)
                        }
                        entry<Profile> {
                            ProfileScreen(settingsViewModel = settingsViewModel)
                        }
                        entry<ManageCategories> {
                            ManageCategoriesScreen()
                        }
                        entry<Report> {
                            ReportScreen(transactionViewModel = transactionViewModel)
                        }
                    }
                )
            }
        }
    }
}