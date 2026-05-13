package com.example.gyme.feature.members.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gyme.feature.members.MembersRepository
import com.example.gyme.core.model.*
import com.example.gyme.util.ApiResult
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date

data class AddMemberUiState(
    val isLoading: Boolean = false,
    val name: String = "",
    val phone: String = "",
    val gender: String = "male",
    val subscriptionStart: Date = Date(),
    val subscriptionEnd: Date? = null,
    val selectedPlan: MembershipPlan? = null,
    val availablePlans: List<MembershipPlan> = emptyList(),
    val discount: String = "0",
    val discountType: String = "percentage",
    val isPaid: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val memberId: String = ""
)

class AddMemberViewModel(
    private val repository: MembersRepository = MembersRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddMemberUiState())
    val uiState: StateFlow<AddMemberUiState> = _uiState.asStateFlow()

    init {
        loadPlans()
    }

    private fun loadPlans() {
        viewModelScope.launch {
            repository.getMembershipPlans().collect { plans ->
                _uiState.update { 
                    it.copy(
                        availablePlans = plans,
                        selectedPlan = plans.firstOrNull()
                    )
                }
            }
        }
    }

    fun onFullNameChange(name: String) = _uiState.update { it.copy(name = name) }
    fun onPhoneNumberChange(phone: String) = _uiState.update { it.copy(phone = phone) }
    fun onGenderChange(gender: String) = _uiState.update { it.copy(gender = gender) }
    fun onPlanChange(plan: MembershipPlan) = _uiState.update { it.copy(selectedPlan = plan) }
    fun onDiscountChange(discount: String) = _uiState.update { it.copy(discount = discount) }
    fun onDiscountTypeChange(type: String) = _uiState.update { it.copy(discountType = type) }
    fun onSubscriptionStartChange(date: Date) = _uiState.update { it.copy(subscriptionStart = date) }
    fun onSubscriptionEndChange(date: Date?) = _uiState.update { it.copy(subscriptionEnd = date) }

    fun addMember() {
        val state = _uiState.value
        if (state.selectedPlan == null) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", java.util.Locale.US)
            val result = repository.create(
                name = state.name,
                phone = state.phone,
                subscriptionStart = sdf.format(state.subscriptionStart),
                subscriptionEnd = state.subscriptionEnd?.let { sdf.format(it) } ?: ""
            )

            when (result) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }
                is ApiResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
            }
        }
    }
}
