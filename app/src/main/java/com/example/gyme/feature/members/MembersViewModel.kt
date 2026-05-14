package com.example.gyme.feature.members

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gyme.core.model.*
import com.example.gyme.util.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class MembersUiState {
    object Loading : MembersUiState()
    data class Success(
        val stats: MemberStats,
        val members: List<MemberSummary>,
        val selectedFilter: String = "All Members"
    ) : MembersUiState()
    data class Error(val message: String) : MembersUiState()
}

class MembersViewModel(
    private val repository: MembersRepository = MembersRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<MembersUiState>(MembersUiState.Loading)
    val uiState: StateFlow<MembersUiState> = _uiState.asStateFlow()

    init {
        loadMembersData()
    }

    private fun loadMembersData() {
        viewModelScope.launch {
            _uiState.value = MembersUiState.Loading
            
            val result = repository.getAll()
            if (result is ApiResult.Success) {
                val sortedMembers = result.data.sortedBy { it.createdAt }
                val summaries = sortedMembers.mapIndexed { index, it ->
                    MemberSummary(
                        id = it.id,
                        displayId = (index + 1).toString(),
                        name = it.name,
                        plan = it.planId ?: "",
                        status = it.status,
                        initials = it.name.take(2).uppercase()
                    )
                }
                
                val stats = MemberStats(
                    totalActive = sortedMembers.count { it.status == "active" },
                    activeGrowth = "",
                    pendingActivation = sortedMembers.count { it.status == "pending" },
                    recentlyExpired = sortedMembers.count { it.status == "expired" }
                )
                
                _uiState.value = MembersUiState.Success(stats, summaries)
            } else {
                _uiState.value = MembersUiState.Error("Failed to load members")
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        // Implement search logic
    }

    fun onFilterSelected(filter: String) {
        val currentState = _uiState.value
        if (currentState is MembersUiState.Success) {
            _uiState.value = currentState.copy(selectedFilter = filter)
        }
    }

    fun renewMember(memberId: String) {
        viewModelScope.launch {
            // In a real app, you'd fetch the member to get the plan and current end date
            // For now, let's assume +1 month renewal for simplicity in this quick menu
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
            val calendar = java.util.Calendar.getInstance()
            calendar.add(java.util.Calendar.MONTH, 1)
            val newEnd = sdf.format(calendar.time)
            
            repository.update(id = memberId, status = "active", subscriptionEnd = newEnd)
            loadMembersData() // Refresh list
        }
    }

    fun freezeMember(memberId: String) {
        viewModelScope.launch {
            repository.update(id = memberId, status = "frozen")
            loadMembersData()
        }
    }

    fun blockMember(memberId: String) {
        viewModelScope.launch {
            repository.update(id = memberId, status = "blocked")
            loadMembersData()
        }
    }

    fun activateMember(memberId: String) {
        viewModelScope.launch {
            repository.update(id = memberId, status = "active")
            loadMembersData()
        }
    }
}
