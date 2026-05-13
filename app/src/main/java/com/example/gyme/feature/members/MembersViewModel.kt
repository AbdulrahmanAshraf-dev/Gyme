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
                val members = result.data
                val summaries = members.map { 
                    MemberSummary(
                        id = it.id,
                        name = it.name,
                        plan = it.planId ?: "",
                        status = it.status,
                        initials = it.name.take(2).uppercase()
                    )
                }
                
                val stats = MemberStats(
                    totalActive = members.count { it.status == "active" },
                    activeGrowth = "",
                    pendingActivation = members.count { it.status == "pending" },
                    recentlyExpired = members.count { it.status == "expired" }
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

    fun onMemberMenuClicked(memberId: String) {
        // Handle menu click
    }
}
