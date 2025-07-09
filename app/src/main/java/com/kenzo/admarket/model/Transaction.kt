package com.kenzo.admarket.model

data class Transaction(
    val id: String,
    val date: String,
    val amount: Double,
    val status: String // You can use enum later if needed
)
