package com.example.myduit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.myduit.core.ComposeApp
import com.example.myduit.data.UserPreferencesDataStore
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID
import javax.inject.Inject

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val amount: Double,
    val isIncome: Boolean,
    val date: String
)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPreferencesDataStore: UserPreferencesDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeApp(userPreferencesDataStore = userPreferencesDataStore)
        }
    }
}