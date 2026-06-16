package com.example.myduit.ui

import androidx.lifecycle.ViewModel
import com.example.myduit.data.model.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor() : ViewModel() {

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    fun addTransaction(transaction: Transaction) {
        _transactions.value = _transactions.value + transaction
    }

    fun deleteTransaction(transaction: Transaction) {
        _transactions.value = _transactions.value.filter { it.id != transaction.id }
    }

    fun getTransactionById(id: String): Transaction? {
        return _transactions.value.find { it.id == id }
    }
}
