package com.example.gyme.domain.model

data class ExpenseRequest(
    val id: String,
    val staffName: String,
    val staffInitials: String,
    val description: String,
    val amount: Double
)
