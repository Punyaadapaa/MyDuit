package com.example.myduit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.material3.FilterChip
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.myduit.ui.theme.MyDuitTheme
import java.text.SimpleDateFormat
import java.util.*
import java.text.NumberFormat

data class Transaction(val title: String, val amount: Double, val isIncome: Boolean, val date: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyDuitTheme() {
                DompetApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DompetApp() {
    var showDialog by remember { mutableStateOf(false) }
    val transactions = remember { mutableStateListOf<Transaction>() }
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
    val formattedIncome = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
        maximumFractionDigits = 0
    }.format(totalIncome)
    val formattedExpense = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
        maximumFractionDigits = 0
    }.format(totalExpense)

    val filterOptions = listOf("Semua", "Pemasukan", "Pengeluaran")
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
                    Text(formatted, color = MaterialTheme.colorScheme.onPrimary, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Card pemasukan & pengeluaran
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Pemasukan", color = Color.White, fontSize = 12.sp)
                        Text(formattedIncome, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE53935))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Pengeluaran", color = Color.White, fontSize = 12.sp)
                        Text(formattedExpense, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Riwayat", fontWeight = FontWeight.Bold, fontSize = 18.sp)

            Spacer(modifier = Modifier.height(8.dp))

            // FilterChip
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                filterOptions.forEach { option ->
                    FilterChip(
                        selected = selectedFilter == option,
                        onClick = { selectedFilter = option },
                        label = { Text(option) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(filteredTransactions) { tx ->
                    val color = if (tx.isIncome) Color(0xFF4CAF50) else Color(0xFFE53935)
                    val sign = if (tx.isIncome) "+" else "-"

                    ListItem(
                        headlineContent = { Text(tx.title, fontWeight = FontWeight.Bold) },
                        supportingContent = { Text(tx.date) },
                        trailingContent = { Text("$sign Rp ${tx.amount}", color = color, fontWeight = FontWeight.Bold) },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
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
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Keterangan") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = amount,
                            onValueChange = { amount = it },
                            label = { Text("Nominal") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = isIncome, onClick = { isIncome = true })
                            Text("Masuk")
                            Spacer(modifier = Modifier.width(16.dp))
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
                            transactions.add(Transaction(title, nominal, isIncome, date))
                            title = ""
                            amount = ""
                            isIncome = true
                            showDialog = false
                            // Tampilkan snackbar
                            scope.launch {
                                snackbarHostState.showSnackbar("Transaksi berhasil disimpan")
                            }
                        }
                    }) { Text("Simpan") }
                },
                dismissButton = {
                    TextButton(onClick = {
                        title = ""
                        amount = ""
                        isIncome = true
                        showDialog = false
                    }) { Text("Batal") }
                }
            )
        }
    }
}