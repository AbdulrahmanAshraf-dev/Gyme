package com.example.gyme.feature.attendance.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gyme.feature.members.MembersRepository
import com.example.gyme.feature.attendance.repo.AttendanceRepository
import com.example.gyme.util.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class AttendanceUiState {
    object Idle : AttendanceUiState()
    object Scanning : AttendanceUiState()
    object Loading : AttendanceUiState()
    data class Success(val memberName: String) : AttendanceUiState()
    data class SubscriptionExpired(val memberName: String) : AttendanceUiState()
    data class Error(val message: String) : AttendanceUiState()
}

class AttendanceViewModel(
    private val attendanceRepository: AttendanceRepository = AttendanceRepository(),
    private val membersRepository: MembersRepository = MembersRepository()
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
            
            val memberResult = membersRepository.getById(barcode)
            if (memberResult is ApiResult.Success) {
                val member = memberResult.data
                val now = Date()
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US)
                val expiryDate = try { member.subscriptionEnd?.let { sdf.parse(it) } } catch (e: Exception) { null }

                if (expiryDate != null && expiryDate.before(now)) {
                    _uiState.value = AttendanceUiState.SubscriptionExpired(member.name)
                } else {
                    // Perform check-in
                    val checkInResult = attendanceRepository.checkIn(barcode, now.toString())
                    if (checkInResult is ApiResult.Success) {
                        _uiState.value = AttendanceUiState.Success(member.name)
                    } else {
                        _uiState.value = AttendanceUiState.Error("Check-in failed")
                    }
                }
            } else {
                _uiState.value = AttendanceUiState.Error("Member not found")
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
