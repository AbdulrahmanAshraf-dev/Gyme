package com.example.gyme.domain.model

data class FinancialStats(
    val totalRevenue: Double,
    val revenueTrend: String,
    val isRevenuePositive: Boolean,
    
    val totalExpenses: Double,
    val expensesTrend: String,
    
    val netProfit: Double,
    val netProfitTrend: String,
    val isNetProfitPositive: Boolean,
    
    val currentMonth: String
)
