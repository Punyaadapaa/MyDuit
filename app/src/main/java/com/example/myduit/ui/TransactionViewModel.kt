package com.example.myduit.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myduit.data.model.Transaction
import com.example.myduit.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    val transactions: StateFlow<List<Transaction>> = repository.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch { repository.insert(transaction) }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch { repository.insert(transaction) }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch { repository.delete(transaction) }
    }

    suspend fun getById(id: String): Transaction? {
        return repository.getById(id)
    }

    fun getIncomeVsExpense(): Pair<Double, Double> {
        val income = transactions.value.filter { it.isIncome }.sumOf { it.amount }
        val expense = transactions.value.filter { !it.isIncome }.sumOf { it.amount }
        return income to expense
    }

    fun buildReportCsv(txList: List<Transaction>): String = buildString {
        val income = txList.filter { it.isIncome }.sumOf { it.amount }
        val expense = txList.filter { !it.isIncome }.sumOf { it.amount }
        val balance = income - expense

        append("Laporan Keuangan MyDuit\n")
        append("Total Pemasukan,Total Pengeluaran,Saldo Bersih\n")
        append("$income,$expense,$balance\n\n")
        append("Rincian Transaksi:\n")
        append("Tanggal,Jenis,Kategori,Keterangan,Nominal\n")
        txList.forEach { tx ->
            val jenis = if (tx.isIncome) "Pemasukan" else "Pengeluaran"
            append("${tx.date},$jenis,${tx.category},${tx.title},${tx.amount}\n")
        }
    }
}
