package com.example.gyme.feature.members.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gyme.feature.members.MembersRepository
import com.example.gyme.feature.finance.FinanceRepository
import com.example.gyme.util.SessionManager
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
    val amountPaid: String = "",
    val isPaid: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val memberId: String = ""
)

class AddMemberViewModel(
    private val repository: MembersRepository = MembersRepository(),
    private val financeRepository: FinanceRepository = FinanceRepository()
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
    fun onPhoneNumberChange(phone: String) {
        val cleanPhone = phone.filter { it.isDigit() }
        if (cleanPhone.length <= 10) {
            _uiState.update { it.copy(phone = cleanPhone) }
        }
    }
    fun onGenderChange(gender: String) = _uiState.update { it.copy(gender = gender) }
    fun onPlanChange(plan: MembershipPlan) = _uiState.update { it.copy(selectedPlan = plan) }
    fun onDiscountChange(discount: String) = _uiState.update { it.copy(discount = discount) }
    fun onDiscountTypeChange(type: String) = _uiState.update { it.copy(discountType = type) }
    fun onSubscriptionStartChange(date: Date) = _uiState.update { it.copy(subscriptionStart = date) }
    fun onSubscriptionEndChange(date: Date?) = _uiState.update { it.copy(subscriptionEnd = date) }
    fun onAmountPaidChange(amount: String) = _uiState.update { it.copy(amountPaid = amount.filter { it.isDigit() }) }

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
                    // Create transaction for the payment
                    val plan = state.selectedPlan!!
                    val basePrice = plan.price
                    val disc = state.discount.toDoubleOrNull() ?: 0.0
                    val finalPrice = if (state.discountType == "percentage") {
                        basePrice * (1 - disc / 100)
                    } else {
                        basePrice - disc
                    }
                    
                    val financeResult = financeRepository.create(
                        amount = finalPrice,
                        type = "income",
                        description = "Subscription: ${plan.name} - ${state.name}",
                        createdBy = SessionManager.currentUser?.id ?: ""
                    )
                    
                    if (financeResult is ApiResult.Success) {
                        _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                    } else {
                        // If finance fails, show why but don't hang
                        val errorMsg = (financeResult as? ApiResult.Error)?.message ?: "Unknown Finance Error"
                        _uiState.update { it.copy(isLoading = false, error = "Member Added, but Finance failed: $errorMsg") }
                        // Optional: Still navigate back after a delay or let user see error
                    }
                }
                is ApiResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
            }
        }
    }
}
