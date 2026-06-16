package com.example.myduit.data.model

import java.util.UUID

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val amount: Double,
    val isIncome: Boolean,
    val date: String
)
