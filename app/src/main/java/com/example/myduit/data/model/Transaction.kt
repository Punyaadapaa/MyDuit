package com.example.myduit.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val amount: Double,
    val isIncome: Boolean,
    val date: String,
    val category: String = "Lainnya",
    val isImportant: Boolean = false
)
