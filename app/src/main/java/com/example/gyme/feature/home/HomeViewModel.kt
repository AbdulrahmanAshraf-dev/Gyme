package com.example.gyme.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gyme.feature.members.MembersRepository
import com.example.gyme.feature.finance.FinanceRepository
import com.example.gyme.feature.attendance.AttendanceRepository
import com.example.gyme.core.model.*
import com.example.gyme.util.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val stats: DashboardStats) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

class HomeViewModel(
    private val membersRepository: MembersRepository = MembersRepository(),
    private val financeRepository: FinanceRepository = FinanceRepository(),
    private val attendanceRepository: AttendanceRepository = AttendanceRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            
            val membersResult = membersRepository.getAll()
            val financeResult = financeRepository.getAll()
            val attendanceResult = attendanceRepository.getAll()

            if (membersResult is ApiResult.Success && 
                financeResult is ApiResult.Success && 
                attendanceResult is ApiResult.Success) {
                
                val members = membersResult.data
                val transactions = financeResult.data
                val attendance = attendanceResult.data

                val activeMembers = members.count { it.status == "active" }
                val expiringSoon = 0 // Calculate based on dates if needed
                val expired = members.count { it.status == "expired" }
                
                val todayRevenue = transactions.filter { it.type == "income" }.sumOf { it.amount }
                
                val stats = DashboardStats(
                    userName = com.example.gyme.util.SessionManager.currentUser?.name ?: "Manager",
                    totalRevenue = todayRevenue,
                    revenueGrowthPercentage = 0,
                    previousRevenue = 0.0,
                    currentlyActive = attendance.size, 
                    capacityPercentage = if (attendance.isNotEmpty()) (attendance.size * 100) / 100 else 0,
                    totalActiveMembers = activeMembers,
                    expiringSoonMembers = expiringSoon,
                    expiredMembers = expired
                )
                _uiState.value = HomeUiState.Success(stats)
            } else {
                _uiState.value = HomeUiState.Error("Failed to load dashboard data")
            }
        }
    }
}
