package com.trackmyboot.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trackmyboot.app.AppContainer
import com.trackmyboot.app.data.OfferQuery
import com.trackmyboot.app.data.SearchResult
import com.trackmyboot.app.data.catalog.ClubCatalog
import com.trackmyboot.app.data.local.PricePoint
import com.trackmyboot.app.data.local.WatchedOffer
import com.trackmyboot.app.data.model.Category
import com.trackmyboot.app.data.model.Club
import com.trackmyboot.app.data.model.Offer
import com.trackmyboot.app.data.model.SortOrder
import com.trackmyboot.app.data.model.StoreInfo
import com.trackmyboot.app.data.model.StorePlatform
import com.trackmyboot.app.data.prefs.CustomStore
import com.trackmyboot.app.data.prefs.Settings
import com.trackmyboot.app.util.normalized
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private val EMPTY_SETTINGS = Settings(emptySet(), emptySet(), emptyList(), true)

/** Estado compartilhado: preferências e itens monitorados. */
class AppViewModel(private val container: AppContainer) : ViewModel() {
    private val repo = container.repository

    val settings: StateFlow<Settings> = container.prefs.settings.stateIn(viewModelScope, SharingStarted.Eagerly, EMPTY_SETTINGS)
    val watchedIds: StateFlow<Set<String>> = repo.observeWatchedIds().map { it.toSet() }.stateIn(viewModelScope, SharingStarted.Eagerly, emptySet())
    val watched: StateFlow<List<WatchedOffer>> = repo.observeWatched().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val history: StateFlow<Map<String, List<PricePoint>>> = repo.observeHistory().map { pts -> pts.groupBy { it.offerId } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())

    val favoriteClubs: StateFlow<List<Club>> = settings.map { s -> ClubCatalog.clubs.filter { it.id in s.favoriteClubs } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val stores: StateFlow<List<StoreInfo>> = settings.map { repo.allStores(it) }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun toggleFavorite(club: Club) = viewModelScope.launch { container.prefs.toggleFavorite(club.id) }
    fun watch(offer: Offer, target: Double?) = viewModelScope.launch { repo.watch(offer, target) }
    fun unwatch(id: String) = viewModelScope.launch { repo.unwatch(id) }
    fun updateTarget(item: WatchedOffer, target: Double?) = viewModelScope.launch { repo.updateTarget(item, target) }
    fun setStoreEnabled(id: String, enabled: Boolean) = viewModelScope.launch { container.prefs.setStoreEnabled(id, enabled) }
    fun setNotifications(enabled: Boolean) = viewModelScope.launch { container.prefs.setNotifications(enabled) }
    fun removeCustomStore(id: String) = viewModelScope.launch { container.prefs.removeCustomStore(id) }

    fun addCustomStore(name: String, url: String, platform: StorePlatform, clubId: String?) = viewModelScope.launch {
        val clean = url.trim().trimEnd('/').let { if (it.startsWith("http")) it else "https://$it" }
        val id = "custom-" + clean.normalized().replace(Regex("[^a-z0-9]+"), "-").trim('-')
        container.prefs.addCustomStore(CustomStore(id, name.trim(), clean, platform, clubId))
    }
}

data class OffersUiState(
    val loading: Boolean = false,
    val result: SearchResult? = null,
    val category: Category? = null,
    val sort: SortOrder = SortOrder.BEST_DISCOUNT,
) {
    val offers: List<Offer>
        get() = sortList(result?.offers.orEmpty())

    private fun sortList(list: List<Offer>) = when (sort) {
        SortOrder.BEST_DISCOUNT -> list.sortedWith(compareByDescending<Offer> { it.discountPercent }.thenBy { it.price })
        SortOrder.LOWEST_PRICE -> list.sortedBy { it.price }
        SortOrder.HIGHEST_PRICE -> list.sortedByDescending { it.price }
    }
}

/** Lista de ofertas para uma categoria, um clube, uma busca livre ou combinações. */
class OffersViewModel(
    private val container: AppContainer,
    private val club: Club?,
    initialCategory: Category?,
    private val initialText: String = "",
) : ViewModel() {
    private val _state = MutableStateFlow(OffersUiState(category = initialCategory))
    val state: StateFlow<OffersUiState> = _state.asStateFlow()
    private var text = initialText
    private var job: Job? = null

    init {
        if (club != null || initialCategory != null || initialText.isNotBlank()) load()
    }

    fun search(newText: String) {
        text = newText
        load()
    }

    fun selectCategory(category: Category?) {
        _state.update { it.copy(category = category) }
        load()
    }

    fun setSort(sort: SortOrder) = _state.update { it.copy(sort = sort) }

    fun load(force: Boolean = false) {
        job?.cancel()
        job = viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            val result = container.repository.search(OfferQuery(text, _state.value.category, club), force)
            _state.update { it.copy(loading = false, result = result) }
        }
    }
}

/** Filtro da lista de clubes. */
class ClubsViewModel(app: AppViewModel) : ViewModel() {
    val query = MutableStateFlow("")
    val clubs: StateFlow<List<Pair<Club, Boolean>>> = combine(query, app.settings) { q, s ->
        val nq = q.normalized()
        ClubCatalog.clubs
            .filter { c -> nq.isBlank() || (c.aliases + c.name).any { it.normalized().contains(nq) } }
            .map { it to (it.id in s.favoriteClubs) }
            .sortedByDescending { it.second }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
}
