package com.example.gyme.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gyme.domain.model.DashboardStats
import com.example.gyme.domain.usecase.GetDashboardStatsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * UI State for the Home Screen.
 * Represents the 3 possible states: Loading, Success, Error.
 */
sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val stats: DashboardStats) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

/**
 * ViewModel managing the state for the Home Dashboard.
 * Utilizes StateFlow for reactive UI updates.
 */
class HomeViewModel(
    private val getDashboardStatsUseCase: GetDashboardStatsUseCase = GetDashboardStatsUseCase()
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            
            getDashboardStatsUseCase().fold(
                onSuccess = { stats ->
                    _uiState.value = HomeUiState.Success(stats)
                },
                onFailure = { error ->
                    _uiState.value = HomeUiState.Error(error.localizedMessage ?: "An unexpected error occurred.")
                }
            )
        }
    }
}
