package com.trackmyboot.app.data.repository

import com.trackmyboot.app.data.db.dao.AlertDao
import com.trackmyboot.app.domain.model.Alert
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlertRepository @Inject constructor(
    private val alertDao: AlertDao
) {
    fun getUnreadAlerts(): Flow<List<Alert>> = alertDao.getUnreadAlerts()

    fun getRecentAlerts(limit: Int): Flow<List<Alert>> = alertDao.getRecentAlerts(limit)

    fun getAllAlerts(): Flow<List<Alert>> = alertDao.getAllAlerts()

    fun getAlertsForProduct(productId: String): Flow<List<Alert>> = alertDao.getAlertsForProduct(productId)

    suspend fun createAlert(alert: Alert): Long = alertDao.insert(alert)

    suspend fun createAlerts(alerts: List<Alert>) = alertDao.insertAll(alerts)

    suspend fun markAsRead(id: Long): Int = alertDao.markAsRead(id)

    suspend fun markAllAsRead(): Int = alertDao.markAllAsRead()

    suspend fun deleteOldAlerts(cutoffDate: Long) = alertDao.deleteOldAlerts(cutoffDate)

    suspend fun getUnreadCount(): Int = alertDao.getUnreadCount()
}