package com.trackmyboot.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trackmyboot.app.data.repository.SettingsRepository
import com.trackmyboot.app.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    // Settings flows
    val priceCheckIntervalHours: Flow<Int> = settingsRepository.priceCheckIntervalHours.distinctUntilChanged()
    val autoSyncEnabled: Flow<Boolean> = settingsRepository.autoSyncEnabled.distinctUntilChanged()
    val notificationsEnabled: Flow<Boolean> = settingsRepository.notificationsEnabled.distinctUntilChanged()
    val themeMode: Flow<Int> = settingsRepository.themeMode.distinctUntilChanged()
    val language: Flow<String> = settingsRepository.language.distinctUntilChanged()
    val enabledCategories: Flow<String> = settingsRepository.enabledCategories.distinctUntilChanged()
    val enabledStores: Flow<String> = settingsRepository.enabledStores.distinctUntilChanged()
    val minDiscountPercentage: Flow<Int> = settingsRepository.minDiscountPercentage.distinctUntilChanged()

    // Available options
    val priceCheckIntervals = listOf(1, 3, 6, 12, 24)
    val themeModes = listOf(
        ThemeOption.SYSTEM,
        ThemeOption.LIGHT,
        ThemeOption.DARK
    )
    val languages = listOf(
        LanguageOption("pt", "Português"),
        LanguageOption("en", "English"),
        LanguageOption("es", "Español")
    )

    val allCategories = com.trackmyboot.app.domain.model.ProductCategory.values().toList()
    val allStores = com.trackmyboot.app.domain.model.Store.values().toList()

    fun setPriceCheckInterval(hours: Int) {
        viewModelScope.launch {
            settingsRepository.setPriceCheckIntervalHours(hours)
        }
    }

    fun setAutoSyncEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setAutoSyncEnabled(enabled)
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setNotificationsEnabled(enabled)
        }
    }

    fun setThemeMode(mode: Int) {
        viewModelScope.launch {
            settingsRepository.setThemeMode(mode)
        }
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch {
            settingsRepository.setLanguage(lang)
        }
    }

    fun setEnabledCategories(categories: List<String>) {
        viewModelScope.launch {
            settingsRepository.setEnabledCategories(categories)
        }
    }

    fun setEnabledStores(stores: List<String>) {
        viewModelScope.launch {
            settingsRepository.setEnabledStores(stores)
        }
    }

    fun setMinDiscountPercentage(percentage: Int) {
        viewModelScope.launch {
            settingsRepository.setMinDiscountPercentage(percentage)
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            // Clear cached data - implementation would clear database or specific tables
        }
    }

    fun exportData() {
        viewModelScope.launch {
            // Export data implementation
        }
    }
}

enum class ThemeOption(val value: Int, val label: String) {
    SYSTEM(0, "Sistema"),
    LIGHT(1, "Claro"),
    DARK(2, "Escuro")
}

data class LanguageOption(val code: String, val name: String)