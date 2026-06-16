package com.example.myduit.ui

import androidx.lifecycle.ViewModel
import com.example.myduit.data.UserPreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val dataStore: UserPreferencesDataStore
) : ViewModel() {

    val usernameFlow: Flow<String> = dataStore.usernameFlow

    suspend fun saveUsername(username: String) {
        dataStore.saveUsername(username)
    }

    suspend fun clearUsername() {
        dataStore.clearUsername()
    }
}
