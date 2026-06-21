package com.example.myduit.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Description
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
import com.example.myduit.ui.TransactionViewModel
import com.example.myduit.ui.theme.*
import com.example.myduit.ui.components.AppBottomNavigation
import com.example.myduit.navigation.LocalBackStack
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreen(
    transactionViewModel: TransactionViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val backStack = LocalBackStack.current
    
    var selectedPeriod by remember { mutableStateOf("Bulan Ini") }
    
    val calendar = Calendar.getInstance()
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
    var fromDate by remember { mutableStateOf(sdf.format(calendar.apply { set(Calendar.DAY_OF_MONTH, 1) }.time)) }
    var toDate by remember { mutableStateOf(sdf.format(Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH)) }.time)) }

    val fromDatePicker = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val cal = Calendar.getInstance().apply { set(year, month, dayOfMonth) }
            fromDate = sdf.format(cal.time)
            selectedPeriod = "Kustom"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val toDatePicker = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val cal = Calendar.getInstance().apply { set(year, month, dayOfMonth) }
            toDate = sdf.format(cal.time)
            selectedPeriod = "Kustom"
        },
        Calendar.getInstance().get(Calendar.YEAR),
        Calendar.getInstance().get(Calendar.MONTH),
        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    )

    val transactions by transactionViewModel.transactions.collectAsState()

    // Filter logic
    val filteredTransactions = transactions.filter { tx ->
        try {
            val tDate = sdf.parse(tx.date.split(",")[0].trim())
            val fDate = sdf.parse(fromDate)
            val tDateObj = sdf.parse(toDate)
            if (tDate != null && fDate != null && tDateObj != null) {
                !tDate.before(fDate) && !tDate.after(tDateObj)
            } else {
                true
            }
        } catch (e: Exception) {
            true
        }
    }

    val totalIncome = filteredTransactions.filter { it.isIncome }.sumOf { it.amount }
    val totalExpense = filteredTransactions.filter { !it.isIncome }.sumOf { it.amount }
    val currentBalance = totalIncome - totalExpense

    val exportReportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv"),
        onResult = { uri ->
            if (uri != null) {
                scope.launch {
                    try {
                        val csvData = transactionViewModel.buildReportCsv(filteredTransactions) 
                        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                            outputStream.write(csvData.toByteArray())
                        }
                        Toast.makeText(context, "Laporan berhasil diekspor!", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(context, "Gagal mengekspor laporan: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        bottomBar = {
            AppBottomNavigation(currentRoute = "Export")
        },
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
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = MaterialTheme.colorScheme.primary)
                }
                Text(
                    text = "Ekspor Laporan",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.width(48.dp)) // Untuk menyeimbangkan layout
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Periode Laporan
            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Periode Laporan", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                    
                    // Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val options = listOf("Bulan Ini", "Bulan Lalu", "3 Bulan Terakhir", "Kustom")
                        options.forEach { option ->
                            val isSelected = selectedPeriod == option
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { 
                                        selectedPeriod = option 
                                        when (option) {
                                            "Bulan Ini" -> {
                                                fromDate = sdf.format(Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, 1) }.time)
                                                toDate = sdf.format(Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH)) }.time)
                                            }
                                            "Bulan Lalu" -> {
                                                fromDate = sdf.format(Calendar.getInstance().apply { 
                                                    add(Calendar.MONTH, -1)
                                                    set(Calendar.DAY_OF_MONTH, 1) 
                                                }.time)
                                                toDate = sdf.format(Calendar.getInstance().apply { 
                                                    add(Calendar.MONTH, -1)
                                                    set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH)) 
                                                }.time)
                                            }
                                            "3 Bulan Terakhir" -> {
                                                fromDate = sdf.format(Calendar.getInstance().apply { 
                                                    add(Calendar.MONTH, -2)
                                                    set(Calendar.DAY_OF_MONTH, 1) 
                                                }.time)
                                                toDate = sdf.format(Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH)) }.time)
                                            }
                                        }
                                    }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = option,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Dari - Sampai
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Dari", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            OutlinedTextField(
                                value = fromDate,
                                onValueChange = {},
                                readOnly = true,
                                enabled = false,
                                leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                                modifier = Modifier.fillMaxWidth().clickable { fromDatePicker.show() },
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                    disabledBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                    disabledContainerColor = MaterialTheme.colorScheme.surface
                                )
                            )
                        }
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Sampai", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            OutlinedTextField(
                                value = toDate,
                                onValueChange = {},
                                readOnly = true,
                                enabled = false,
                                leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                                modifier = Modifier.fillMaxWidth().clickable { toDatePicker.show() },
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                    disabledBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                    disabledContainerColor = MaterialTheme.colorScheme.surface
                                )
                            )
                        }
                    }
                }
            }

            // Pratinjau Ringkasan
            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Pratinjau Ringkasan", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                    
                    val currency = java.text.NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID")).apply {
                        maximumFractionDigits = 0
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Pemasukan", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(currency.format(totalIncome), color = getIncomeGreen(), fontWeight = FontWeight.SemiBold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Pengeluaran", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(currency.format(totalExpense), color = getExpenseRed(), fontWeight = FontWeight.SemiBold)
                            }
                            HorizontalDivider()
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total Saldo", color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                                Text(currency.format(currentBalance), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Format File
            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Format File", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Hanya CSV
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .border(2.dp, MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Description, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(32.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("CSV", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold)
                                Text("Data Mentah", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                            }
                        }
                        
                        // Empty box for alignment
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            // Tombol Export
            item {
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        val fileName = "Laporan_MyDuit_${selectedPeriod.replace(" ", "_")}.csv"
                        exportReportLauncher.launch(fileName)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(Icons.Default.Description, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ekspor Sekarang", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
            }
        }
    }
}
