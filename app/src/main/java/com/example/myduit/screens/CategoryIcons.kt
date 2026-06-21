package com.example.myduit.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

val availableCategoryIcons = listOf(
    "Restaurant" to Icons.Default.Restaurant,
    "Commute" to Icons.Default.Commute,
    "ShoppingBag" to Icons.Default.ShoppingBag,
    "ReceiptLong" to Icons.Default.ReceiptLong,
    "Payments" to Icons.Default.Payments,
    "SportsEsports" to Icons.Default.SportsEsports,
    "MedicalServices" to Icons.Default.MedicalServices,
    "School" to Icons.Default.School,
    "Category" to Icons.Default.Category,
    "Favorite" to Icons.Default.Favorite,
    "Home" to Icons.Default.Home,
    "Build" to Icons.Default.Build,
    "Flight" to Icons.Default.Flight,
    "Pets" to Icons.Default.Pets
)

fun getIconByName(iconName: String): ImageVector {
    return availableCategoryIcons.find { it.first == iconName }?.second ?: Icons.Default.Category
}
