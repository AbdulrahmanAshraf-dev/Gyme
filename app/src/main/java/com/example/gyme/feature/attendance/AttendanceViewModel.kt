package com.example.gyme.feature.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gyme.domain.usecase.CheckInMemberUseCase
import com.example.gyme.domain.usecase.CheckInResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AttendanceUiState {
    object Idle : AttendanceUiState()
    object Scanning : AttendanceUiState()
    object Loading : AttendanceUiState()
    data class Success(val memberName: String) : AttendanceUiState()
    data class SubscriptionExpired(val memberName: String) : AttendanceUiState()
    data class Error(val message: String) : AttendanceUiState()
}

class AttendanceViewModel(
    private val checkInMemberUseCase: CheckInMemberUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AttendanceUiState>(AttendanceUiState.Scanning)
    val uiState: StateFlow<AttendanceUiState> = _uiState.asStateFlow()

    private val _isFlashOn = MutableStateFlow(false)
    val isFlashOn: StateFlow<Boolean> = _isFlashOn.asStateFlow()

    private var lastScannedId: String? = null

    fun onBarCodeScanned(barcode: String) {
        if (_uiState.value is AttendanceUiState.Loading || barcode == lastScannedId) return
        
        lastScannedId = barcode
        
        viewModelScope.launch {
            _uiState.value = AttendanceUiState.Loading
            
            when (val result = checkInMemberUseCase(barcode)) {
                is CheckInResult.Success -> {
                    _uiState.value = AttendanceUiState.Success(result.member.name)
                }
                is CheckInResult.SubscriptionExpired -> {
                    _uiState.value = AttendanceUiState.SubscriptionExpired(result.member.name)
                }
                is CheckInResult.Error -> {
                    _uiState.value = AttendanceUiState.Error(result.message)
                }
            }
        }
    }

    fun toggleFlash() {
        _isFlashOn.value = !_isFlashOn.value
    }

    fun resetState() {
        _uiState.value = AttendanceUiState.Scanning
        lastScannedId = null
    }
}
