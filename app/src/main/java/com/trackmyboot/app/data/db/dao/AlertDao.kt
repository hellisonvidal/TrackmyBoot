package com.trackmyboot.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.trackmyboot.app.domain.model.Alert
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(alert: Alert): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(alerts: List<Alert>)

    @Update
    suspend fun update(alert: Alert): Int

    @Query("SELECT * FROM alerts WHERE id = :id")
    fun getById(id: Long): Flow<Alert?>

    @Query("SELECT * FROM alerts WHERE productId = :productId ORDER BY createdAt DESC")
    fun getAlertsForProduct(productId: String): Flow<List<Alert>>

    @Query("SELECT * FROM alerts WHERE isRead = 0 ORDER BY createdAt DESC")
    fun getUnreadAlerts(): Flow<List<Alert>>

    @Query("SELECT * FROM alerts ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentAlerts(limit: Int): Flow<List<Alert>>

    @Query("SELECT * FROM alerts ORDER BY createdAt DESC")
    fun getAllAlerts(): Flow<List<Alert>>

    @Query("UPDATE alerts SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long): Int

    @Query("UPDATE alerts SET isRead = 1")
    suspend fun markAllAsRead(): Int

    @Query("DELETE FROM alerts WHERE createdAt < :cutoffDate")
    suspend fun deleteOldAlerts(cutoffDate: Long): Int

    @Query("SELECT COUNT(*) FROM alerts WHERE isRead = 0")
    suspend fun getUnreadCount(): Int
}