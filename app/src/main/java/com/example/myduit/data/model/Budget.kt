package com.example.myduit.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "budgets")
data class Budget(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val category: String,
    val limitAmount: Double,
    val monthYear: String // Format: "MM-yyyy" (e.g., "06-2026")
)
