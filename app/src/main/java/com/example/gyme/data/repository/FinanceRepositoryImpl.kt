package com.example.gyme.data.repository

import com.example.gyme.domain.model.ExpenseRequest
import com.example.gyme.domain.model.FinancialStats
import com.example.gyme.domain.repository.FinanceRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FinanceRepositoryImpl : FinanceRepository {

    override fun getFinancialStats(): Flow<FinancialStats> = flow {
        // In real production, this calls remoteDataSource/localDataSource
        delay(500)
        emit(
            FinancialStats(
                totalRevenue = 425000.0,
                revenueTrend = "↑ 15% vs last month",
                isRevenuePositive = true,
                totalExpenses = 184500.0,
                expensesTrend = "— In line with projection",
                netProfit = 240500.0,
                netProfitTrend = "↑ 12% vs last month",
                isNetProfitPositive = true,
                currentMonth = "MAY 2026"
            )
        )
    }

    override fun getPendingExpenseRequests(): Flow<List<ExpenseRequest>> = flow {
        delay(300)
        emit(
            listOf(
                ExpenseRequest("1", "Sarah Jenkins", "SJ", "New Yoga Mats", 4500.00),
                ExpenseRequest("2", "Abdulrahman", "AR", "Equipment Maintenance", 8200.00)
            )
        )
    }

    override suspend fun approveExpense(id: String) {
        // Implementation for API call
    }
}
