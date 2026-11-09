package com.trackmyboot.app.data.source

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.doubleOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.TimeUnit

class StoreHttpException(val code: Int, message: String) : IOException(message)

/** Cliente HTTP compartilhado pelos conectores de loja. */
class Http(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .followRedirects(true)
        .build(),
) {
    val json = Json { ignoreUnknownKeys = true; isLenient = true }

    suspend fun getJson(url: String): JsonElement = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(url)
            .header("Accept", "application/json")
            .header("User-Agent", "TrackmyBoot/0.1 (Android; monitor de precos)")
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw StoreHttpException(response.code, "HTTP ${response.code}")
            val body = response.body?.string().orEmpty()
            json.parseToJsonElement(body)
        }
    }
}

// Pequenos utilitários para navegar no JSON sem modelos rígidos (cada loja muda um pouco).
operator fun JsonElement?.get(key: String): JsonElement? = (this as? JsonObject)?.get(key)
operator fun JsonElement?.get(index: Int): JsonElement? = (this as? JsonArray)?.getOrNull(index)
val JsonElement?.arr: List<JsonElement> get() = (this as? JsonArray) ?: emptyList()
val JsonElement?.str: String? get() = (this as? JsonPrimitive)?.takeIf { this !is JsonNull }?.content
val JsonElement?.num: Double? get() = (this as? JsonPrimitive)?.takeIf { this !is JsonNull }?.let { it.doubleOrNull ?: it.content.replace(",", ".").toDoubleOrNull() }
val JsonElement?.bool: Boolean? get() = (this as? JsonPrimitive)?.booleanOrNull
