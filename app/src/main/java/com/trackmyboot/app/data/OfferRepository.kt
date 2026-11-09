package com.trackmyboot.app.data

import com.trackmyboot.app.data.catalog.StoreCatalog
import com.trackmyboot.app.data.local.PricePoint
import com.trackmyboot.app.data.local.WatchDao
import com.trackmyboot.app.data.local.WatchedOffer
import com.trackmyboot.app.data.model.Category
import com.trackmyboot.app.data.model.Club
import com.trackmyboot.app.data.model.Offer
import com.trackmyboot.app.data.model.StoreInfo
import com.trackmyboot.app.data.model.StorePlatform
import com.trackmyboot.app.data.prefs.Settings
import com.trackmyboot.app.data.prefs.UserPrefs
import com.trackmyboot.app.data.source.Http
import com.trackmyboot.app.data.source.MercadoLivreSource
import com.trackmyboot.app.data.source.ShopifySource
import com.trackmyboot.app.data.source.StoreSource
import com.trackmyboot.app.data.source.VtexSource
import com.trackmyboot.app.data.source.WebPageSource
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.ConcurrentHashMap

/** O que o usuário quer ver: texto livre, categoria, clube ou combinação. */
data class OfferQuery(val text: String = "", val category: Category? = null, val club: Club? = null) {
    val searchTerm: String
        get() = listOfNotNull(
            category?.searchTerm?.takeIf { text.isBlank() || category.keywords.none { text.lowercase().contains(it) } },
            club?.name,
            text.takeIf { it.isNotBlank() },
        ).joinToString(" ").ifBlank { "futebol" }

    fun accepts(offer: Offer): Boolean =
        (category == null || category.matches(offer.title)) && (club == null || club.matches(offer.title))
}

data class StoreStatus(val store: StoreInfo, val count: Int, val error: String?)

data class SearchResult(val term: String, val offers: List<Offer>, val statuses: List<StoreStatus>) {
    /** Lojas consultadas que falharam (bloqueio, fora do ar, mudança de plataforma...). */
    val failedStores get() = statuses.filter { it.error != null && it.store.platform != StorePlatform.LINK }

    /** Lojas sem ofertas capturadas: o usuário pode abrir a busca direto no site/app delas. */
    val storesToOpen get() = statuses.filter { it.count == 0 && it.store.searchUrl != null }.map { it.store }
}

