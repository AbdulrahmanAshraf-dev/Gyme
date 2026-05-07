package com.example.gyme.feature.members.update

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gyme.domain.model.*
import com.example.gyme.domain.usecase.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class UpdateMemberUiState(
    val isLoading: Boolean = false,
    val member: Member? = null,
    val paymentHistory: List<PaymentRecord> = emptyList(),
    val availablePlans: List<MembershipPlan> = emptyList(),
    val error: String? = null,
    val isUpdateSuccess: Boolean = false
)

class UpdateMemberViewModel(
    private val memberId: String,
    private val getMemberDetails: GetMemberDetailsUseCase,
    private val getMemberPaymentHistory: GetMemberPaymentHistoryUseCase,
    private val getMembershipPlans: GetMembershipPlansUseCase,
    private val updateMember: UpdateMemberUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(UpdateMemberUiState())
    val uiState: StateFlow<UpdateMemberUiState> = _uiState.asStateFlow()

    init {
        loadMemberData()
    }

    private fun loadMemberData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Parallel loading
            val detailsResult = getMemberDetails(memberId)
            val historyResult = getMemberPaymentHistory(memberId)
            
            getMembershipPlans().collect { plans ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        member = detailsResult.getOrNull(),
                        paymentHistory = historyResult.getOrDefault(emptyList()),
                        availablePlans = plans,
                        error = if (detailsResult.isFailure) detailsResult.exceptionOrNull()?.message else null
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

    fun saveChanges() {
        val currentMember = _uiState.value.member ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            updateMember(currentMember).onSuccess {
                _uiState.update { it.copy(isLoading = false, isUpdateSuccess = true) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
