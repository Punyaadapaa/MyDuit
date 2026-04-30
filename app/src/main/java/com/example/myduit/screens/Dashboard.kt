package com.example.myduit.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myduit.Transaction
import com.example.myduit.navigation.LocalBackStack
import com.example.myduit.navigation.TransactionDetail
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    transactions: MutableList<Transaction>,
    onAddTransaction: (Transaction) -> Unit
) {
    val backStack = LocalBackStack.current
    var showDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var isIncome by remember { mutableStateOf(true) }
    var selectedFilter by remember { mutableStateOf("Semua") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val balance = transactions.fold(100000.0) { acc, tx ->
        if (tx.isIncome) acc + tx.amount else acc - tx.amount
    }
    val formatted = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(balance)
    val totalIncome = transactions.filter { it.isIncome }.sumOf { it.amount }
    val totalExpense = transactions.filter { !it.isIncome }.sumOf { it.amount }
    val fmt = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply { maximumFractionDigits = 0 }

    val filteredTransactions = when (selectedFilter) {
        "Pemasukan" -> transactions.filter { it.isIncome }
        "Pengeluaran" -> transactions.filter { !it.isIncome }
        else -> transactions
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("MyDuit", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Tambah")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Total Saldo", color = MaterialTheme.colorScheme.onPrimary)
                    Text(formatted, color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 32.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Card(modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50))) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Pemasukan", color = Color.White, fontSize = 12.sp)
                        Text(fmt.format(totalIncome), color = Color.White,
                            fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
                Card(modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE53935))) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Pengeluaran", color = Color.White, fontSize = 12.sp)
                        Text(fmt.format(totalExpense), color = Color.White,
                            fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("Riwayat", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Semua", "Pemasukan", "Pengeluaran").forEach { option ->
                    FilterChip(
                        selected = selectedFilter == option,
                        onClick = { selectedFilter = option },
                        label = { Text(option) }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(
                    items = filteredTransactions,
                    key = { tx -> tx.id }
                ) { tx ->
                    val color = if (tx.isIncome) Color(0xFF4CAF50) else Color(0xFFE53935)
                    val sign = if (tx.isIncome) "+" else "-"

                    ListItem(
                        headlineContent = { Text(tx.title, fontWeight = FontWeight.Bold) },
                        supportingContent = { Text(tx.date) },
                        trailingContent = {
                            Text(
                                "$sign Rp ${tx.amount}",
                                color = color,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        modifier = Modifier.clickable {
                            backStack.add(TransactionDetail(tx.id))
                        }
                    )
                    HorizontalDivider()
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Catat Transaksi") },
                text = {
                    Column {
                        OutlinedTextField(value = title, onValueChange = { title = it },
                            label = { Text("Keterangan") })
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(value = amount, onValueChange = { amount = it },
                            label = { Text("Nominal") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = isIncome, onClick = { isIncome = true })
                            Text("Masuk")
                            Spacer(Modifier.width(16.dp))
                            RadioButton(selected = !isIncome, onClick = { isIncome = false })
                            Text("Keluar")
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val nominal = amount.toDoubleOrNull()
                        if (title.isNotBlank() && nominal != null && nominal > 0) {
                            val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
                            onAddTransaction(Transaction(title = title, amount = nominal,
                                isIncome = isIncome, date = date))
                            title = ""; amount = ""; isIncome = true; showDialog = false
                            scope.launch { snackbarHostState.showSnackbar("Transaksi berhasil disimpan") }
                        }
                    }) { Text("Simpan") }
                },
                dismissButton = {
                    TextButton(onClick = { title = ""; amount = ""; isIncome = true; showDialog = false }) {
                        Text("Batal")
                    }
                }
            )
        }
    }
}