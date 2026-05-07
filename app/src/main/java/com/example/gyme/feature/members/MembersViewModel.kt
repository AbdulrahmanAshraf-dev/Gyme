package com.example.gyme.feature.members

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gyme.domain.model.MemberStats
import com.example.gyme.domain.model.MemberSummary
import com.example.gyme.domain.model.MemberStatus
import kotlinx.coroutines.delay
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

class MembersViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<MembersUiState>(MembersUiState.Loading)
    val uiState: StateFlow<MembersUiState> = _uiState.asStateFlow()

    init {
        loadMembersData()
    }

    private fun loadMembersData() {
        viewModelScope.launch {
            _uiState.value = MembersUiState.Loading
            delay(800)
            
            val dummyStats = MemberStats(
                totalActive = 1248,
                activeGrowth = "↑ 12%",
                pendingActivation = 34,
                recentlyExpired = 18
            )

            val dummyMembers = listOf(
                MemberSummary(
                    id = "1",
                    name = "Elena Rodriguez",
                    plan = "Premium Unlimited",
                    status = MemberStatus.ACTIVE,
                    initials = "ER"
                ),
                MemberSummary(
                    id = "2",
                    name = "Marcus King",
                    plan = "Standard Pass",
                    status = MemberStatus.PENDING,
                    initials = "MK"
                ),
                MemberSummary(
                    id = "3",
                    name = "David Palmer",
                    plan = "Basic Access",
                    status = MemberStatus.EXPIRED,
                    initials = "DP"
                ),
                MemberSummary(
                    id = "4",
                    name = "Sophia Lin",
                    plan = "Premium Unlimited",
                    status = MemberStatus.ACTIVE,
                    initials = "SL"
                )
            )

            _uiState.value = MembersUiState.Success(dummyStats, dummyMembers)
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
