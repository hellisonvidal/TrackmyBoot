package com.trackmyboot.app.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.trackmyboot.app.data.model.StoreKind
import com.trackmyboot.app.data.model.StoreInfo
import com.trackmyboot.app.data.model.StorePlatform
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

/** Loja cadastrada pelo usuário (por exemplo, a loja oficial do clube dele). */
@Serializable
data class CustomStore(
    val id: String,
    val name: String,
    val baseUrl: String,
    val platform: StorePlatform,
    val clubId: String? = null,
) {
    fun toStoreInfo() = StoreInfo(
        id, name, baseUrl, platform,
        if (clubId != null) StoreKind.CLUB else StoreKind.RETAILER,
        searchUrl = when (platform) {
            StorePlatform.SHOPIFY -> "$baseUrl/search?q={q}"
            else -> "$baseUrl/{q}?_q={q}&map=ft"
        },
    )
}

data class Settings(
    val favoriteClubs: Set<String>,
    /** Lojas desligadas pelo usuário (as demais ficam ligadas). */
    val disabledStores: Set<String>,
    val customStores: List<CustomStore>,
    val notificationsEnabled: Boolean,
)

class UserPrefs(private val context: Context) {
    private val favoritesKey = stringSetPreferencesKey("favorite_clubs")
    private val disabledKey = stringSetPreferencesKey("disabled_stores")
    private val customKey = stringPreferencesKey("custom_stores")
    private val notificationsKey = booleanPreferencesKey("notifications")
    private val json = Json { ignoreUnknownKeys = true }
    private val customSerializer = ListSerializer(CustomStore.serializer())

    val settings: Flow<Settings> = context.dataStore.data.map { p ->
        Settings(
            favoriteClubs = p[favoritesKey] ?: emptySet(),
            disabledStores = p[disabledKey] ?: emptySet(),
            customStores = p[customKey]?.let { runCatching { json.decodeFromString(customSerializer, it) }.getOrNull() } ?: emptyList(),
            notificationsEnabled = p[notificationsKey] ?: true,
        )
    }

    suspend fun current(): Settings = settings.first()

    suspend fun toggleFavorite(clubId: String) = context.dataStore.edit { p ->
        val set = p[favoritesKey] ?: emptySet()
        p[favoritesKey] = if (clubId in set) set - clubId else set + clubId
    }

    suspend fun setStoreEnabled(storeId: String, enabled: Boolean) = context.dataStore.edit { p ->
        val set = p[disabledKey] ?: emptySet()
        p[disabledKey] = if (enabled) set - storeId else set + storeId
    }

    suspend fun setNotifications(enabled: Boolean) = context.dataStore.edit { it[notificationsKey] = enabled }

    suspend fun addCustomStore(store: CustomStore) = context.dataStore.edit { p ->
        val list = p[customKey]?.let { runCatching { json.decodeFromString(customSerializer, it) }.getOrNull() } ?: emptyList()
        p[customKey] = json.encodeToString(customSerializer, list.filterNot { it.id == store.id } + store)
    }

    suspend fun removeCustomStore(id: String) = context.dataStore.edit { p ->
        val list = p[customKey]?.let { runCatching { json.decodeFromString(customSerializer, it) }.getOrNull() } ?: emptyList()
        p[customKey] = json.encodeToString(customSerializer, list.filterNot { it.id == id })
    }
}
