package com.example.myduit.domain

import androidx.compose.runtime.mutableStateListOf

data class Transaction(
    val id: Int,
    val title: String,
    val amount: String,
    val categoryId: Int
)

data class Category(
    val id: Int,
    val name: String
)

val transactionList = mutableStateListOf(
    Transaction(1, "Makan Siang", "Rp 25.000", 1),
    Transaction(2, "Beli Kuota", "Rp 50.000", 2)
)

val categoryList = listOf(
    Category(1, "Makanan & Minuman"),
    Category(2, "Gajian"),
    Category(3, "Tagihan"),
    Category(4, "Transportasi")
)