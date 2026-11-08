package com.trackmyboot.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trackmyboot.app.data.repository.ProductRepository
import com.trackmyboot.app.data.repository.RemoteRepository
import com.trackmyboot.app.data.repository.SettingsRepository
import com.trackmyboot.app.domain.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val remoteRepository: RemoteRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: Flow<String> = _query.distinctUntilChanged()

    private val _selectedCategory = MutableStateFlow<com.trackmyboot.app.domain.model.ProductCategory?>(null)
    val selectedCategory: Flow<com.trackmyboot.app.domain.model.ProductCategory?> = _selectedCategory.distinctUntilChanged()

    private val _sortBy = MutableStateFlow(SortOption.RELEVANCE)
    val sortBy: Flow<SortOption> = _sortBy.distinctUntilChanged()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: Flow<Boolean> = _isLoading.distinctUntilChanged()

    private val _error = MutableStateFlow<String?>(null)
    val error: Flow<String?> = _error.distinctUntilChanged()

    private val _searchResults = MutableStateFlow<List<Product>>(emptyList())
    val searchResults: Flow<List<Product>> = _searchResults.distinctUntilChanged()

    val categories = com.trackmyboot.app.domain.model.ProductCategory.values().toList()

    val sortedAndFilteredResults: Flow<List<Product>> = combine(
        searchResults,
        selectedCategory,
        settingsRepository.enabledCategories.map { it.split(",").filter { it.isNotBlank() } },
        settingsRepository.enabledStores.map { it.split(",").filter { it.isNotBlank() } },
        sortBy
    ) { results, category, enabledCategories, enabledStores, sort ->
        var filtered = results.filter { product ->
            val categoryMatch = category == null || product.category == category
            val categoryEnabled = enabledCategories.isEmpty() || enabledCategories.contains(product.category.name)
            val storeEnabled = enabledStores.isEmpty() || enabledStores.contains(product.store.name)
            categoryMatch && categoryEnabled && storeEnabled
        }

        when (sort) {
            SortOption.PRICE_LOW -> filtered = filtered.sortedBy { it.currentPrice }
            SortOption.PRICE_HIGH -> filtered = filtered.sortedByDescending { it.currentPrice }
            SortOption.DISCOUNT -> filtered = filtered.sortedByDescending { it.discountPercentage }
            SortOption.NEWEST -> filtered = filtered.sortedByDescending { it.lastUpdated.time }
            SortOption.RELEVANCE -> { /* Already sorted by relevance from API */ }
        }
        filtered
    }

    fun setQuery(query: String) {
        _query.value = query
        if (query.isNotBlank()) {
            performSearch(query)
        } else {
            _searchResults.value = emptyList()
        }
    }

    fun setCategory(category: com.trackmyboot.app.domain.model.ProductCategory?) {
        _selectedCategory.value = category
    }

    fun setSortBy(sort: SortOption) {
        _sortBy.value = sort
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            // First search local database
            val localResults = productRepository.searchProducts(query).first()

            // Then search remote API
            val remoteResults = remoteRepository.searchProducts(query).first()

            // Merge results, avoiding duplicates
            val mergedResults = (localResults + remoteResults)
                .distinctBy { it.id }
                .sortedByDescending { it.discountPercentage }
                .thenBy { it.currentPrice }

            _searchResults.value = mergedResults
            _isLoading.value = false
        }
    }

    fun clearSearch() {
        _query.value = ""
        _searchResults.value = emptyList()
        _selectedCategory.value = null
        _sortBy.value = SortOption.RELEVANCE
    }
}

enum class SortOption {
    RELEVANCE,
    PRICE_LOW,
    PRICE_HIGH,
    DISCOUNT,
    NEWEST
}