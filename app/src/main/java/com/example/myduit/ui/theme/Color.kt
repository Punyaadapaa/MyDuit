package com.example.myduit.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PrimaryLight = Color(0xFF1d2b3e)
val OnPrimaryLight = Color(0xFFffffff)
val PrimaryContainerLight = Color(0xFF334155)
val OnPrimaryContainerLight = Color(0xFF9eadc5)
val SecondaryLight = Color(0xFF006780)
val OnSecondaryLight = Color(0xFFffffff)
val SecondaryContainerLight = Color(0xFF76dcff)
val OnSecondaryContainerLight = Color(0xFF006077)
val TertiaryLight = Color(0xFF003212)
val OnTertiaryLight = Color(0xFFffffff)
val TertiaryContainerLight = Color(0xFF004b1f)
val OnTertiaryContainerLight = Color(0xFF5ec074)
val ErrorLight = Color(0xFFba1a1a)
val OnErrorLight = Color(0xFFffffff)
val ErrorContainerLight = Color(0xFFffdad6)
val OnErrorContainerLight = Color(0xFF93000a)
val BackgroundLight = Color(0xFFf7f9fb)
val OnBackgroundLight = Color(0xFF191c1e)
val SurfaceLight = Color(0xFFf7f9fb)
val OnSurfaceLight = Color(0xFF191c1e)
val SurfaceVariantLight = Color(0xFFe0e3e5)
val OnSurfaceVariantLight = Color(0xFF44474c)
val OutlineLight = Color(0xFF75777d)

val PrimaryDark = Color(0xFF92ccff)
val OnPrimaryDark = Color(0xFF003351)
val PrimaryContainerDark = Color(0xFF004b73)
val OnPrimaryContainerDark = Color(0xFFcce5ff)
val SecondaryDark = Color(0xFFb4cad6)
val OnSecondaryDark = Color(0xFF1e333d)
val SecondaryContainerDark = Color(0xFF354a55)
val OnSecondaryContainerDark = Color(0xFFd0e6f2)
val TertiaryDark = Color(0xFFbac8ea)
val OnTertiaryDark = Color(0xFF24314d)
val TertiaryContainerDark = Color(0xFF3b4865)
val OnTertiaryContainerDark = Color(0xFFdae2ff)
val ErrorDark = Color(0xFFffb4ab)
val OnErrorDark = Color(0xFF690005)
val ErrorContainerDark = Color(0xFF93000a)
val OnErrorContainerDark = Color(0xFFffdad6)
val BackgroundDark = Color(0xFF000000) // Pitch black background
val OnBackgroundDark = Color(0xFFe0e3e5)
val SurfaceDark = Color(0xFF191c1e) // Slightly lighter surface for cards
val OnSurfaceDark = Color(0xFFe0e3e5)
val SurfaceVariantDark = Color(0xFF1d2022) // Adjusted surface container
val OnSurfaceVariantDark = Color(0xFFc1c7ce)
val OutlineDark = Color(0xFF8b9198)

// Custom Semantic Colors
val IncomeGreenLight = Color(0xFF15803D)
val IncomeBgLight = Color(0xFFD1FAE5)
val ExpenseRedLight = Color(0xFFDC2626)
val ExpenseBgLight = Color(0xFFFEE2E2)

val IncomeGreenDark = Color(0xFF22c55e)
val IncomeBgDark = Color(0xFF064e3b)
val ExpenseRedDark = Color(0xFFef4444)
val ExpenseBgDark = Color(0xFF7f1d1d)

@Composable
fun getIncomeGreen(): Color = if (LocalDarkTheme.current) IncomeGreenDark else IncomeGreenLight

@Composable
fun getIncomeBg(): Color = if (LocalDarkTheme.current) IncomeBgDark else IncomeBgLight

@Composable
fun getExpenseRed(): Color = if (LocalDarkTheme.current) ExpenseRedDark else ExpenseRedLight

@Composable
fun getExpenseBg(): Color = if (LocalDarkTheme.current) ExpenseBgDark else ExpenseBgLight
