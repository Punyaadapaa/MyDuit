package com.example.myduit.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

// Instance DataStore di level Context (singleton)
val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferencesDataStore(private val context: Context) {

    companion object {
        val USERNAME_KEY = stringPreferencesKey("username")
        val PIN_HASH_KEY = stringPreferencesKey("pin_hash")
        val PIN_SALT_KEY = stringPreferencesKey("pin_salt")
        private const val PBKDF2_ITERATIONS = 100_000
        private const val KEY_LENGTH = 256
        private const val SALT_LENGTH = 16
    }

    // Membaca username sebagai Flow — otomatis update jika berubah
    val usernameFlow: Flow<String> = context.userDataStore.data
        .map { preferences ->
            preferences[USERNAME_KEY] ?: ""
        }

    // PIN tidak diekspos sebagai Flow — hanya verifikasi
    suspend fun verifyPin(inputPin: String): Boolean {
        val prefs = context.userDataStore.data.first()
        val storedHash = prefs[PIN_HASH_KEY] ?: return false
        val storedSalt = prefs[PIN_SALT_KEY] ?: return false
        val computedHash = hashPin(inputPin, storedSalt)
        return MessageDigest.isEqual(hexToBytes(storedHash), hexToBytes(computedHash))
    }

    // Menyimpan username secara asynchronous (suspend function)
    suspend fun saveUsername(username: String) {
        context.userDataStore.edit { preferences ->
            preferences[USERNAME_KEY] = username
        }
    }

    suspend fun savePin(pin: String) {
        val salt = generateSalt()
        val hash = hashPin(pin, salt)
        context.userDataStore.edit { preferences ->
            preferences[PIN_HASH_KEY] = hash
            preferences[PIN_SALT_KEY] = salt
        }
    }

    // Menghapus kredensial saat logout
    suspend fun clearCredentials() {
        context.userDataStore.edit { preferences ->
            preferences.remove(USERNAME_KEY)
            preferences.remove(PIN_HASH_KEY)
            preferences.remove(PIN_SALT_KEY)
        }
    }

    private fun generateSalt(): String {
        val salt = ByteArray(SALT_LENGTH)
        SecureRandom().nextBytes(salt)
        return salt.joinToString("") { "%02x".format(it) }
    }

    private fun hashPin(pin: String, saltHex: String): String {
        val salt = hexToBytes(saltHex)
        val spec = PBEKeySpec(pin.toCharArray(), salt, PBKDF2_ITERATIONS, KEY_LENGTH)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val hash = factory.generateSecret(spec).encoded
        return hash.joinToString("") { "%02x".format(it) }
    }

    private fun hexToBytes(hex: String): ByteArray {
        return hex.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
    }
}