package com.example.myduit.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myduit.Transaction
import com.example.myduit.navigation.LocalBackStack
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    transaction: Transaction,
    // PERUBAHAN 1: Tambah parameter callback untuk fungsi hapus
    onDeleteTransaction: (Transaction) -> Unit
) {
    val backStack = LocalBackStack.current

    // PERUBAHAN 2: Tambahkan state untuk mengontrol visibilitas komponen
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }

    // State dan Coroutine untuk animasi Bottom Sheet yang aman
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Transaksi") },
                navigationIcon = {
                    IconButton(onClick = { backStack.removeLastOrNull() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                // PERUBAHAN 3: Tambahkan tombol aksi di TopAppBar
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Red)
                    }
                    IconButton(onClick = { showBottomSheet = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Opsi Lain")
                    }
                }
            )
        }
    ) { padding ->
        // (Kode layout detail kamu tetap utuh dan sama persis di sini)
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

    // PERUBAHAN 4: Implementasi Alert Dialog (Ketentuan Praktikum)
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Transaksi") },
            text = { Text("Apakah Anda yakin ingin menghapus transaksi ini?") },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    onClick = {
                        showDeleteDialog = false
                        onDeleteTransaction(transaction) // Menghapus data
                        backStack.removeLastOrNull() // Kembali secara aman (tidak force close)
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

    // PERUBAHAN 5: Implementasi Bottom Sheet (Ketentuan Praktikum)
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                // Menutup dengan aman menggunakan coroutine
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
                    headlineContent = { Text("Bagikan Transaksi") },
                    modifier = Modifier.clickable {
                        // Tutup otomatis jika diklik
                        scope.launch { bottomSheetState.hide() }.invokeOnCompletion { showBottomSheet = false }
                    }
                )
                // Spacer agar tidak menabrak batas bawah layar
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}