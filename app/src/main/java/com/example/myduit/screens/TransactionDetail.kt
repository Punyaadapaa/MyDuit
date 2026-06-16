package com.example.myduit.screens

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myduit.data.model.Transaction
import com.example.myduit.navigation.LocalBackStack
import com.example.myduit.ui.TransactionViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    transactionId: String,
    transactionViewModel: TransactionViewModel
) {
    val backStack = LocalBackStack.current
    val context = LocalContext.current

    // remember agar tidak null saat recompose setelah delete (mencegah double-pop)
    val transaction = remember { transactionViewModel.getTransactionById(transactionId) }

    // Kalau transaksi tidak ditemukan dari awal (ID invalid), langsung kembali
    if (transaction == null) {
        LaunchedEffect(Unit) { backStack.removeLastOrNull() }
        return
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }

    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val transactionAmountColor = if (transaction.isIncome) {
        MaterialTheme.colorScheme.tertiary
    } else {
        MaterialTheme.colorScheme.error
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Transaksi") },
                navigationIcon = {
                    IconButton(onClick = { backStack.removeLastOrNull() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            Icons.Outlined.Delete,
                            contentDescription = "Hapus",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(transaction.title, style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold)
            Text(transaction.date)
            val fmtAmount = NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID"))
                .format(transaction.amount.toLong())
            Text(
                "${if (transaction.isIncome) "+" else "-"} Rp $fmtAmount",
                color = transactionAmountColor,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.weight(1f))

            // Bottom Sheet
            Button(
                onClick = { showBottomSheet = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Opsi Tambahan")
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Transaksi") },
            text = { Text("Apakah Anda yakin ingin menghapus transaksi ini?") },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    onClick = {
                        showDeleteDialog = false
                        transactionViewModel.deleteTransaction(transaction)
                        backStack.removeLastOrNull()
                    }
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                    if (!bottomSheetState.isVisible) showBottomSheet = false
                }
            },
            sheetState = bottomSheetState
        ) {
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                Text("Opsi Tambahan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                ListItem(
                    leadingContent = {
                        Icon(Icons.Outlined.Share, contentDescription = null)
                    },
                    headlineContent = { Text("Bagikan Transaksi") },
                    modifier = Modifier.clickable {
                        val shareIntent = transaction.buildShareIntent()
                        scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                            showBottomSheet = false
                            context.startActivity(
                                Intent.createChooser(shareIntent, "Bagikan transaksi")
                            )
                        }
                    }
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

private fun Transaction.buildShareIntent(): Intent =
    Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Detail Transaksi MyDuit")
        putExtra(Intent.EXTRA_TEXT, buildShareText())
    }

private fun Transaction.buildShareText(): String {
    val currency = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID")).apply {
        maximumFractionDigits = 0
    }
    return """
        Detail Transaksi MyDuit

        Keterangan: $title
        Jenis: ${if (isIncome) "Pemasukan" else "Pengeluaran"}
        Nominal: ${currency.format(amount)}
        Tanggal: $date
    """.trimIndent()
}
