package com.example.gyme.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gyme.feature.members.MembersRepository
import com.example.gyme.feature.finance.FinanceRepository
import com.example.gyme.feature.attendance.AttendanceRepository
import com.example.gyme.data.remote.supabaseSdk
import io.github.jan.supabase.auth.auth
import com.example.gyme.core.model.*
import com.example.gyme.util.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

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

    private val _selectedDate = MutableStateFlow(Date())
    val selectedDate: StateFlow<Date> = _selectedDate.asStateFlow()

    init {
        loadDashboardData()
    }

    fun updateSelectedDate(date: Date) {
        _selectedDate.value = date
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            
            val membersResult = membersRepository.getAll()
            val financeResult = financeRepository.getAll()
            val attendanceResult = attendanceRepository.getAll()
            
            // Fetch user info from API
            val userFromApi = try {
                supabaseSdk.supabase.auth.retrieveUserForCurrentSession(updateSession = true)
            } catch (e: Exception) {
                null
            }
            
            val apiName = userFromApi?.userMetadata?.get("full_name")?.toString()?.replace("\"", "")
                ?: userFromApi?.userMetadata?.get("name")?.toString()?.replace("\"", "")
                ?: com.example.gyme.util.SessionManager.currentUser?.name

            if (membersResult is ApiResult.Success && 
                financeResult is ApiResult.Success && 
                attendanceResult is ApiResult.Success) {
                
                val members = membersResult.data
                val transactions = financeResult.data
                val attendance = attendanceResult.data

                val activeMembers = members.count { it.status == "active" }
                val expiringSoon = 0 // Calculate based on dates if needed
                val expired = members.count { it.status == "expired" }
                
                val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
                val dateStr = sdf.format(_selectedDate.value)

                val filteredTransactions = transactions.filter { 
                    it.createdAt.startsWith(dateStr) && it.type.equals("income", ignoreCase = true) 
                }
                val todayRevenue = filteredTransactions.sumOf { it.amount }
                val activeAttendance = attendance.filter { it.checkIn.startsWith(dateStr) }
                
                val plansResult = membersRepository.getAllPlans()
                val plansCount = if (plansResult is ApiResult.Success) plansResult.data.size else 0

                val trendData = calculateRevenueTrend(transactions)

                val stats = DashboardStats(
                    userName = apiName ?: "Abdulrahman",
                    totalRevenue = todayRevenue,
                    revenueGrowthPercentage = 0,
                    previousRevenue = 0.0,
                    currentlyActive = activeAttendance.size, 
                    capacityPercentage = if (activeAttendance.isNotEmpty()) (activeAttendance.size * 100) / 100 else 0,
                    totalActiveMembers = activeMembers,
                    expiringSoonMembers = plansCount, // Using this for plans count debug
                    expiredMembers = expired,
                    transactionCount = filteredTransactions.size,
                    revenueTrendData = trendData
                )
                _uiState.value = HomeUiState.Success(stats)
            } else {
                _uiState.value = HomeUiState.Error("Failed to load dashboard data")
            }
        }
    }

    private fun calculateRevenueTrend(transactions: List<Transaction>): List<Double> {
        val calendar = java.util.Calendar.getInstance()
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
}
