package com.example.myduit.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myduit.data.model.Transaction
import com.example.myduit.data.model.UiState
import com.example.myduit.navigation.LocalBackStack
import com.example.myduit.navigation.TransactionDetail
import com.example.myduit.ui.AuthViewModel
import com.example.myduit.ui.CurrencyViewModel
import com.example.myduit.ui.TransactionViewModel
import com.example.myduit.ui.CategoryViewModel
import com.example.myduit.ui.components.AppBottomNavigation
import com.example.myduit.ui.theme.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    transactionViewModel: TransactionViewModel,
    authViewModel: AuthViewModel = hiltViewModel(),
    currencyViewModel: CurrencyViewModel = hiltViewModel(),
    categoryViewModel: CategoryViewModel = hiltViewModel()
) {
    val backStack = LocalBackStack.current
    val scope = rememberCoroutineScope()
    val isDarkTheme = isSystemInDarkTheme()
    val context = LocalContext.current

    val username by authViewModel.usernameFlow.collectAsState(initial = "")
    val transactions by transactionViewModel.transactions.collectAsState()
    val rateState by currencyViewModel.rateState.collectAsState()
    val categories by categoryViewModel.categories.collectAsState()

    LaunchedEffect(Unit) { 
        currencyViewModel.loadRates()
    }

    var selectedFilter by remember { mutableStateOf("Semua") }
    val snackbarHostState = remember { SnackbarHostState() }

    // Saldo dihitung murni dari transaksi (awal = 0)
    val balance = transactions.fold(0.0) { acc, tx ->
        if (tx.isIncome) acc + tx.amount else acc - tx.amount
    }
    val indonesianLocale = Locale.forLanguageTag("id-ID")
    val formatted = NumberFormat.getCurrencyInstance(indonesianLocale).format(balance)
    val totalIncome = transactions.filter { it.isIncome }.sumOf { it.amount }
    val totalExpense = transactions.filter { !it.isIncome }.sumOf { it.amount }
    val fmt = NumberFormat.getCurrencyInstance(indonesianLocale).apply { maximumFractionDigits = 0 }

    val filteredTransactions = when (selectedFilter) {
        "Pemasukan" -> transactions.filter { it.isIncome }
        "Pengeluaran" -> transactions.filter { !it.isIncome }
        else -> transactions
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            AppBottomNavigation(currentRoute = "Dashboard")
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { backStack.add(com.example.myduit.navigation.AddTransaction()) },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Tambah")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = username.take(1).uppercase(),
                            color = MaterialTheme.colorScheme.onSecondary,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Halo,", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(username.ifBlank { "User" }, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    }
                }

                Text(
                    "MyDuit",
                    style = MaterialTheme.typography.displayLarge,
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Box(modifier = Modifier.weight(1f))
            }

            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Balance Bento
                item {
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
                                Text("Total Saldo", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                Spacer(Modifier.height(4.dp))
                                Text(formatted, style = MaterialTheme.typography.displayLarge, color = Color.White)
                            }
                        }
                    }
                }

                // Kurs Rate Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { backStack.add(com.example.myduit.navigation.Currency) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Filled.CurrencyExchange, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                                }
                                Spacer(Modifier.width(16.dp))
                                Column {
                                    Text("Kurs USD \u2192 IDR", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    when (val state = rateState) {
                                        is UiState.Idle -> Text("-", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                                        is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.size(16.dp), color = MaterialTheme.colorScheme.primary)
                                        is UiState.Success -> {
                                            val idrRate = state.data["IDR"] ?: 0.0
                                            Text("Rp ${formatRupiah(idrRate.toLong())}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                                        }
                                        is UiState.Error -> Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("Gagal", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.error)
                                            IconButton(onClick = { currencyViewModel.loadRates() }, modifier = Modifier.size(24.dp)) {
                                                Icon(Icons.Outlined.Refresh, contentDescription = "Coba lagi", modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Detail", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                                Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                            }
                        }
                    }
                }

                // Income / Expense Row
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(1.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Box(
                                    modifier = Modifier.size(32.dp).background(getIncomeBg(), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Filled.ArrowDownward, contentDescription = null, tint = getIncomeGreen(), modifier = Modifier.size(18.dp))
                                }
                                Spacer(Modifier.height(8.dp))
                                Text("Pemasukan", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(fmt.format(totalIncome), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(1.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Box(
                                    modifier = Modifier.size(32.dp).background(getExpenseBg(), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Filled.ArrowUpward, contentDescription = null, tint = getExpenseRed(), modifier = Modifier.size(18.dp))
                                }
                                Spacer(Modifier.height(8.dp))
                                Text("Pengeluaran", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(fmt.format(totalExpense), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }

                // Riwayat Section
                item {
                    Text("Riwayat", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 8.dp))
                }

                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(listOf("Semua", "Pemasukan", "Pengeluaran")) { option ->
                            FilterChip(
                                selected = selectedFilter == option,
                                onClick = { selectedFilter = option },
                                label = { Text(option, style = MaterialTheme.typography.labelSmall) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                border = null,
                                shape = CircleShape
                            )
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Column {
                            filteredTransactions.forEachIndexed { index, tx ->
                                val amountColor = if (tx.isIncome) getIncomeGreen() else getExpenseRed()
                                val sign = if (tx.isIncome) "+" else "-"
                                val iconName = categories.find { it.name == tx.category }?.iconName
                                val icon = iconName?.let { getIconByName(it) } ?: (if (tx.isIncome) Icons.Filled.Work else Icons.Filled.Restaurant)

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { backStack.add(TransactionDetail(tx.id)) }
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .background(
                                                    if (tx.isIncome) getIncomeBg() else getExpenseBg(),
                                                    CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                icon,
                                                contentDescription = null,
                                                tint = if (tx.isIncome) getIncomeGreen() else getExpenseRed(),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(Modifier.width(16.dp))
                                        Column {
                                            Text(tx.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                                            Text(tx.date, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                    Text(
                                        "$sign Rp ${formatRupiah(tx.amount.toLong())}",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = amountColor
                                    )
                                }
                                if (index < filteredTransactions.size - 1) {
                                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                }
                            }
                            if (filteredTransactions.isEmpty()) {
                                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                    Text("Belum ada transaksi", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }

    }
}
@Composable
fun DashboardMenuItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Box(modifier = Modifier.size(56.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.height(8.dp))
        Text(title, style = MaterialTheme.typography.labelMedium)
    }
}


private fun formatRupiah(value: Long): String { return NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID")).format(value) }

private class ThousandSeparatorTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text
        if (digits.isEmpty()) return TransformedText(text, OffsetMapping.Identity)
        val number = digits.toLongOrNull() ?: return TransformedText(text, OffsetMapping.Identity)
        val formatted = NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID")).format(number)
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                var digitsSeen = 0
                for (i in formatted.indices) { if (digitsSeen == offset) return i; if (formatted[i] != '.') digitsSeen++ }
                return formatted.length
            }
            override fun transformedToOriginal(offset: Int): Int {
                var digitsSeen = 0
                val clampedOffset = offset.coerceAtMost(formatted.length)
                for (i in 0 until clampedOffset) { if (formatted[i] != '.') digitsSeen++ }
                return digitsSeen
            }
        }
        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}
