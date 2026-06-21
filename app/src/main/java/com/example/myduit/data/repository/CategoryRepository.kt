package com.example.myduit.data.repository

import com.example.myduit.data.local.CategoryDao
import com.example.myduit.data.model.Category
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CategoryRepository @Inject constructor(
    private val dao: CategoryDao
) {
    fun getAllCategories(): Flow<List<Category>> = dao.getAllCategories()

    suspend fun insert(category: Category) = dao.insert(category)

    suspend fun delete(category: Category) = dao.delete(category)
}
