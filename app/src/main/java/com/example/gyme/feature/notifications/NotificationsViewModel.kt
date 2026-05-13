package com.example.gyme.feature.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gyme.core.model.*
import com.example.gyme.util.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class NotificationsUiState {
    object Loading : NotificationsUiState()
    data class Success(
        val notifications: List<Notification>,
        val filter: NotificationFilter = NotificationFilter.ALL
    ) : NotificationsUiState()
    data class Error(val message: String) : NotificationsUiState()
}

enum class NotificationFilter {
    ALL, UNREAD
}

class NotificationsViewModel(
    private val repository: NotificationsRepository = NotificationsRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<NotificationsUiState>(NotificationsUiState.Loading)
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    private var allNotifications: List<Notification> = emptyList()
    private var currentFilter: NotificationFilter = NotificationFilter.ALL

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.value = NotificationsUiState.Loading
            // In a real app, we'd get the current user ID
            when (val result = repository.getAll()) {
                is ApiResult.Success -> {
                    allNotifications = result.data
                    applyFilter()
                }
                is ApiResult.Error -> {
                    _uiState.value = NotificationsUiState.Error(result.message)
                }
            }
        }
    }

    fun setFilter(filter: NotificationFilter) {
        currentFilter = filter
        applyFilter()
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            repository.markAsRead(notificationId)
            allNotifications = allNotifications.map {
                if (it.id == notificationId) it.copy(isRead = true) else it
            }
            applyFilter()
        }
    }

    private fun applyFilter() {
        val filtered = when (currentFilter) {
            NotificationFilter.ALL -> allNotifications
            NotificationFilter.UNREAD -> allNotifications.filter { !it.isRead }
        }
        _uiState.value = NotificationsUiState.Success(filtered, currentFilter)
    }

}
