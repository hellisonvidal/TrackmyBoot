package com.trackmyboot.app.data.repository

import com.trackmyboot.app.data.db.dao.AppSettingsDao
import com.trackmyboot.app.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val settingsDao: AppSettingsDao
) {
    private fun getString(key: String, defaultValue: String): Flow<String> =
        settingsDao.getSetting(key)
            .map { it?.value ?: defaultValue }
            .distinctUntilChanged()

    private fun getBoolean(key: String, defaultValue: Boolean): Flow<Boolean> =
        settingsDao.getSetting(key)
            .map { it?.value?.toBoolean() ?: defaultValue }
            .distinctUntilChanged()

    private fun getInt(key: String, defaultValue: Int): Flow<Int> =
        settingsDao.getSetting(key)
            .map { it?.value?.toInt() ?: defaultValue }
            .distinctUntilChanged()

    private suspend fun setString(key: String, value: String) {
        settingsDao.insert(AppSettings(key, value))
    }

    private suspend fun setBoolean(key: String, value: Boolean) {
        settingsDao.insert(AppSettings(key, value.toString()))
    }

    private suspend fun setInt(key: String, value: Int) {
        settingsDao.insert(AppSettings(key, value.toString()))
    }

    // Price check interval in hours
    val priceCheckIntervalHours: Flow<Int> = getInt(AppSettings.KEY_PRICE_CHECK_INTERVAL_HOURS, 6)

    suspend fun setPriceCheckIntervalHours(hours: Int) = setInt(AppSettings.KEY_PRICE_CHECK_INTERVAL_HOURS, hours)

    // Auto sync enabled
    val autoSyncEnabled: Flow<Boolean> = getBoolean(AppSettings.KEY_AUTO_SYNC_ENABLED, true)

    suspend fun setAutoSyncEnabled(enabled: Boolean) = setBoolean(AppSettings.KEY_AUTO_SYNC_ENABLED, enabled)

    // Notifications enabled
    val notificationsEnabled: Flow<Boolean> = getBoolean(AppSettings.KEY_NOTIFICATIONS_ENABLED, true)

    suspend fun setNotificationsEnabled(enabled: Boolean) = setBoolean(AppSettings.KEY_NOTIFICATIONS_ENABLED, enabled)

    // Theme mode (0 = system, 1 = light, 2 = dark)
    val themeMode: Flow<Int> = getInt(AppSettings.KEY_THEME_MODE, 0)

    suspend fun setThemeMode(mode: Int) = setInt(AppSettings.KEY_THEME_MODE, mode)

    // Language
    val language: Flow<String> = getString(AppSettings.KEY_LANGUAGE, "pt")

    suspend fun setLanguage(lang: String) = setString(AppSettings.KEY_LANGUAGE, lang)

    // Last sync time
    val lastSyncTime: Flow<Long> = getString(AppSettings.KEY_LAST_SYNC_TIME, "0").map { it.toLong() }

    suspend fun setLastSyncTime(time: Long) = setString(AppSettings.KEY_LAST_SYNC_TIME, time.toString())

    // First launch
    val isFirstLaunch: Flow<Boolean> = getBoolean(AppSettings.KEY_FIRST_LAUNCH, true)

    suspend fun setFirstLaunchCompleted() = setBoolean(AppSettings.KEY_FIRST_LAUNCH, false)

    // Enabled categories
    val enabledCategories: Flow<String> = getString(AppSettings.KEY_CATEGORIES_ENABLED, "")

    suspend fun setEnabledCategories(categories: List<String>) =
        setString(AppSettings.KEY_CATEGORIES_ENABLED, categories.joinToString(","))

    // Enabled stores
    val enabledStores: Flow<String> = getString(AppSettings.KEY_STORES_ENABLED, "")

    suspend fun setEnabledStores(stores: List<String>) =
        setString(AppSettings.KEY_STORES_ENABLED, stores.joinToString(","))

    // Minimum discount percentage
    val minDiscountPercentage: Flow<Int> = getInt(AppSettings.KEY_MIN_DISCOUNT_PERCENTAGE, 10)

    suspend fun setMinDiscountPercentage(percentage: Int) = setInt(AppSettings.KEY_MIN_DISCOUNT_PERCENTAGE, percentage)
}