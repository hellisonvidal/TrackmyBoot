package com.trackmyboot.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.trackmyboot.app.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface AppSettingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(setting: AppSettings): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(settings: List<AppSettings>)

    @Update
    suspend fun update(setting: AppSettings): Int

    @Query("SELECT * FROM app_settings WHERE key = :key")
    fun getSetting(key: String): Flow<AppSettings?>

    @Query("SELECT * FROM app_settings WHERE key = :key")
    suspend fun getSettingSync(key: String): AppSettings?

    @Query("SELECT * FROM app_settings")
    fun getAllSettings(): Flow<List<AppSettings>>

    @Query("DELETE FROM app_settings WHERE key = :key")
    suspend fun deleteSetting(key: String): Int
}