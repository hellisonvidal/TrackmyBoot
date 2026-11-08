package com.trackmyboot.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trackmyboot.app.data.repository.AlertRepository
import com.trackmyboot.app.domain.model.Alert
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class AlertsViewModel @Inject constructor(
    private val alertRepository: AlertRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: Flow<Boolean> = _isLoading.distinctUntilChanged()

    val unreadAlerts: Flow<List<Alert>> = alertRepository.getUnreadAlerts().distinctUntilChanged()
    val recentAlerts: Flow<List<Alert>> = alertRepository.getRecentAlerts(50).distinctUntilChanged()
    val allAlerts: Flow<List<Alert>> = alertRepository.getAllAlerts().distinctUntilChanged()

    val unreadCount: Flow<Int> = alertRepository.getUnreadCount().distinctUntilChanged()

    fun markAsRead(alertId: Long) {
        viewModelScope.launch {
            alertRepository.markAsRead(alertId)
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            _isLoading.value = true
            alertRepository.markAllAsRead()
                .also { _isLoading.value = false }
        }
    }

    fun deleteOldAlerts(days: Int = 30) {
        viewModelScope.launch {
            val cutoffDate = System.currentTimeMillis() - (days * 24L * 60 * 60 * 1000)
            alertRepository.deleteOldAlerts(cutoffDate)
        }
    }
}