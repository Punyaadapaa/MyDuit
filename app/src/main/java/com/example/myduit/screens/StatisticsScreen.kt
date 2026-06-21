package com.example.myduit.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Commute
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myduit.data.model.Transaction
import com.example.myduit.navigation.LocalBackStack
import com.example.myduit.ui.TransactionViewModel
import com.example.myduit.ui.CategoryViewModel
import com.example.myduit.ui.theme.*
import com.example.myduit.ui.components.AppBottomNavigation
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    transactionViewModel: TransactionViewModel,
    categoryViewModel: CategoryViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val backStack = LocalBackStack.current
    val transactions by transactionViewModel.transactions.collectAsState()
    val categories by categoryViewModel.categories.collectAsState()


    
    var selectedPeriod by remember { mutableStateOf("Bulan Ini") }
    
    val filteredTransactions = remember(transactions, selectedPeriod) {
        val indonesianLocale = Locale("id", "ID")
        val dateFormat1 = SimpleDateFormat("dd MMM yyyy, HH:mm", indonesianLocale)
        val dateFormat2 = SimpleDateFormat("dd MMM yyyy", indonesianLocale)
        
        val startOfPeriod = Calendar.getInstance().apply {
            when (selectedPeriod) {
                "Minggu Ini" -> {
                    set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                }
                "Bulan Ini" -> {
                    set(Calendar.DAY_OF_MONTH, 1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                }
                "Tahun Ini" -> {
                    set(Calendar.DAY_OF_YEAR, 1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                }
            }
        }.time

        transactions.filter { tx ->
            val txDate = try {
                if (tx.date.contains(",")) dateFormat1.parse(tx.date) else dateFormat2.parse(tx.date)
            } catch (e: Exception) { null }
            txDate != null && txDate.after(startOfPeriod)
        }
    }

    // Hitung saldo murni untuk periode ini
    val balance = filteredTransactions.fold(0.0) { acc, tx ->
        if (tx.isIncome) acc + tx.amount else acc - tx.amount
    }
    
    val totalIncome = filteredTransactions.filter { it.isIncome }.sumOf { it.amount }
    val totalExpense = filteredTransactions.filter { !it.isIncome }.sumOf { it.amount }
    
    val indonesianLocale = Locale.forLanguageTag("id-ID")
    val formatter = NumberFormat.getCurrencyInstance(indonesianLocale).apply { maximumFractionDigits = 0 }
    val formattedBalance = formatter.format(balance)
    val formattedIncome = formatter.format(totalIncome)
    val formattedExpense = formatter.format(totalExpense)

    val spentPercentage = if (totalIncome > 0) (totalExpense / totalIncome).toFloat() else 0f

    val topExpenses = remember(filteredTransactions) {
        val expenses = filteredTransactions.filter { !it.isIncome }
        val totalExp = expenses.sumOf { it.amount }
        expenses.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
            .toList()
            .sortedByDescending { it.second }
            .take(3)
            .map { Pair(it.first, Pair(it.second, if (totalExp > 0) (it.second / totalExp).toFloat() else 0f)) }
    }

    val smartTip = remember(totalIncome, totalExpense) {
        if (totalIncome == 0.0 && totalExpense == 0.0) {
            "Belum ada transaksi di periode ini. Yuk, mulai catat keuanganmu!"
        } else if (totalExpense > totalIncome) {
            "Pengeluaranmu melebihi pemasukan bulan ini. Coba evaluasi kembali budget jajanmu ya."
        } else if (spentPercentage > 0.8f) {
            "Kamu sudah menggunakan ${"%.0f".format(spentPercentage * 100)}% dari pemasukanmu. Ingat untuk menyisihkan tabungan!"
        } else {
            "Keuanganmu sangat sehat! Pengeluaranmu terkontrol dengan baik."
        }
    }

    val chartDataAndLabels = remember(filteredTransactions, selectedPeriod) {
        val locale = Locale("id", "ID")
        val dfTime = SimpleDateFormat("dd MMM yyyy, HH:mm", locale)
        val dfDate = SimpleDateFormat("dd MMM yyyy", locale)
        
        fun parse(d: String): java.util.Date? = try { if (d.contains(",")) dfTime.parse(d) else dfDate.parse(d) } catch(e:Exception){null}

        if (filteredTransactions.isEmpty()) {
            Pair(listOf(Pair(0f, 0f)), listOf("-"))
        } else {
            when (selectedPeriod) {
                "Minggu Ini" -> {
                    val dfDay = SimpleDateFormat("EEE", locale)
                    val grouped = filteredTransactions.groupBy { dfDay.format(parse(it.date) ?: java.util.Date()) }
                    val days = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")
                    val data = days.map { day ->
                        val txs = grouped[day] ?: emptyList()
                        val inc = txs.filter{it.isIncome}.sumOf{it.amount}.toFloat()
                        val exp = txs.filter{!it.isIncome}.sumOf{it.amount}.toFloat()
                        Pair(inc, exp)
                    }
                    Pair(data, days)
                }
                "Bulan Ini" -> {
                    val grouped = filteredTransactions.groupBy { 
                        val cal = Calendar.getInstance()
                        val parsed = parse(it.date)
                        if (parsed != null) cal.time = parsed
                        "M${cal.get(Calendar.WEEK_OF_MONTH)}" 
                    }
                    val weeks = listOf("M1", "M2", "M3", "M4", "M5")
                    val data = weeks.map { w ->
                        val txs = grouped[w] ?: emptyList()
                        val inc = txs.filter{it.isIncome}.sumOf{it.amount}.toFloat()
                        val exp = txs.filter{!it.isIncome}.sumOf{it.amount}.toFloat()
                        Pair(inc, exp)
                    }
                    Pair(data, weeks)
                }
                else -> {
                    val dfMonth = SimpleDateFormat("MMM", locale)
                    val grouped = filteredTransactions.groupBy { dfMonth.format(parse(it.date) ?: java.util.Date()) }
                    val months = listOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des")
                    val data = months.map { m ->
                        val txs = grouped[m] ?: emptyList()
                        val inc = txs.filter{it.isIncome}.sumOf{it.amount}.toFloat()
                        val exp = txs.filter{!it.isIncome}.sumOf{it.amount}.toFloat()
                        Pair(inc, exp)
                    }
                    Pair(data, months)
                }
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        bottomBar = {
            AppBottomNavigation(currentRoute = "Statistics")
        },
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { /* TODO */ }, modifier = Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))) {
                    Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
                Text(
                    text = "Statistik",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
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
            // Period Selector
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedPeriod == "Minggu Ini", 
                        onClick = { selectedPeriod = "Minggu Ini" }, 
                        label = { Text("Minggu Ini") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                    FilterChip(
                        selected = selectedPeriod == "Bulan Ini",
                        onClick = { selectedPeriod = "Bulan Ini" },
                        label = { Text("Bulan Ini") },
                         colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                    FilterChip(
                        selected = selectedPeriod == "Tahun Ini", 
                        onClick = { selectedPeriod = "Tahun Ini" }, 
                        label = { Text("Tahun Ini") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            // Cash Flow Summary Bento
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text("Total Saldo $selectedPeriod", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(formattedBalance, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onPrimaryContainer)
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f))
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text("Pemasukan", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                Text("+ $formattedIncome", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFF79DB8D))
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Pengeluaran", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                Text("- $formattedExpense", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = getExpenseRed())
                            }
                        }
                    }
                }
            }

            // Circular Progress
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Rasio Pengeluaran", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            com.example.myduit.screens.components.CircularProgressChart(
                                percentage = spentPercentage,
                                label = "Terpakai"
                            )
                        }
                    }
                }
            }

            // Arus Kas Chart
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Arus Kas", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            Text(selectedPeriod, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Compose Canvas for Bar Chart
                        Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                            val data = chartDataAndLabels.first
                            val months = chartDataAndLabels.second
                            val maxValue = data.maxOfOrNull { max(it.first, it.second) }?.takeIf { it > 0f } ?: 100f
                            
                            val incomeColor = getIncomeGreen()
                            val expenseColor = getExpenseRed()
                            val gridColor = MaterialTheme.colorScheme.surfaceVariant
                            val textColor = MaterialTheme.colorScheme.onSurfaceVariant

                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val canvasWidth = size.width
                                val canvasHeight = size.height
                                val paddingBottom = 40f
                                val barGroupWidth = canvasWidth / data.size
                                val maxBarHeight = canvasHeight - paddingBottom

                                // Draw baseline
                                drawLine(
                                    color = gridColor,
                                    start = Offset(0f, canvasHeight - paddingBottom),
                                    end = Offset(canvasWidth, canvasHeight - paddingBottom),
                                    strokeWidth = 2f
                                )

                                data.forEachIndexed { index, (income, expense) ->
                                    val incomeHeight = (income / maxValue) * maxBarHeight
                                    val expenseHeight = (expense / maxValue) * maxBarHeight
                                    
                                    val groupX = index * barGroupWidth
                                    val barWidth = barGroupWidth * 0.3f
                                    val spacing = barGroupWidth * 0.1f
                                    
                                    val incomeX = groupX + (barGroupWidth - barWidth * 2 - spacing) / 2
                                    val expenseX = incomeX + barWidth + spacing

                                    // Draw Income Bar
                                    drawRoundRect(
                                        color = incomeColor.copy(alpha = if (index == data.size -1) 1f else 0.8f),
                                        topLeft = Offset(incomeX, canvasHeight - paddingBottom - incomeHeight),
                                        size = Size(barWidth, incomeHeight),
                                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                                    )
                                    // Draw Expense Bar
                                    drawRoundRect(
                                        color = expenseColor.copy(alpha = if (index == data.size -1) 1f else 0.8f),
                                        topLeft = Offset(expenseX, canvasHeight - paddingBottom - expenseHeight),
                                        size = Size(barWidth, expenseHeight),
                                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                                    )
                                }
                            }
                            
                            // Labels (Simulated using compose for easier text)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                                    .padding(top = 16.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                months.forEachIndexed { i, month ->
                                    Text(
                                        text = month,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (i == months.size - 1) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = if (i == months.size - 1) FontWeight.Bold else FontWeight.Normal,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                        
                        // Legend
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(12.dp).background(getIncomeGreen(), RoundedCornerShape(2.dp)))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Pemasukan", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(12.dp).background(getExpenseRed(), RoundedCornerShape(2.dp)))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Pengeluaran", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
            
            // Top Pengeluaran
            item {
                Text("Top Pengeluaran", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        if (topExpenses.isEmpty()) {
                            Text("Belum ada data pengeluaran.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else {
                            topExpenses.forEachIndexed { index, expense ->
                                val category = expense.first
                                val amount = formatter.format(expense.second.first)
                                val progress = expense.second.second
                                
                                val iconName = categories.find { it.name == category }?.iconName
                                val icon = iconName?.let { getIconByName(it) } ?: when(category.lowercase()) {
                                    "makanan", "makan" -> Icons.Default.Restaurant
                                    "transport", "transportasi" -> Icons.Default.Commute
                                    "belanja" -> Icons.Default.ShoppingBag
                                    else -> Icons.Default.Category
                                }
                                
                                val iconBg = if (index == 0) Color(0xFFFEE2E2) else MaterialTheme.colorScheme.surfaceVariant
                                val iconColor = if (index == 0) getExpenseRed() else MaterialTheme.colorScheme.onSurfaceVariant
                                val progressColor = if (index == 0) getExpenseRed() else Color(0xFF94A3B8)
                                
                                CategoryProgressRow(
                                    icon = icon, 
                                    iconBg = iconBg, 
                                    iconColor = iconColor, 
                                    title = category, 
                                    amount = amount, 
                                    progress = progress, 
                                    progressColor = progressColor
                                )
                            }
                        }
                    }
                }
            }
            
            // Smart Tip
            item {
                val isDark = androidx.compose.foundation.isSystemInDarkTheme()
                val tipBgColor = if (isDark) Color(0xFF064E3B) else Color(0xFFD1FAE5)
                val tipIconColor = getIncomeGreen()
                val tipTitleColor = if (isDark) Color(0xFFECFDF5) else MaterialTheme.colorScheme.onSurface
                val tipTextColor = if (isDark) Color(0xFFA7F3D0) else MaterialTheme.colorScheme.onSurfaceVariant
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = tipBgColor),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = tipIconColor)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Smart Tip", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = tipTitleColor)
                            Text(smartTip, style = MaterialTheme.typography.bodyMedium, color = tipTextColor)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryProgressRow(icon: androidx.compose.ui.graphics.vector.ImageVector, iconBg: Color, iconColor: Color, title: String, amount: String, progress: Float, progressColor: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(32.dp).background(iconBg, CircleShape), contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleMedium)
            }
            Text(amount, style = MaterialTheme.typography.bodyLarge)
        }
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)),
            color = progressColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}
