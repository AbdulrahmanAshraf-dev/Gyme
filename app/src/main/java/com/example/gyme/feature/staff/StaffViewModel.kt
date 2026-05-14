package com.example.gyme.feature.staff

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gyme.core.model.*
import com.example.gyme.util.ApiResult
import com.example.gyme.feature.login.LoginRepository
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.gyme.BuildConfig
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

    fun loadStaffData() {
        viewModelScope.launch {
            _uiState.value = StaffUiState.Loading
            
            val result = repository.getAll()
            if (result is ApiResult.Success) {
                val users = result.data
                val staffList = users.map { 
                    StaffMember(
                        id = it.id,
                        name = it.name ?: "Unknown",
                        role = (it.role ?: "staff").uppercase(),
                        email = it.email ?: "",
                        isAccessEnabled = it.role != "none",
                        canAddMember = it.canAddMember,
                        canEditMember = it.canEditMember,
                        canDeleteMember = it.canDeleteMember,
                        canManageFinance = it.canManageSubscriptions
                    )
                }

                val stats = StaffStats(
                    totalActiveStaff = users.size,
                    growthText = "",
                    onShiftNow = users.size,
                    pendingRequests = 0
                )

                val topTrainer = staffList.firstOrNull { it.role == "TRAINER" } ?: staffList.firstOrNull() ?: StaffMember(
                    "0", "Guest", "STAFF", "", false
                )

                _uiState.value = StaffUiState.Success(stats, topTrainer, staffList)
            } else if (result is ApiResult.Error) {
                _uiState.value = StaffUiState.Error(result.message)
            } else {
                _uiState.value = StaffUiState.Error("Failed to load staff")
            }
        }
    }

    fun onToggleAccess(staffId: String, isEnabled: Boolean) {
        viewModelScope.launch {
            repository.updateUserRole(staffId, if (isEnabled) "staff" else "none")
            loadStaffData()
        }
    }

    fun togglePermission(staffId: String, permission: String, isEnabled: Boolean) {
        viewModelScope.launch {
            repository.updatePermission(staffId, permission, isEnabled)
            loadStaffData()
        }
    }

    fun onDeleteStaff(staffId: String) {
        viewModelScope.launch {
            repository.delete(staffId)
            loadStaffData()
        }
    }

    fun onSearchQueryChanged(query: String) {
    }

    fun createStaffAccount(name: String, email: String, password: String, permissions: Map<String, Boolean>) {
        val cleanName = name.trim()
        val cleanEmail = email.trim()
        
        viewModelScope.launch {
            _uiState.value = StaffUiState.Loading
            
            try {
                val tempClient = createSupabaseClient(
                    supabaseUrl = BuildConfig.SUPABASE_URL,
                    supabaseKey = BuildConfig.SUPABASE_API_KEY
                ) {
                    install(io.github.jan.supabase.auth.Auth)
                    install(Postgrest)
                }

                tempClient.auth.signUpWith(Email) {
                    this.email = cleanEmail
                    this.password = password
                }
                
                val newUserId = tempClient.auth.currentUserOrNull()?.id
                
                if (newUserId != null) {
                    repository.updateUserName(newUserId, cleanName)
                    repository.updateUserRole(newUserId, "staff")
                    repository.updatePermission(newUserId, "add", permissions["add"] ?: false)
                    repository.updatePermission(newUserId, "edit", permissions["edit"] ?: false)
                    repository.updatePermission(newUserId, "delete", permissions["delete"] ?: false)
                    repository.updatePermission(newUserId, "finance", permissions["finance"] ?: false)
                    
                    loadStaffData()
                    
                    tempClient.close()
                } else {
                    _uiState.value = StaffUiState.Error("Failed to retrieve new user ID")
                }
            } catch (e: Exception) {
                _uiState.value = StaffUiState.Error(e.message ?: "Failed to create account")
            }
        }
    }
}
