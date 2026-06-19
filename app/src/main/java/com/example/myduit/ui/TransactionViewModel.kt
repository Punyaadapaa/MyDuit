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

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch { repository.delete(transaction) }
    }

    suspend fun getById(id: String): Transaction? {
        return repository.getById(id)
    }
}
