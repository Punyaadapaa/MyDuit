package com.example.myduit.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myduit.data.model.Transaction
import com.example.myduit.navigation.AddTransaction
import com.example.myduit.navigation.LocalBackStack
import com.example.myduit.ui.TransactionViewModel
import com.example.myduit.ui.CategoryViewModel
import com.example.myduit.ui.theme.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    transactionId: String,
    transactionViewModel: TransactionViewModel,
    categoryViewModel: CategoryViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val backStack = LocalBackStack.current
    val context = LocalContext.current

    var transactionState by remember { mutableStateOf<Transaction?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(transactionId) {
        transactionState = transactionViewModel.getById(transactionId)
        isLoading = false
    }

    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val transaction = transactionState
    if (transaction == null) {
        LaunchedEffect(Unit) { backStack.removeLastOrNull() }
        return
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }

    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    val categories by categoryViewModel.categories.collectAsState()
    val amountColor = if (transaction.isIncome) getIncomeGreen() else getExpenseRed()
    val bgColor = if (transaction.isIncome) Color(0xFFD1FAE5) else Color(0xFFFEE2E2)
    val sign = if (transaction.isIncome) "+" else "-"
    val iconName = categories.find { it.name == transaction.category }?.iconName
    val icon = iconName?.let { getIconByName(it) } ?: (if (transaction.isIncome) Icons.Filled.Work else Icons.Filled.ShoppingCart)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { backStack.removeLastOrNull() },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = MaterialTheme.colorScheme.primary)
                }
                Text(
                    text = "Transaction Detail",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                IconButton(
                    onClick = { backStack.add(AddTransaction(transactionId)) },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Main Transaction Amount
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(bgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = amountColor, modifier = Modifier.size(32.dp))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = transaction.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                val fmtAmount = NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID"))
                    .format(transaction.amount.toLong())
                Text(
                    text = "$sign Rp $fmtAmount",
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                    color = amountColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Completed",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            // Transaction Details Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    DetailRow("Date & Time", transaction.date)
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    DetailRow("Type", if (transaction.isIncome) "Income" else "Expense")
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    DetailRow("Category", transaction.category)
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Payment Method", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CreditCard, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Main Account", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    Column(modifier = Modifier.padding(vertical = 12.dp)) {
                        Text("Notes", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = transaction.title,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Actions
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { showBottomSheet = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.MoreHoriz, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Opsi Tambahan", fontWeight = FontWeight.SemiBold)
                }
                
                Button(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Hapus Transaksi", fontWeight = FontWeight.SemiBold)
                }
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
            sheetState = bottomSheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .padding(bottom = 32.dp)) {
                
                // Header
                Text(
                    "Opsi Tambahan",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 24.dp)
                )

                // Bagikan Transaksi
                BottomSheetAction(
                    icon = Icons.Default.Share,
                    iconBgColor = MaterialTheme.colorScheme.primaryContainer,
                    iconColor = MaterialTheme.colorScheme.primary,
                    text = "Bagikan Transaksi",
                    onClick = {
                        val shareIntent = transaction.buildShareIntent()
                        scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                            showBottomSheet = false
                            context.startActivity(
                                Intent.createChooser(shareIntent, "Bagikan transaksi")
                            )
                        }
                    }
                )

                BottomSheetAction(
                    icon = androidx.compose.material.icons.Icons.Default.Star,
                    iconBgColor = if (transaction.isImportant) Color(0xFF003212) else Color(0xFF95F8A7), 
                    iconColor = if (transaction.isImportant) Color(0xFF95F8A7) else Color(0xFF003212), 
                    text = if (transaction.isImportant) "Hapus Tanda Penting" else "Tandai sebagai Penting",
                    onClick = {
                        val updatedTx = transaction.copy(isImportant = !transaction.isImportant)
                        transactionViewModel.updateTransaction(updatedTx)
                        Toast.makeText(context, if (updatedTx.isImportant) "Ditandai penting" else "Tanda penting dihapus", Toast.LENGTH_SHORT).show()
                        scope.launch { bottomSheetState.hide() }.invokeOnCompletion { showBottomSheet = false }
                    }
                )

                BottomSheetAction(
                    icon = androidx.compose.material.icons.Icons.Default.Edit,
                    iconBgColor = Color(0xFFB7EAFF), 
                    iconColor = Color(0xFF006780), 
                    text = "Edit Transaksi",
                    onClick = {
                        scope.launch { bottomSheetState.hide() }.invokeOnCompletion { 
                            showBottomSheet = false
                            backStack.add(AddTransaction(transactionId = transaction.id))
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))
                
                // Batal Button
                OutlinedButton(
                    onClick = {
                        scope.launch { bottomSheetState.hide() }.invokeOnCompletion { showBottomSheet = false }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Batal", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun BottomSheetAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBgColor: Color,
    iconColor: Color,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconBgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun ShareButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
        }
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
        Kategori: $category
        Nominal: ${currency.format(amount)}
        Tanggal: $date
    """.trimIndent()
}
