package com.example.myduit

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.myduit.core.ComposeApp
import com.example.myduit.data.repository.SettingsRepository
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        // Read language synchronously before Compose initializes
        val entryPoint = EntryPointAccessors.fromApplication(applicationContext, SettingsRepositoryEntryPoint::class.java)
        val language = entryPoint.settingsRepository.getLanguageSync()
        val config = Configuration(newBase.resources.configuration)
        config.setLocale(Locale(language))
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeApp()
        }
    }

    @dagger.hilt.EntryPoint
    @dagger.hilt.InstallIn(dagger.hilt.components.SingletonComponent::class)
    interface SettingsRepositoryEntryPoint {
        val settingsRepository: SettingsRepository
    }
}