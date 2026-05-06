package com.example.gyme.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gyme.data.repository.AttendanceRepositoryImpl
import com.example.gyme.data.repository.MembersRepositoryImpl
import com.example.gyme.data.repository.TransactionsRepositoryImpl
import com.example.gyme.domain.model.Attendance
import com.example.gyme.domain.model.Member
import com.example.gyme.domain.model.Transaction
import com.example.gyme.domain.repository.AttendanceRepository
import com.example.gyme.domain.repository.MembersRepository
import com.example.gyme.domain.repository.TransactionsRepository
import com.example.gyme.util.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = true,
    val members: List<Member> = emptyList(),
    val attendance: List<Attendance> = emptyList(),
    val transactions: List<Transaction> = emptyList(),
    val errorMessage: String? = null
)

class HomeViewModel(
    private val membersRepo: MembersRepository = MembersRepositoryImpl(),
    private val attendanceRepo: AttendanceRepository = AttendanceRepositoryImpl(),
    private val transactionsRepo: TransactionsRepository = TransactionsRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        fetchDashboardData()
    }

    fun fetchDashboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val membersResult = membersRepo.getAll()
            val attendanceResult = attendanceRepo.getAll()
            val transactionsResult = transactionsRepo.getAll()

            if (membersResult is ApiResult.Success && 
                attendanceResult is ApiResult.Success && 
                transactionsResult is ApiResult.Success) {
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    members = membersResult.data,
                    attendance = attendanceResult.data,
                    transactions = transactionsResult.data
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load some dashboard data."
                )
            }
        }
    }
}
