package com.example.gyme.domain.repository

import com.example.gyme.domain.model.ExpenseRequest
import com.example.gyme.domain.model.FinancialStats
import kotlinx.coroutines.flow.Flow

interface FinanceRepository {
    fun getFinancialStats(): Flow<FinancialStats>
    fun getPendingExpenseRequests(): Flow<List<ExpenseRequest>>
    suspend fun approveExpense(id: String)
}
