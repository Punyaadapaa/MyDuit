package com.example.myduit.data.repository

import com.example.myduit.data.local.BudgetDao
import com.example.myduit.data.model.Budget
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BudgetRepository @Inject constructor(
    private val dao: BudgetDao
) {
    fun getBudgetsByMonth(monthYear: String): Flow<List<Budget>> = dao.getBudgetsByMonth(monthYear)

    suspend fun insert(budget: Budget) = dao.insert(budget)

    suspend fun delete(budget: Budget) = dao.delete(budget)
}
