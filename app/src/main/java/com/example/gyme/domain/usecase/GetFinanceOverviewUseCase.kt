package com.example.gyme.domain.usecase

import com.example.gyme.domain.model.ExpenseRequest
import com.example.gyme.domain.model.FinancialStats
import com.example.gyme.domain.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

data class FinanceOverview(
    val stats: FinancialStats,
    val pendingRequests: List<ExpenseRequest>
)

class GetFinanceOverviewUseCase(private val repository: FinanceRepository) {
    operator fun invoke(): Flow<FinanceOverview> {
        return combine(
            repository.getFinancialStats(),
            repository.getPendingExpenseRequests()
        ) { stats, requests ->
            FinanceOverview(stats, requests)
        }
    }
}
