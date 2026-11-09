package com.trackmyboot.app.data.source

import com.trackmyboot.app.data.model.Offer
import com.trackmyboot.app.data.model.StoreInfo
import kotlinx.serialization.json.JsonElement
import java.net.URLEncoder

/** Lojas na plataforma VTEX expõem a API pública de catálogo. */
class VtexSource(override val store: StoreInfo, private val http: Http) : StoreSource {
    private val base = store.baseUrl.trimEnd('/')

    override suspend fun search(term: String, limit: Int): List<Offer> {
        val q = URLEncoder.encode(term, "UTF-8")
        val result = http.getJson("$base/api/catalog_system/pub/products/search?ft=$q&_from=0&_to=${limit - 1}")
        return result.arr.mapNotNull(::parse)
    }

    override suspend fun refresh(productRef: String): Offer? {
        val result = http.getJson("$base/api/catalog_system/pub/products/search?fq=productId:$productRef")
        return result.arr.firstNotNullOfOrNull(::parse)
    }

    private fun parse(p: JsonElement): Offer? {
        val id = p["productId"].str ?: return null
        val title = p["productName"].str ?: return null
        // Usa o item (SKU) disponível com o menor preço.
        val candidates = p["items"].arr.flatMap { item ->
            item["sellers"].arr.mapNotNull { seller ->
                val offer = seller["commertialOffer"]
                val price = offer["Price"].num ?: return@mapNotNull null
                Triple(item, price, offer)
            }
        }.filter { it.second > 0 }
        val available = candidates.filter { (it.third["AvailableQuantity"].num ?: 0.0) > 0 }
        val best = (available.ifEmpty { candidates }).minByOrNull { it.second } ?: return null
        val listPrice = best.third["ListPrice"].num?.takeIf { it > best.second }
        val link = p["link"].str ?: "$base/${p["linkText"].str}/p"
        return Offer(
            id = store.offerId(id),
            storeId = store.id,
            storeName = store.name,
            productRef = id,
            title = title,
            brand = p["brand"].str,
            price = best.second,
            listPrice = listPrice,
            imageUrl = best.first["images"][0]["imageUrl"].str,
            url = link,
            available = available.isNotEmpty(),
        )
    }
}
