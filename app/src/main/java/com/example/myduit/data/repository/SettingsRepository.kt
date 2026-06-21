package com.example.myduit.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsRepository @Inject constructor(@ApplicationContext private val context: Context) {

    private val dataStore = context.dataStore

    companion object {
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        val LANGUAGE = stringPreferencesKey("language") // "id" or "en"
        val IS_NOTIF_ENABLED = booleanPreferencesKey("is_notif_enabled")
    }

    val isDarkMode: Flow<Boolean?> = dataStore.data.map { preferences ->
        preferences[IS_DARK_MODE] 
    }

    val language: Flow<String> = dataStore.data.map { preferences ->
        preferences[LANGUAGE] ?: "id" // Default to Indonesian
    }

    val isNotifEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[IS_NOTIF_ENABLED] ?: true // Default to true
    }

    suspend fun setDarkMode(isDark: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_DARK_MODE] = isDark
        }
    }

    suspend fun setLanguage(lang: String) {
        dataStore.edit { preferences ->
            preferences[LANGUAGE] = lang
        }
    }

    suspend fun setNotifEnabled(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_NOTIF_ENABLED] = isEnabled
        }
    }
}
