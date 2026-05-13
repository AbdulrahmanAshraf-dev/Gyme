package com.example.gyme.feature.staff

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gyme.core.model.*
import com.example.gyme.util.ApiResult
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

class StaffViewModel(
    private val repository: StaffRepository = StaffRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<StaffUiState>(StaffUiState.Loading)
    val uiState: StateFlow<StaffUiState> = _uiState.asStateFlow()

    init {
        loadStaffData()
    }

    private fun loadStaffData() {
        viewModelScope.launch {
            _uiState.value = StaffUiState.Loading
            
            val result = repository.getAll()
            if (result is ApiResult.Success) {
                val users = result.data
                val staffList = users.map { 
                    StaffMember(
                        id = it.id,
                        name = it.name,
                        role = it.role.uppercase(),
                        email = it.email,
                        accessLabel = "System Access",
                        accessDescription = if (it.canDeleteMember) "Full Access" else "Limited Access",
                        isAccessEnabled = true
                    )
                }

                val stats = StaffStats(
                    totalActiveStaff = users.size,
                    growthText = "",
                    onShiftNow = users.size,
                    pendingRequests = 0
                )

                val topTrainer = staffList.firstOrNull { it.role == "TRAINER" } ?: staffList.firstOrNull() ?: StaffMember(
                    "0", "Guest", "STAFF", "", "", "", false
                )

                _uiState.value = StaffUiState.Success(stats, topTrainer, staffList)
            } else {
                _uiState.value = StaffUiState.Error("Failed to load staff")
            }
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
