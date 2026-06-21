package com.example.myduit.data.repository

import com.example.myduit.data.local.TransactionDao
import com.example.myduit.data.model.Transaction
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TransactionRepository @Inject constructor(
    private val dao: TransactionDao
) {
    fun getAllTransactions(): Flow<List<Transaction>> = dao.getAllTransactions()

    fun getTransactionsByMonth(monthYear: String): Flow<List<Transaction>> = dao.getTransactionsByMonth(monthYear)

    suspend fun getById(id: String): Transaction? = dao.getById(id)

    suspend fun insert(transaction: Transaction) = dao.insert(transaction)

    suspend fun delete(transaction: Transaction) = dao.delete(transaction)
}
