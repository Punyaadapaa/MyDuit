package com.example.myduit.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.example.myduit.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val fontHankenGrotesk = GoogleFont("Hanken Grotesk")
val HankenGroteskFamily = FontFamily(
    Font(googleFont = fontHankenGrotesk, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = fontHankenGrotesk, fontProvider = provider, weight = FontWeight.Bold)
)

val fontInter = GoogleFont("Inter")
val InterFamily = FontFamily(
    Font(googleFont = fontInter, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = fontInter, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = fontInter, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = fontInter, fontProvider = provider, weight = FontWeight.Bold)
)

val fontJetBrainsMono = GoogleFont("JetBrains Mono")
val JetBrainsMonoFamily = FontFamily(
    Font(googleFont = fontJetBrainsMono, fontProvider = provider, weight = FontWeight.Bold)
)

// Set of Material typography styles to start with
val Typography = Typography(
    // body-base from Figma
    bodyLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    // body-sm from Figma
    bodyMedium = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    // display-balance from Figma
    displayLarge = TextStyle(
        fontFamily = HankenGroteskFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.02).sp
    ),
    // headline-section from Figma
    headlineMedium = TextStyle(
        fontFamily = HankenGroteskFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 28.sp
    ),
    // title-item from Figma
    titleMedium = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.SemiBold, // 600
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    // stat-label from Figma
    labelMedium = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Medium, // 500
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    // label-caps from Figma
    labelSmall = TextStyle(
        fontFamily = JetBrainsMonoFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.05.sp
    )
)