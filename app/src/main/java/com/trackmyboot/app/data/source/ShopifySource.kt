package com.trackmyboot.app.data.source

import com.trackmyboot.app.data.model.Offer
import com.trackmyboot.app.data.model.StoreInfo
import java.net.URLEncoder

/** Lojas Shopify: busca via /search/suggest.json e preço via /products/<handle>.js. */
class ShopifySource(override val store: StoreInfo, private val http: Http) : StoreSource {
    private val base = store.baseUrl.trimEnd('/')

    override suspend fun search(term: String, limit: Int): List<Offer> {
        val q = URLEncoder.encode(term, "UTF-8")
        val result = http.getJson("$base/search/suggest.json?q=$q&resources[type]=product&resources[limit]=${limit.coerceAtMost(10)}")
        return result["resources"]["results"]["products"].arr.mapNotNull { p ->
            val handle = p["handle"].str ?: return@mapNotNull null
            val price = p["price"].num ?: return@mapNotNull null
            Offer(
                id = store.offerId(handle),
                storeId = store.id,
                storeName = store.name,
                productRef = handle,
                title = p["title"].str ?: handle,
                brand = p["vendor"].str,
                price = price,
                listPrice = p["compare_at_price_max"].num?.takeIf { it > price },
                imageUrl = (p["image"].str ?: p["featured_image"]["url"].str)?.let(::absolute),
                url = absolute(p["url"].str ?: "/products/$handle"),
                available = p["available"].bool ?: true,
            )
        }
    }

    override suspend fun refresh(productRef: String): Offer? {
        val p = http.getJson("$base/products/$productRef.js")
        // Em /products/<handle>.js os preços vêm em centavos.
        val price = p["price"].num?.div(100) ?: return null
        return Offer(
            id = store.offerId(productRef),
            storeId = store.id,
            storeName = store.name,
            productRef = productRef,
            title = p["title"].str ?: productRef,
            brand = p["vendor"].str,
            price = price,
            listPrice = p["compare_at_price"].num?.div(100)?.takeIf { it > price },
            imageUrl = p["featured_image"].str?.let(::absolute),
            url = "$base/products/$productRef",
            available = p["available"].bool ?: true,
        )
    }

    private fun absolute(url: String) = when {
        url.startsWith("//") -> "https:$url"
        url.startsWith("/") -> base + url
        else -> url
    }
}
