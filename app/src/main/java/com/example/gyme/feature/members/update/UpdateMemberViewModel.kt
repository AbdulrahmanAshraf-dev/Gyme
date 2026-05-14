package com.example.gyme.feature.members.update

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gyme.feature.members.MembersRepository
import com.example.gyme.core.model.*
import com.example.gyme.util.ApiResult
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class UpdateMemberUiState(
    val isLoading: Boolean = false,
    val member: Member? = null,
    val displayId: String = "",
    val paymentHistory: List<PaymentRecord> = emptyList(),
    val availablePlans: List<MembershipPlan> = emptyList(),
    val error: String? = null,
    val isUpdateSuccess: Boolean = false
)

class UpdateMemberViewModel(
    private val memberId: String,
    private val repository: MembersRepository = MembersRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(UpdateMemberUiState())
    val uiState: StateFlow<UpdateMemberUiState> = _uiState.asStateFlow()

    init {
        loadMemberData()
    }

    private fun loadMemberData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val detailsResult = repository.getById(memberId)
            val allMembersResult = repository.getAll()
            
            var displayId = ""
            if (allMembersResult is ApiResult.Success) {
                val sorted = allMembersResult.data.sortedBy { it.createdAt }
                val index = sorted.indexOfFirst { it.id == memberId }
                if (index != -1) {
                    displayId = (index + 1).toString()
                }
            }
            
            repository.getMembershipPlans().collect { plans ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        member = if (detailsResult is ApiResult.Success) detailsResult.data else null,
                        displayId = displayId,
                        availablePlans = plans,
                        error = if (detailsResult is ApiResult.Error) detailsResult.message else null
                    )
                }
            }
        }
    }

    fun onNameChange(newName: String) {
        _uiState.update { state ->
            state.copy(member = state.member?.copy(name = newName))
        }
    }

    fun onEmailChange(newEmail: String) {
        _uiState.update { state ->
            state.copy(member = state.member?.copy(email = newEmail))
        }
    }

    fun onPhoneChange(newPhone: String) {
        _uiState.update { state ->
            state.copy(member = state.member?.copy(phone = newPhone))
        }
    }

    fun onPlanChange(newPlan: MembershipPlan) {
        _uiState.update { state ->
            state.copy(member = state.member?.copy(planId = newPlan.id))
        }
    }

    fun renewMembership() {
        val member = _uiState.value.member ?: return
        val plan = _uiState.value.availablePlans.find { it.id == member.planId } ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
            val currentEnd = member.subscriptionEnd?.let { 
                try { sdf.parse(it) } catch (e: Exception) { null } 
            } ?: java.util.Date()
            
            val calendar = java.util.Calendar.getInstance()
            calendar.time = currentEnd
            calendar.add(java.util.Calendar.MONTH, plan.durationMonths)
            val newEnd = sdf.format(calendar.time)
            
            val result = repository.update(
                id = member.id,
                status = "active",
                subscriptionEnd = newEnd
            )
            handleUpdateResult(result)
        }
    }

    fun freezeAccount() {
        val member = _uiState.value.member ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = repository.update(id = member.id, status = "frozen")
            handleUpdateResult(result)
        }
    }

    fun blockMember() {
        val member = _uiState.value.member ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = repository.update(id = member.id, status = "blocked")
            handleUpdateResult(result)
        }
    }

    private fun handleUpdateResult(result: ApiResult<Member>) {
        when (result) {
            is ApiResult.Success -> {
                _uiState.update { it.copy(isLoading = false, member = result.data, isUpdateSuccess = true) }
            }
            is ApiResult.Error -> {
                _uiState.update { it.copy(isLoading = false, error = result.message) }
            }
        }
    }

    fun saveChanges() {
        val currentMember = _uiState.value.member ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = repository.update(
                id = currentMember.id,
                name = currentMember.name,
                phone = currentMember.phone,
                status = currentMember.status
            )
            handleUpdateResult(result)
        }
    }
}
