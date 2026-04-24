package com.example.myduit.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myduit.Transaction
import com.example.myduit.navigation.LocalBackStack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(transaction: Transaction) {
    val backStack = LocalBackStack.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Transaksi") },
                navigationIcon = {
                    IconButton(onClick = { backStack.removeLastOrNull() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(transaction.title, style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold)
            Text(transaction.date)
            Text(
                "${if (transaction.isIncome) "+" else "-"} Rp ${transaction.amount}",
                color = if (transaction.isIncome) Color(0xFF4CAF50) else Color(0xFFE53935),
                fontWeight = FontWeight.Bold
            )
        }
    }
}