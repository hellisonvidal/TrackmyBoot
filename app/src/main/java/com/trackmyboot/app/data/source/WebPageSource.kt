package com.trackmyboot.app.data.source

import com.trackmyboot.app.data.model.Offer
import com.trackmyboot.app.data.model.StoreInfo
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.net.URI

/**
 * Conector genérico para lojas sem API pública: abre a página de busca da loja e lê os dados de
 * produto que a própria página traz embutidos (JSON-LD do schema.org, `__NEXT_DATA__` do Next.js
 * e outros blocos JSON de estado). Não depende do layout visual da página.
 */
class WebPageSource(override val store: StoreInfo, private val http: Http) : StoreSource {

    override suspend fun search(term: String, limit: Int): List<Offer> {
        val url = store.searchLink(term) ?: return emptyList()
        return WebProductExtractor.extract(http.getText(url), url).take(limit).map(::toOffer)
    }

    /** Em lojas WEB a referência do produto é o próprio endereço da página dele. */
    override suspend fun refresh(productRef: String): Offer? =
        WebProductExtractor.extract(http.getText(productRef), productRef).firstOrNull()?.let(::toOffer)?.copy(
            id = store.offerId(productRef),
            productRef = productRef,
            url = productRef,
        )

    private fun toOffer(p: WebProductExtractor.Product) = Offer(
        id = store.offerId(p.url),
        storeId = store.id,
        storeName = store.name,
        productRef = p.url,
        title = p.name,
        brand = p.brand,
        price = p.price,
        listPrice = p.listPrice?.takeIf { it > p.price },
        imageUrl = p.image,
        url = p.url,
        available = p.available,
    )
}

object WebProductExtractor {
    data class Product(
        val name: String,
        val url: String,
        val price: Double,
        val listPrice: Double?,
        val image: String?,
        val brand: String?,
        val available: Boolean,
    )

    private val json = Json { isLenient = true; ignoreUnknownKeys = true }
    private val scriptRegex = Regex("<script([^>]*)>(.*?)</script>", setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.IGNORE_CASE))
    private val stateAssign = Regex("^\\s*(?:window\\.)?[A-Za-z_$][\\w$.]*\\s*=\\s*(\\{.*\\})\\s*;?\\s*$", RegexOption.DOT_MATCHES_ALL)

    private val nameKeys = listOf("name", "productName", "title", "displayName")
    private val urlKeys = listOf("url", "link", "productUrl", "href", "permalink", "canonicalUrl", "detailUrl", "path")
    private val priceKeys = listOf("price", "salePrice", "bestPrice", "sellingPrice", "finalPrice", "currentPrice", "lowPrice", "priceValue", "offerPrice", "promotionalPrice")
    private val listPriceKeys = listOf("listPrice", "originalPrice", "oldPrice", "fullPrice", "priceFrom", "regularPrice", "priceWithoutDiscount", "highPrice")
    private val imageKeys = listOf("image", "imageUrl", "thumbnail", "thumbnailUrl", "img", "images", "picture", "mainImage")

    fun extract(html: String, pageUrl: String): List<Product> {
        val found = LinkedHashMap<String, Product>()
        scriptRegex.findAll(html).forEach { match ->
            val attrs = match.groupValues[1].lowercase()
            val body = match.groupValues[2].trim()
            val text = when {
                "ld+json" in attrs || "application/json" in attrs || "__next_data__" in attrs -> body
                else -> stateAssign.matchEntire(body)?.groupValues?.get(1) ?: return@forEach
            }
            val element = runCatching { json.parseToJsonElement(text) }.getOrNull() ?: return@forEach
            walk(element, pageUrl, found, depth = 0)
        }
        return found.values.toList()
    }

    private fun walk(e: JsonElement, base: String, out: MutableMap<String, Product>, depth: Int) {
        if (depth > 40) return
        when (e) {
            is JsonObject -> {
                toProduct(e, base)?.let { p -> out.putIfAbsent(p.url, p) }
                e.values.forEach { walk(it, base, out, depth + 1) }
            }
            is JsonArray -> e.forEach { walk(it, base, out, depth + 1) }
            else -> Unit
        }
    }

    private fun toProduct(o: JsonObject, base: String): Product? {
        val type = o["@type"].str
        if (type != null && type != "Product" && type != "ProductGroup") return null
        val name = nameKeys.firstNotNullOfOrNull { o[it].str?.trim() }?.takeIf { it.length >= 4 } ?: return null
        val offers = o["offers"].let { if (it is JsonArray) it.firstOrNull() else it }
        val price = priceKeys.firstNotNullOfOrNull { priceOf(o[it]) }
            ?: offers?.let { off -> priceKeys.firstNotNullOfOrNull { priceOf(off[it]) } }
            ?: return null
        if (price <= 0 || price > 100_000) return null
        val rawUrl = urlKeys.firstNotNullOfOrNull { o[it].str?.takeIf { s -> s.isNotBlank() } }
            ?: offers["url"].str
            ?: return null
        val url = absolute(rawUrl, base) ?: return null
        val listPrice = listPriceKeys.firstNotNullOfOrNull { priceOf(o[it]) }
            ?: offers?.let { off -> listPriceKeys.firstNotNullOfOrNull { priceOf(off[it]) } }
        val image = imageKeys.firstNotNullOfOrNull { imageOf(o[it]) }?.let { absolute(it, base) }
        val brand = o["brand"].let { it.str ?: it["name"].str }
        val availability = offers["availability"].str ?: o["availability"].str
        val available = (o["available"].bool ?: o["isAvailable"].bool)
            ?: (availability?.contains("OutOfStock", ignoreCase = true) != true)
        return Product(name, url, price, listPrice, image, brand, available)
    }

    /** Aceita 199.9, "199.90", "R$ 1.299,90" ou objetos como {"value": 199.9}. */
    internal fun priceOf(e: JsonElement?): Double? = when (e) {
        null -> null
        is JsonPrimitive -> if (e.isString) parsePrice(e.content) else e.content.toDoubleOrNull()
        is JsonObject -> listOf("value", "amount", "current", "sale", "price", "min").firstNotNullOfOrNull { priceOf(e[it]) }
        else -> null
    }

    internal fun parsePrice(raw: String): Double? {
        val s = raw.replace(Regex("[^0-9,.]"), "")
        if (s.isEmpty()) return null
        val normalized = when {
            ',' in s && '.' in s -> if (s.lastIndexOf(',') > s.lastIndexOf('.')) s.replace(".", "").replace(',', '.') else s.replace(",", "")
            ',' in s -> s.replace(',', '.')
            else -> s
        }
        return normalized.toDoubleOrNull()
    }

    private fun imageOf(e: JsonElement?): String? = when (e) {
        is JsonPrimitive -> e.content.takeIf { e.isString && (it.startsWith("http") || it.startsWith("/")) }
        is JsonArray -> e.firstNotNullOfOrNull { imageOf(it) }
        is JsonObject -> imageOf(e["url"]) ?: imageOf(e["src"]) ?: imageOf(e["imageUrl"])
        else -> null
    }

    private fun absolute(url: String, base: String): String? = runCatching {
        val u = if (url.startsWith("//")) "https:$url" else url
        URI(base).resolve(u).toString().takeIf { it.startsWith("http") }
    }.getOrNull()
}
