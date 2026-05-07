package com.example.gyme.feature.staff

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gyme.domain.model.StaffStats
import com.example.gyme.domain.model.StaffMember
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class StaffUiState {
    object Loading : StaffUiState()
    data class Success(
        val stats: StaffStats,
        val topTrainer: StaffMember,
        val staffList: List<StaffMember>
    ) : StaffUiState()
    data class Error(val message: String) : StaffUiState()
}

class StaffViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<StaffUiState>(StaffUiState.Loading)
    val uiState: StateFlow<StaffUiState> = _uiState.asStateFlow()

    init {
        loadStaffData()
    }

    private fun loadStaffData() {
        viewModelScope.launch {
            _uiState.value = StaffUiState.Loading
            delay(800)
            
            val dummyStats = StaffStats(
                totalActiveStaff = 42,
                growthText = "+3 this month",
                onShiftNow = 14,
                pendingRequests = 5
            )

            val dummyTopTrainer = StaffMember(
                id = "0",
                name = "Abdulrahman",
                role = "TRAINER",
                email = "abdulrahman@gymmanager.com",
                accessLabel = "Booking System",
                accessDescription = "Client Schedule & Notes",
                isAccessEnabled = true,
                rating = 4.9
            )

            val dummyStaff = listOf(
                StaffMember(
                    id = "1",
                    name = "Sarah Connor",
                    role = "MANAGER",
                    email = "sarah.c@gymmanager.com",
                    accessLabel = "System Access",
                    accessDescription = "Full Administrative Rights",
                    isAccessEnabled = true
                ),
                StaffMember(
                    id = "2",
                    name = "Abdulrahman",
                    role = "TRAINER",
                    email = "abdulrahman@gymmanager.com",
                    accessLabel = "Booking System",
                    accessDescription = "Client Schedule & Notes",
                    isAccessEnabled = true
                ),
                StaffMember(
                    id = "3",
                    name = "Elena Rodriguez",
                    role = "RECEPTIONIST",
                    email = "elena.r@gymmanager.com",
                    accessLabel = "Financial Access",
                    accessDescription = "View Only / Processing",
                    isAccessEnabled = false
                )
            )

            _uiState.value = StaffUiState.Success(dummyStats, dummyTopTrainer, dummyStaff)
        }
    }

    fun onToggleAccess(staffId: String, isEnabled: Boolean) {
        val currentState = _uiState.value
        if (currentState is StaffUiState.Success) {
            val updatedList = currentState.staffList.map {
                if (it.id == staffId) it.copy(isAccessEnabled = isEnabled) else it
            }
            _uiState.value = currentState.copy(staffList = updatedList)
        }
    }

    fun onSearchQueryChanged(query: String) {
        // Implement search logic
    }
}