class OfferRepository(
    private val http: Http,
    private val prefs: UserPrefs,
    private val watchDao: WatchDao,
) {
    private val cache = ConcurrentHashMap<String, Pair<Long, SearchResult>>()

    fun allStores(settings: Settings): List<StoreInfo> =
        StoreCatalog.builtIn + settings.customStores.map { it.toStoreInfo() }

    private fun sourceFor(store: StoreInfo): StoreSource? = when (store.platform) {
        StorePlatform.VTEX -> VtexSource(store, http)
        StorePlatform.SHOPIFY -> ShopifySource(store, http)
        StorePlatform.MERCADO_LIVRE -> MercadoLivreSource(store, http)
        StorePlatform.WEB -> WebPageSource(store, http)
        StorePlatform.LINK -> null
    }

    /** Consulta todas as lojas ligadas em paralelo e junta as ofertas. */
    suspend fun search(query: OfferQuery, forceRefresh: Boolean = false): SearchResult {
        val settings = prefs.current()
        val stores = allStores(settings).filter { it.id !in settings.disabledStores }
            // Loja de clube só entra na busca do próprio clube ou em buscas livres.
            .filter { store ->
                val clubId = settings.customStores.firstOrNull { it.id == store.id }?.clubId
                clubId == null || query.club == null || clubId == query.club.id
            }
        val key = "${query.searchTerm}|${query.category}|${query.club?.id}|${stores.joinToString { it.id }}"
        if (!forceRefresh) cache[key]?.takeIf { System.currentTimeMillis() - it.first < CACHE_MS }?.let { return it.second }

        val perStore = coroutineScope {
            stores.map { store ->
                async {
                    val source = sourceFor(store)
                        ?: return@async StoreStatus(store, 0, "sem captura automática") to emptyList()
                    val outcome = withTimeoutOrNull(TIMEOUT_MS) {
                        runCatching { source.search(query.searchTerm) }
                    }
                    when {
                        outcome == null -> StoreStatus(store, 0, "tempo esgotado") to emptyList()
                        outcome.isFailure -> StoreStatus(store, 0, outcome.exceptionOrNull()?.message ?: "erro") to emptyList()
                        else -> {
                            val offers = outcome.getOrThrow().filter(query::accepts)
                            StoreStatus(store, offers.size, null) to offers
                        }
                    }
                }
            }.awaitAll()
        }
        val result = SearchResult(
            term = query.searchTerm,
            offers = perStore.flatMap { it.second }.distinctBy { it.id },
            statuses = perStore.map { it.first },
        )
        cache[key] = System.currentTimeMillis() to result
        return result
    }

    // ---------- Monitoramento ----------

    fun observeWatched(): Flow<List<WatchedOffer>> = watchDao.observeAll()
    fun observeWatchedIds(): Flow<List<String>> = watchDao.observeIds()
    fun observeHistory(): Flow<List<PricePoint>> = watchDao.observeAllPoints()

    suspend fun watch(offer: Offer, targetPrice: Double?) {
        val now = System.currentTimeMillis()
        watchDao.upsert(
            WatchedOffer(
                id = offer.id, storeId = offer.storeId, storeName = offer.storeName, productRef = offer.productRef,
                title = offer.title, brand = offer.brand, imageUrl = offer.imageUrl, url = offer.url,
                initialPrice = offer.price, currentPrice = offer.price, lowestPrice = offer.price, listPrice = offer.listPrice,
                targetPrice = targetPrice, available = offer.available, addedAt = now, lastCheckedAt = now,
            ),
        )
        watchDao.insertPoint(PricePoint(offerId = offer.id, price = offer.price, timestamp = now))
    }

    suspend fun unwatch(id: String) {
        watchDao.delete(id)
        watchDao.deleteHistory(id)
    }

    suspend fun updateTarget(item: WatchedOffer, target: Double?) = watchDao.upsert(item.copy(targetPrice = target))

    /** Resultado de uma reconsulta de preço, usado para gerar notificações. */
    data class PriceChange(val item: WatchedOffer, val oldPrice: Double, val newPrice: Double) {
        val hitTarget get() = item.targetPrice != null && newPrice <= item.targetPrice && oldPrice > item.targetPrice
        val dropped get() = newPrice < oldPrice
    }

    /** Reconsulta o preço de todos os produtos monitorados. */
    suspend fun refreshWatched(): List<PriceChange> {
        val settings = prefs.current()
        val stores = allStores(settings).associateBy { it.id }
        val changes = mutableListOf<PriceChange>()
        for (item in watchDao.getAll()) {
            val source = stores[item.storeId]?.let(::sourceFor) ?: continue
            val fresh = withTimeoutOrNull(TIMEOUT_MS) { runCatching { source.refresh(item.productRef) }.getOrNull() }
            val now = System.currentTimeMillis()
            if (fresh == null) {
                watchDao.upsert(item.copy(lastCheckedAt = now))
                continue
            }
            val updated = item.copy(
                currentPrice = fresh.price,
                lowestPrice = minOf(item.lowestPrice, fresh.price),
                listPrice = fresh.listPrice,
                available = fresh.available,
                imageUrl = fresh.imageUrl ?: item.imageUrl,
                lastCheckedAt = now,
            )
            watchDao.upsert(updated)
            if (fresh.price != item.currentPrice) {
                watchDao.insertPoint(PricePoint(offerId = item.id, price = fresh.price, timestamp = now))
                changes += PriceChange(updated, item.currentPrice, fresh.price)
            }
        }
        return changes
    }

    private companion object {
        const val CACHE_MS = 10 * 60 * 1000L
        const val TIMEOUT_MS = 20_000L
    }
}
