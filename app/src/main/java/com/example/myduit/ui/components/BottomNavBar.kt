package com.example.myduit.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.myduit.navigation.ExportReport
import com.example.myduit.navigation.Dashboard
import com.example.myduit.navigation.LocalBackStack
import com.example.myduit.navigation.Profile
import com.example.myduit.navigation.Statistics

@Composable
fun AppBottomNavigation(currentRoute: String) {
    val backStack = LocalBackStack.current

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        tonalElevation = 4.dp
    ) {
        NavigationBarItem(
            selected = currentRoute == "Dashboard",
            onClick = {
                if (currentRoute != "Dashboard") backStack.add(Dashboard)
            },
            icon = { Icon(Icons.Outlined.GridView, contentDescription = "Home") },
            label = { Text("Home", style = MaterialTheme.typography.labelSmall) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
        NavigationBarItem(
            selected = currentRoute == "Statistics",
            onClick = {
                if (currentRoute != "Statistics") backStack.add(Statistics)
            },
            icon = { Icon(Icons.Outlined.ReceiptLong, contentDescription = "History") },
            label = { Text("Statistics", style = MaterialTheme.typography.labelSmall) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
        NavigationBarItem(
            selected = currentRoute == "Export",
            onClick = {
                if (currentRoute != "Export") backStack.add(ExportReport)
            },
            icon = { Icon(Icons.Outlined.AccountBalanceWallet, contentDescription = "Export") },
            label = { Text("Export", style = MaterialTheme.typography.labelSmall) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
        NavigationBarItem(
            selected = currentRoute == "Profile",
            onClick = {
                if (currentRoute != "Profile") backStack.add(Profile)
            },
            icon = { Icon(Icons.Outlined.Person, contentDescription = "Profile") },
            label = { Text("Profile", style = MaterialTheme.typography.labelSmall) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}
