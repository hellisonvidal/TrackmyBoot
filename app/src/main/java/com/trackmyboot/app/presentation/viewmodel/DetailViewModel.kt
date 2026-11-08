package com.trackmyboot.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trackmyboot.app.data.repository.PriceHistoryDao
import com.trackmyboot.app.data.repository.ProductRepository
import com.trackmyboot.app.data.repository.WatchlistRepository
import com.trackmyboot.app.domain.model.PriceHistory
import com.trackmyboot.app.domain.model.Product
import com.trackmyboot.app.domain.model.WatchlistItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val watchlistRepository: WatchlistRepository,
    private val priceHistoryDao: PriceHistoryDao
) : ViewModel() {

    private val _productId = MutableStateFlow<String?>(null)
    val productId: Flow<String?> = _productId.distinctUntilChanged()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: Flow<Boolean> = _isLoading.distinctUntilChanged()

    private val _error = MutableStateFlow<String?>(null)
    val error: Flow<String?> = _error.distinctUntilChanged()

    // Product with watchlist status
    val productWithWatchlist: Flow<ProductWithWatchlist?> = combine(
        productId.flatMapLatest { id ->
            if (id != null) productRepository.getProductById(id) else flowOf(null)
        },
        watchlistRepository.getActiveWatchlistItems()
    ) { product, watchlistItems ->
        product?.let { p ->
            val watchlistItem = watchlistItems.find { it.productId == p.id }
            ProductWithWatchlist(p, watchlistItem)
        }
    }.distinctUntilChanged()

    // Price history for charts
    val priceHistory: Flow<List<PriceHistory>> = productId.flatMapLatest { id ->
        if (id != null) priceHistoryDao.getHistoryForProduct(id) else flowOf(emptyList())
    }.distinctUntilChanged()

    // Price statistics
    val priceStats: Flow<PriceStats?> = productId.flatMapLatest { id ->
        if (id != null) {
            flow {
                val lowest = priceHistoryDao.getLowestPrice(id)
                val highest = priceHistoryDao.getHighestPrice(id)
                val recent = priceHistoryDao.getRecentHistoryForProduct(id, 30)
                val current = recent.firstOrNull()?.price
                val avg = if (recent.isNotEmpty()) recent.averageOf { it.price } else 0.0

                emit(PriceStats(
                    lowestPrice = lowest,
                    highestPrice = highest,
                    currentPrice = current,
                    averagePrice = avg,
                    priceCount = recent.size
                ))
            }
        } else flowOf(null)
    }.distinctUntilChanged()

    fun setProductId(id: String?) {
        _productId.value = id
    }

    fun addToWatchlist(product: Product, targetPrice: Double? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            val item = WatchlistItem(
                productId = product.id,
                targetPrice = targetPrice,
                notifyOnPriceDrop = true,
                notifyOnBackInStock = true
            )
            watchlistRepository.addToWatchlist(item)
            _isLoading.value = false
        }
    }

    fun removeFromWatchlist(productId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            watchlistRepository.removeFromWatchlist(productId)
            _isLoading.value = false
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

    fun togglePriceDropNotification(productId: String, enabled: Boolean) {
        viewModelScope.launch {
            watchlistRepository.getWatchlistItem(productId).firstOrNull()?.let { item ->
                val updatedItem = item.copy(notifyOnPriceDrop = enabled)
                watchlistRepository.updateWatchlistItem(updatedItem)
            }
        }
    }
}

data class ProductWithWatchlist(
    val product: Product,
    val watchlistItem: WatchlistItem?
) {
    val isInWatchlist: Boolean
        get() = watchlistItem != null

    val targetPrice: Double?
        get() = watchlistItem?.targetPrice

    val notifyOnPriceDrop: Boolean
        get() = watchlistItem?.notifyOnPriceDrop ?: false

    val notifyOnBackInStock: Boolean
        get() = watchlistItem?.notifyOnBackInStock ?: false
}

data class PriceStats(
    val lowestPrice: Double?,
    val highestPrice: Double?,
    val currentPrice: Double?,
    val averagePrice: Double,
    val priceCount: Int
) {
    val hasData: Boolean
        get() = priceCount > 0

    val savingsFromHighest: Double?
        get() = highestPrice?.let { it - (currentPrice ?: 0.0) }

    val savingsPercentage: Int?
        get() = highestPrice?.let { ((savingsFromHighest!! / it) * 100).toInt() }
}