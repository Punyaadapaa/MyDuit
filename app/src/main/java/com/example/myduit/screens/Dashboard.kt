package com.example.myduit.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    transactionViewModel: TransactionViewModel,
    authViewModel: AuthViewModel = hiltViewModel(),
    currencyViewModel: CurrencyViewModel = hiltViewModel()
) {
    val backStack = LocalBackStack.current
    val scope = rememberCoroutineScope()
    val isDarkTheme = isSystemInDarkTheme()
    val context = androidx.compose.ui.platform.LocalContext.current

    val username by authViewModel.usernameFlow.collectAsState(initial = "")
    val transactions by transactionViewModel.transactions.collectAsState()
    val rateState by currencyViewModel.rateState.collectAsState()

    LaunchedEffect(Unit) { currencyViewModel.loadUsdToIdr() }

    var showDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var isIncome by remember { mutableStateOf(true) }
    var selectedFilter by remember { mutableStateOf("Semua") }
    var pendingExportCsv by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val exportReportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv"),
        onResult = { uri ->
            if (uri == null) return@rememberLauncherForActivityResult

            val isSaved = context.writeTextToUri(uri, pendingExportCsv)
            scope.launch {
                snackbarHostState.showSnackbar(
                    if (isSaved) "Laporan berhasil diekspor" else "Gagal mengekspor laporan"
                )
            }
        }
    )

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
        topBar = {
            TopAppBar(
                title = { Text("MyDuit", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(
                        onClick = {
                            pendingExportCsv = buildReportCsv(
                                transactions = transactions,
                                balance = balance,
                                totalIncome = totalIncome,
                                totalExpense = totalExpense
                            )
                            exportReportLauncher.launch(buildReportFileName())
                        }
                    ) {
                        Icon(
                            Icons.Outlined.FileDownload,
                            contentDescription = "Ekspor laporan"
                        )
                    }
                    IconButton(onClick = {
                        scope.launch {
                            authViewModel.clearUsername()
                            while (backStack.size > 1) {
                                backStack.removeLastOrNull()
                            }
                        }
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Outlined.Add, contentDescription = "Tambah")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            if (username.isNotBlank()) {
                Text(
                    text = "Halo, $username 👋",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(8.dp))
            }

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

            // Kurs USD → IDR card dengan UiState
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Kurs USD \u2192 IDR", fontSize = 13.sp)
                    when (val state = rateState) {
                        is UiState.Idle -> Text("-")
                        is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.size(16.dp))
                        is UiState.Success -> Text(
                            "Rp ${formatRupiah(state.data.toLong())}",
                            fontWeight = FontWeight.Bold
                        )
                        is UiState.Error -> Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                state.message,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.error
                            )
                            IconButton(
                                onClick = { currencyViewModel.loadUsdToIdr() },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    Icons.Outlined.Refresh,
                                    contentDescription = "Coba lagi",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Pemasukan",
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            fontSize = 12.sp
                        )
                        Text(
                            fmt.format(totalIncome),
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Pengeluaran",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontSize = 12.sp
                        )
                        Text(
                            fmt.format(totalExpense),
                            color = MaterialTheme.colorScheme.onErrorContainer,
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
                    val amountColor = if (tx.isIncome) {
                        MaterialTheme.colorScheme.tertiary
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                    val sign = if (tx.isIncome) "+" else "-"

                    ListItem(
                        headlineContent = { Text(tx.title, fontWeight = FontWeight.Bold) },
                        supportingContent = { Text(tx.date) },
                        trailingContent = {
                            Text(
                                "$sign Rp ${formatRupiah(tx.amount.toLong())}",
                                color = amountColor,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.clickable {
                            backStack.add(TransactionDetail(tx.id))
                        }
                    )
                    HorizontalDivider()
                }
            }
        }

        if (showDialog) {
            val dialogContainerColor = if (isDarkTheme) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                Color.White
            }
            val dialogTextFieldColors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = MaterialTheme.colorScheme.secondary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = MaterialTheme.colorScheme.secondary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                cursorColor = MaterialTheme.colorScheme.secondary
            )

            AlertDialog(
                onDismissRequest = { showDialog = false },
                containerColor = dialogContainerColor,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
                textContentColor = MaterialTheme.colorScheme.onSurface,
                title = { Text("Catat Transaksi") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Keterangan") },
                            colors = dialogTextFieldColors
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = amount,
                            onValueChange = { newValue ->
                                amount = newValue.filter { it.isDigit() }.trimStart('0')
                            },
                            label = { Text("Nominal") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            visualTransformation = ThousandSeparatorTransformation(),
                            colors = dialogTextFieldColors
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = isIncome,
                                onClick = { isIncome = true },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colorScheme.secondary,
                                    unselectedColor = MaterialTheme.colorScheme.outline
                                )
                            )
                            Text("Masuk")
                            Spacer(Modifier.width(16.dp))
                            RadioButton(
                                selected = !isIncome,
                                onClick = { isIncome = false },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colorScheme.secondary,
                                    unselectedColor = MaterialTheme.colorScheme.outline
                                )
                            )
                            Text("Keluar")
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val nominal = amount.toDoubleOrNull()
                        if (title.isNotBlank() && nominal != null && nominal > 0) {
                            val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
                            transactionViewModel.addTransaction(
                                Transaction(title = title, amount = nominal,
                                    isIncome = isIncome, date = date)
                            )
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

private fun buildReportFileName(): String {
    val timestamp = SimpleDateFormat("yyyyMMdd-HHmm", Locale.getDefault()).format(Date())
    return "laporan-myduit-$timestamp.csv"
}

private fun buildReportCsv(
    transactions: List<Transaction>,
    balance: Double,
    totalIncome: Double,
    totalExpense: Double
): String = buildString {
    append('\uFEFF')
    appendCsvLine("sep=;")
    appendCsvLine(csvRow("Laporan MyDuit", "", "", "", ""))
    appendCsvLine(csvRow("Dibuat", SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(Date()), "", "", ""))
    appendCsvLine()
    appendCsvLine(csvRow("Ringkasan", "Nominal (Rp)", "", "", ""))
    appendCsvLine(csvRow("Saldo Saat Ini", balance.asRupiahCsvNumber(), "", "", ""))
    appendCsvLine(csvRow("Total Pemasukan", totalIncome.asRupiahCsvNumber(), "", "", ""))
    appendCsvLine(csvRow("Total Pengeluaran", totalExpense.asRupiahCsvNumber(), "", "", ""))
    appendCsvLine()
    appendCsvLine(csvRow("No", "Tanggal", "Jenis", "Keterangan", "Nominal (Rp)"))
    transactions.forEachIndexed { index, transaction ->
        appendCsvLine(
            csvRow(
                index + 1,
                transaction.date,
                if (transaction.isIncome) "Pemasukan" else "Pengeluaran",
                transaction.title,
                transaction.amount.asRupiahCsvNumber()
            )
        )
    }
}

private fun StringBuilder.appendCsvLine(value: String = "") {
    append(value)
    append("\r\n")
}

private fun csvRow(vararg values: Any?): String =
    values.joinToString(separator = ";") { value ->
        val text = value?.toString().orEmpty()
        "\"${text.replace("\"", "\"\"")}\""
    }

private fun Double.asRupiahCsvNumber(): String = toLong().toString()

private fun Context.writeTextToUri(uri: Uri, text: String): Boolean =
    runCatching {
        contentResolver.openOutputStream(uri)?.bufferedWriter()?.use { writer ->
            writer.write(text)
        } ?: error("Tidak bisa membuka file tujuan")
    }.isSuccess

/**
 * Format angka dengan titik sebagai pemisah ribuan (Indonesian locale).
 * Contoh: 16500 → "16.500", 1500000 → "1.500.000"
 */
private fun formatRupiah(value: Long): String {
    return NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID")).format(value)
}

/**
 * VisualTransformation yang menampilkan titik pemisah ribuan pada input field.
 * State tetap menyimpan angka murni ("1500000"), tampilan jadi "1.500.000".
 */
private class ThousandSeparatorTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text
        if (digits.isEmpty()) return TransformedText(text, OffsetMapping.Identity)

        val number = digits.toLongOrNull() ?: return TransformedText(text, OffsetMapping.Identity)
        val formatted = NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID")).format(number)

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                // Hitung posisi di formatted string berdasarkan jumlah digit yang sudah dilewati
                var digitsSeen = 0
                for (i in formatted.indices) {
                    if (digitsSeen == offset) return i
                    if (formatted[i] != '.') digitsSeen++
                }
                return formatted.length
            }

            override fun transformedToOriginal(offset: Int): Int {
                // Hitung jumlah digit sebelum posisi offset di formatted string
                var digitsSeen = 0
                val clampedOffset = offset.coerceAtMost(formatted.length)
                for (i in 0 until clampedOffset) {
                    if (formatted[i] != '.') digitsSeen++
                }
                return digitsSeen
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}
