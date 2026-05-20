package com.example.myduit.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Instance DataStore di level Context (singleton)
val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferencesDataStore(private val context: Context) {

    companion object {
        val USERNAME_KEY = stringPreferencesKey("username")
    }

    // Membaca username sebagai Flow — otomatis update jika berubah
    val usernameFlow: Flow<String> = context.userDataStore.data
        .map { preferences ->
            preferences[USERNAME_KEY] ?: ""
        }

    // Menyimpan username secara asynchronous (suspend function)
    suspend fun saveUsername(username: String) {
        context.userDataStore.edit { preferences ->
            preferences[USERNAME_KEY] = username
        }
    }

    // Menghapus username saat logout
    suspend fun clearUsername() {
        context.userDataStore.edit { preferences ->
            preferences.remove(USERNAME_KEY)
        }
    }
}