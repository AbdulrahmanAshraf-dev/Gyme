package com.example.gyme.feature.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gyme.core.model.*
import com.example.gyme.util.ApiResult
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class FinanceUiState {
    object Loading : FinanceUiState()
    data class Success(
        val stats: FinancialStats,
        val pendingRequests: List<ExpenseRequest>
    ) : FinanceUiState()
    data class Error(val message: String) : FinanceUiState()
}

class FinanceViewModel(
    private val repository: FinanceRepository = FinanceRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<FinanceUiState>(FinanceUiState.Loading)
    val uiState: StateFlow<FinanceUiState> = _uiState.asStateFlow()

    init {
        loadFinanceData()
    }

    private fun loadFinanceData() {
        viewModelScope.launch {
            _uiState.value = FinanceUiState.Loading
            
            val result = repository.getAll()
            if (result is ApiResult.Success) {
                val transactions = result.data
                val totalIncome = transactions.filter { it.type == "income" }.sumOf { it.amount }
                val totalExpense = transactions.filter { it.type == "expense" }.sumOf { it.amount }
                val netProfit = totalIncome - totalExpense
                
                val trendData = calculateRevenueTrend(transactions)
                
                val stats = FinancialStats(
                    totalRevenue = totalIncome,
                    totalExpenses = totalExpense,
                    netProfit = netProfit,
                    revenueTrend = "Updated Today", 
                    isRevenuePositive = true,
                    expensesTrend = "", 
                    netProfitTrend = "", 
                    isNetProfitPositive = netProfit >= 0,
                    currentMonth = "This Month",
                    pendingExpenses = transactions.count { it.status == "pending" && it.type == "expense" },
                    activeSubscriptions = transactions.count { it.type == "income" },
                    revenueTrendData = trendData
                )
                
                val pendingRequests = transactions.filter { it.status == "pending" && it.type == "expense" }
                    .map { 
                        ExpenseRequest(
                            id = it.id,
                            description = it.description,
                            amount = it.amount,
                            date = it.createdAt,
                            staffName = "Staff Member", 
                            staffInitials = "SM"
                        )
                    }
                
                _uiState.value = FinanceUiState.Success(stats, pendingRequests)
            } else {
                _uiState.value = FinanceUiState.Error("Failed to load finance data")
            }
        }
    }

    private fun calculateRevenueTrend(transactions: List<Transaction>): List<Double> {
        val sdf = java.text.SimpleDateFormat("yyyy-MM", java.util.Locale.US)
        val months = mutableListOf<String>()
        
        for (i in 5 downTo 0) {
            val cal = java.util.Calendar.getInstance()
            cal.add(java.util.Calendar.MONTH, -i)
            months.add(sdf.format(cal.time))
        }
        
        return months.map { month ->
            transactions.filter { 
                it.createdAt.startsWith(month) && it.type.equals("income", ignoreCase = true) 
            }.sumOf { it.amount }
        }
    }

    fun onApproveExpense(id: String) {
        viewModelScope.launch {
            repository.update(id, status = "approved")
            loadFinanceData()
        }
    }

    fun onReviewExpense(id: String) {
        // Mock review logic
    }
}
