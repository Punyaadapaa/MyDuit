package com.example.myduit.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myduit.data.model.UiState
import com.example.myduit.navigation.LocalBackStack
import com.example.myduit.ui.CurrencyViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyScreen(
    currencyViewModel: CurrencyViewModel = hiltViewModel()
) {
    val backStack = LocalBackStack.current
    val rateState by currencyViewModel.rateState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    // Ensure data is loaded
    LaunchedEffect(Unit) {
        if (currencyViewModel.rateState.value is UiState.Idle) {
            currencyViewModel.loadRates()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.9f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { backStack.removeLastOrNull() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = MaterialTheme.colorScheme.primary)
                        }
                        Text(
                            "Nilai Tukar",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "Opsi", tint = MaterialTheme.colorScheme.primary)
                    }
                }
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Cari mata uang...", style = MaterialTheme.typography.bodyMedium) },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, modifier = Modifier.size(20.dp)) },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            cursorColor = MaterialTheme.colorScheme.primary
                        ),
                        singleLine = true
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            when (val state = rateState) {
                is UiState.Idle, is UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                is UiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.size(16.dp))
                        IconButton(onClick = { currencyViewModel.loadRates() }) {
                            Icon(Icons.Outlined.Refresh, contentDescription = "Coba lagi")
                        }
                    }
                }
                is UiState.Success -> {
                    val idrRate = state.data["IDR"] ?: 0.0
                    val fmt = NumberFormat.getNumberInstance(Locale("id", "ID"))
                    
                    Spacer(Modifier.height(8.dp))

                    // Base Rate Hero Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            // Decorative blur circles
                            Box(modifier = Modifier.align(Alignment.TopEnd).offset(x = 20.dp, y = (-20).dp).size(120.dp).blur(40.dp).background(Color.White.copy(alpha = 0.05f), CircleShape))
                            Box(modifier = Modifier.align(Alignment.BottomStart).offset(x = (-20).dp, y = 20.dp).size(80.dp).blur(20.dp).background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f), CircleShape))

                            Column(modifier = Modifier.padding(24.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier.size(24.dp).background(Color.White.copy(alpha = 0.2f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Filled.CurrencyExchange, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    Text("1 USD setara dengan", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "Rp ${fmt.format(idrRate)}",
                                    style = MaterialTheme.typography.displayLarge,
                                    color = Color.White
                                )
                                Spacer(Modifier.height(16.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(12.dp))
                                    Spacer(Modifier.width(4.dp))
                                    val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                                    Text("TERAKHIR DIPERBARUI: HARI INI, $time", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onPrimaryContainer)
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                    Text("Mata Uang Populer", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))

                    val ratesList = state.data.toList()
                        .filter { it.first.contains(searchQuery, ignoreCase = true) }
                        .sortedBy { it.first }

                    Card(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        LazyColumn {
                            items(ratesList) { (currencyCode, rate) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { }
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(currencyCode.take(3), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                        }
                                        Spacer(Modifier.width(16.dp))
                                        Column {
                                            Text(currencyCode, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                                            val countryName = when(currencyCode) {
                                                "IDR" -> "Indonesia"
                                                "USD" -> "Amerika Serikat"
                                                "EUR" -> "Uni Eropa"
                                                "JPY" -> "Jepang"
                                                "GBP" -> "Inggris Raya"
                                                "AUD" -> "Australia"
                                                "CAD" -> "Kanada"
                                                "CHF" -> "Swiss"
                                                "CNY" -> "Tiongkok"
                                                "HKD" -> "Hong Kong"
                                                "NZD" -> "Selandia Baru"
                                                "KRW" -> "Korea Selatan"
                                                "SGD" -> "Singapura"
                                                "INR" -> "India"
                                                "MYR" -> "Malaysia"
                                                "THB" -> "Thailand"
                                                "PHP" -> "Filipina"
                                                "VND" -> "Vietnam"
                                                "SAR" -> "Arab Saudi"
                                                "AED" -> "Uni Emirat Arab"
                                                "TRY" -> "Turki"
                                                "RUB" -> "Rusia"
                                                "ZAR" -> "Afrika Selatan"
                                                "BRL" -> "Brasil"
                                                "MXN" -> "Meksiko"
                                                else -> "Global"
                                            }
                                            Text(countryName, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                    Text(fmt.format(rate), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                                }
                                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            }
                        }
                    }
                }
            }
        }
    }
}
