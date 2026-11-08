package com.trackmyboot.app.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "app_settings")
@Serializable
data class AppSettings(
    @PrimaryKey
    val key: String,
    val value: String
) {
    companion object {
        const val KEY_PRICE_CHECK_INTERVAL_HOURS = "price_check_interval_hours"
        const val KEY_AUTO_SYNC_ENABLED = "auto_sync_enabled"
        const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        const val KEY_THEME_MODE = "theme_mode"
        const val KEY_LANGUAGE = "language"
        const val KEY_LAST_SYNC_TIME = "last_sync_time"
        const val KEY_FIRST_LAUNCH = "first_launch"
        const val KEY_CATEGORIES_ENABLED = "categories_enabled"
        const val KEY_STORES_ENABLED = "stores_enabled"
        const val KEY_MIN_DISCOUNT_PERCENTAGE = "min_discount_percentage"
    }
}