package com.example.myduit.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myduit.data.model.Transaction
import com.example.myduit.navigation.LocalBackStack
import com.example.myduit.navigation.ManageCategories
import com.example.myduit.ui.theme.*
import com.example.myduit.ui.CategoryViewModel
import com.example.myduit.ui.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    transactionId: String? = null,
    transactionViewModel: TransactionViewModel,
    categoryViewModel: CategoryViewModel = hiltViewModel()
) {
    val backStack = LocalBackStack.current
    val context = LocalContext.current

    val categories by categoryViewModel.categories.collectAsState()

    var isIncome by remember { mutableStateOf(true) }
    var nominal by remember { mutableStateOf("") }
    var keterangan by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Lainnya") }

    var currentDate by remember {
        mutableStateOf(SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")).format(Date()))
    }
    
    // If edit mode, load data
    LaunchedEffect(transactionId) {
        if (transactionId != null) {
            val existing = transactionViewModel.getById(transactionId)
            if (existing != null) {
                isIncome = existing.isIncome
                nominal = existing.amount.toLong().toString()
                keterangan = existing.title
                selectedCategory = existing.category
                currentDate = existing.date
            }
        }
    }
    
    val calendar = Calendar.getInstance()
    
    val timePickerDialog = TimePickerDialog(
        context,
        { _, selectedHour, selectedMinute ->
            calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
            calendar.set(Calendar.MINUTE, selectedMinute)
            currentDate = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")).format(calendar.time)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            calendar.set(Calendar.YEAR, selectedYear)
            calendar.set(Calendar.MONTH, selectedMonth)
            calendar.set(Calendar.DAY_OF_MONTH, selectedDay)
            timePickerDialog.show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

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
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Tutup")
                }
                Text(
                    text = if (transactionId != null) "Edit Transaksi" else "Transaksi Baru",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(modifier = Modifier.width(48.dp))
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Toggle Pemasukan / Pengeluaran
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surface),
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { isIncome = true }
                                .background(if (isIncome) Color(0xFFD1FAE5) else Color.Transparent)
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Pemasukan",
                                color = if (isIncome) getIncomeGreen() else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (isIncome) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { isIncome = false }
                                .background(if (!isIncome) Color(0xFFFEE2E2) else Color.Transparent)
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Pengeluaran",
                                color = if (!isIncome) getExpenseRed() else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (!isIncome) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }

                    // Nominal
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Nominal (Rp)", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        OutlinedTextField(
                            value = nominal,
                            onValueChange = { nominal = it },
                            placeholder = { Text("0", fontSize = 24.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            leadingIcon = { Text("Rp", modifier = Modifier.padding(start = 8.dp), color = MaterialTheme.colorScheme.onSurfaceVariant) },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = LocalTextStyle.current.copy(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Keterangan
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Keterangan", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        OutlinedTextField(
                            value = keterangan,
                            onValueChange = { keterangan = it },
                            placeholder = { Text("Contoh: Makan siang, Gaji...") },
                            leadingIcon = { Icon(Icons.Default.EditNote, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Tanggal
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Tanggal", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        OutlinedTextField(
                            value = currentDate,
                            onValueChange = { },
                            readOnly = true,
                            enabled = false,
                            leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                            modifier = Modifier.fillMaxWidth().clickable { datePickerDialog.show() },
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Kategori
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Kategori", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(categories) { category ->
                                val isSelected = selectedCategory == category.name
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedCategory = category.name },
                                    label = { Text(category.name) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = getIconByName(category.iconName),
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                        selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                )
                            }
                            
                            item {
                                FilterChip(
                                    selected = false,
                                    onClick = { backStack.add(ManageCategories) },
                                    label = { Text("Kelola") },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Settings,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                )
                            }
                        }
                    }

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedButton(
                            onClick = { backStack.removeLastOrNull() },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Batal", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Button(
                            onClick = {
                                val amountValue = nominal.toDoubleOrNull()
                                if (keterangan.isNotBlank() && amountValue != null && amountValue > 0) {
                                    val newTx = Transaction(
                                        id = transactionId ?: UUID.randomUUID().toString(),
                                        title = keterangan,
                                        amount = amountValue,
                                        isIncome = isIncome,
                                        date = currentDate,
                                        category = selectedCategory
                                    )
                                    
                                    if (transactionId != null) {
                                        transactionViewModel.updateTransaction(newTx)
                                        Toast.makeText(context, "Transaksi diperbarui!", Toast.LENGTH_SHORT).show()
                                        // Update state immediately if backStack pops to detail screen
                                    } else {
                                        transactionViewModel.addTransaction(newTx)
                                        Toast.makeText(context, "Transaksi berhasil disimpan!", Toast.LENGTH_SHORT).show()
                                    }
                                    backStack.removeLastOrNull()
                                } else {
                                    Toast.makeText(context, "Mohon isi data dengan benar", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Simpan", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
