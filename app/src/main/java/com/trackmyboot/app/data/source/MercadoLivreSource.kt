package com.trackmyboot.app.data.source

import com.trackmyboot.app.data.model.Offer
import com.trackmyboot.app.data.model.StoreInfo
import kotlinx.serialization.json.JsonElement
import java.net.URLEncoder

/** API pública do Mercado Livre Brasil (site MLB). */
class MercadoLivreSource(override val store: StoreInfo, private val http: Http) : StoreSource {
    override suspend fun search(term: String, limit: Int): List<Offer> {
        val q = URLEncoder.encode(term, "UTF-8")
        val result = http.getJson("https://api.mercadolibre.com/sites/MLB/search?q=$q&limit=$limit")
        return result["results"].arr.mapNotNull(::parse)
    }

    override suspend fun refresh(productRef: String): Offer? =
        parse(http.getJson("https://api.mercadolibre.com/items/$productRef"))

    private fun parse(item: JsonElement): Offer? {
        val id = item["id"].str ?: return null
        val price = item["price"].num ?: return null
        val brand = item["attributes"].arr.firstOrNull { it["id"].str == "BRAND" }?.get("value_name").str
        return Offer(
            id = store.offerId(id),
            storeId = store.id,
            storeName = store.name,
            productRef = id,
            title = item["title"].str ?: id,
            brand = brand,
            price = price,
            listPrice = item["original_price"].num?.takeIf { it > price },
            imageUrl = (item["thumbnail"].str ?: item["pictures"][0]["secure_url"].str)?.replace("http://", "https://"),
            url = item["permalink"].str ?: "https://www.mercadolivre.com.br",
            available = (item["available_quantity"].num ?: 1.0) > 0,
        )
    }
}
