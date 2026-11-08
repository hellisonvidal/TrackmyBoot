package com.trackmyboot.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trackmyboot.app.data.repository.ProductRepository
import com.trackmyboot.app.data.repository.WatchlistRepository
import com.trackmyboot.app.domain.model.Product
import com.trackmyboot.app.domain.model.WatchlistItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    private val watchlistRepository: WatchlistRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: Flow<Boolean> = _isLoading.distinctUntilChanged()

    private val _error = MutableStateFlow<String?>(null)
    val error: Flow<String?> = _error.distinctUntilChanged()

    // Combined flow of watchlist items with product details
    val watchlistWithProducts: Flow<List<WatchlistItemWithProduct>> =
        combine(
            watchlistRepository.getActiveWatchlistItems(),
            productRepository.getAllProducts()
        ) { watchlistItems, allProducts ->
            val productMap = allProducts.associateBy { it.id }
            watchlistItems.mapNotNull { item ->
                productMap[item.productId]?.let { product ->
                    WatchlistItemWithProduct(item, product)
                }
            }
        }.distinctUntilChanged()

    val watchlistCount: Flow<Int> = watchlistRepository.getActiveWatchlistItems().map { it.size }

    fun addToWatchlist(product: Product, targetPrice: Double? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val item = WatchlistItem(
                productId = product.id,
                targetPrice = targetPrice,
                notifyOnPriceDrop = true,
                notifyOnBackInStock = true
            )

            watchlistRepository.addToWatchlist(item)
                .also { _isLoading.value = false }
        }
    }

    fun removeFromWatchlist(productId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            watchlistRepository.removeFromWatchlist(productId)
                .also { _isLoading.value = false }
        }
    }

    fun updateTargetPrice(productId: String, targetPrice: Double?) {
        viewModelScope.launch {
            watchlistRepository.getWatchlistItem(productId).firstOrNull()?.let { item ->
                val updatedItem = item.copy(targetPrice = targetPrice)
                watchlistRepository.updateWatchlistItem(updatedItem)
            }
        }
    }

    fun updateNotificationSettings(productId: String, notifyOnPriceDrop: Boolean, notifyOnBackInStock: Boolean) {
        viewModelScope.launch {
            watchlistRepository.getWatchlistItem(productId).firstOrNull()?.let { item ->
                val updatedItem = item.copy(
                    notifyOnPriceDrop = notifyOnPriceDrop,
                    notifyOnBackInStock = notifyOnBackInStock
                )
                watchlistRepository.updateWatchlistItem(updatedItem)
            }
        }
    }

    fun isInWatchlist(productId: String): Flow<Boolean> =
        watchlistRepository.getWatchlistItem(productId).map { it != null }
}

data class WatchlistItemWithProduct(
    val watchlistItem: WatchlistItem,
    val product: Product
) {
    val isAtTargetPrice: Boolean
        get() = watchlistItem.targetPrice != null && product.currentPrice <= watchlistItem.targetPrice!!

    val priceDropFromTarget: Double?
        get() = watchlistItem.targetPrice?.let { product.currentPrice - it }

    val savings: Double
        get() = product.originalPrice - product.currentPrice
}