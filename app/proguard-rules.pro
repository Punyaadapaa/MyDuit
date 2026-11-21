# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# ── Kotlin / Coroutines ──────────────────────────────────────────
-keep class kotlin.coroutines.** { *; }
-keep class kotlinx.coroutines.** { *; }
-keepattributes *Annotation*
-keepattributes InnerClasses
-keepattributes EnclosingMethod
-keepattributes Signature

# ── Retrofit + Gson ──────────────────────────────────────────────
# Keep all data classes in data.remote package (Gson uses reflection)
-keep class com.example.myduit.data.remote.** { *; }

# Gson internals
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }

# ── Room ─────────────────────────────────────────────────────────
-keep class com.example.myduit.data.local.** { *; }
-keep class com.example.myduit.data.model.** { *; }
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Database class *
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *
-keep class * implements androidx.room.Migration { *; }
-dontwarn androidx.room.**

# ── Hilt / Dagger ────────────────────────────────────────────────
-keep class dagger.** { *; }
-keep class * extends dagger.hilt.android.HiltAndroidApp
-keep class * extends dagger.hilt.android.AndroidEntryPoint
-keep class * extends dagger.hilt.EntryPoint
-keep class * extends dagger.hilt.InstallIn
-keep class * extends dagger.Module
-keep class * extends dagger.hilt.components.SingletonComponent
-keepattributes *Annotation*
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations
-dontwarn dagger.hilt.**

# ── DataStore ────────────────────────────────────────────────────
-keep class androidx.datastore.** { *; }
-keep class com.example.myduit.data.UserPreferencesDataStore { *; }
-keep class com.example.myduit.data.repository.SettingsRepository { *; }

# ── Navigation 3 ─────────────────────────────────────────────────
-keep class androidx.navigation3.** { *; }
-keep class com.example.myduit.navigation.** { *; }
-dontwarn androidx.navigation3.**

# ── Compose ──────────────────────────────────────────────────────
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# ── Serialization ────────────────────────────────────────────────
-keep class kotlinx.serialization.** { *; }
-keep class com.example.myduit.navigation.** { *; }

# ── App classes ──────────────────────────────────────────────────
-keep class com.example.myduit.MyApplication { *; }
-keep class com.example.myduit.MainActivity { *; }
-keep class com.example.myduit.core.ComposeApp { *; }
-keep class com.example.myduit.ui.** { *; }
-keep class com.example.myduit.screens.** { *; }
-keep class com.example.myduit.data.** { *; }
-keep class com.example.myduit.di.** { *; }

# ── OkHttp / Okio ────────────────────────────────────────────────
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep class okio.** { *; }