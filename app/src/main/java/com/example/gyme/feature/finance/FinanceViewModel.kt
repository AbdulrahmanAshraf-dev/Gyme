package com.example.gyme.feature.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gyme.domain.model.ExpenseRequest
import com.example.gyme.domain.model.FinancialStats
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import com.example.gyme.domain.usecase.GetFinanceOverviewUseCase
import com.example.gyme.data.repository.FinanceRepositoryImpl
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
    private val getFinanceOverview: GetFinanceOverviewUseCase = GetFinanceOverviewUseCase(FinanceRepositoryImpl())
) : ViewModel() {

    private val _uiState = MutableStateFlow<FinanceUiState>(FinanceUiState.Loading)
    val uiState: StateFlow<FinanceUiState> = _uiState.asStateFlow()

    init {
        loadFinanceData()
    }

    private fun loadFinanceData() {
        viewModelScope.launch {
            getFinanceOverview()
                .onStart { _uiState.value = FinanceUiState.Loading }
                .catch { e -> _uiState.value = FinanceUiState.Error(e.message ?: "Unknown Error") }
                .collect { overview ->
                    _uiState.value = FinanceUiState.Success(overview.stats, overview.pendingRequests)
                }
        }
    }

    fun onApproveExpense(id: String) {
        // Handle approval via repository
    }

    fun onReviewExpense(id: String) {
        // Mock review logic
    }
}
