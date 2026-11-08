package com.trackmyboot.app.data.repository

import com.trackmyboot.app.data.db.dao.ProductDao
import com.trackmyboot.app.data.db.dao.PriceHistoryDao
import com.trackmyboot.app.domain.model.Product
import com.trackmyboot.app.domain.model.PriceHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val productDao: ProductDao,
    private val priceHistoryDao: PriceHistoryDao
) {
    fun getAllProducts(): Flow<List<Product>> = productDao.getAllProducts()

    fun getProductById(id: String): Flow<Product?> = productDao.getProductById(id)

    fun getProductsByCategory(category: String): Flow<List<Product>> = productDao.getProductsByCategory(category)

    fun getProductsByStore(store: String): Flow<List<Product>> = productDao.getProductsByStore(store)

    fun getProductsByCategoryAndStore(category: String, store: String): Flow<List<Product>> =
        productDao.getProductsByCategoryAndStore(category, store)

    fun searchProducts(query: String): Flow<List<Product>> = productDao.searchProducts("%$query%")

    fun getProductsOnSale(): Flow<List<Product>> = productDao.getProductsOnSale()

    fun getAvailableProducts(): Flow<List<Product>> = productDao.getAvailableProducts()

    fun getProductsByIds(productIds: List<String>): Flow<List<Product>> = productDao.getProductsByIds(productIds)

    suspend fun insertProducts(products: List<Product>) {
        productDao.insertAll(products)
        // Also save price history
        val historyList = products.map { product ->
            PriceHistory(
                productId = product.id,
                price = product.currentPrice,
                originalPrice = product.originalPrice,
                discountPercentage = product.discountPercentage,
                isAvailable = product.isAvailable,
                store = product.store.name
            )
        }
        priceHistoryDao.insertAll(historyList)
    }

    suspend fun updateProduct(product: Product) = withContext(Dispatchers.IO) {
        productDao.update(product)
        // Save price history if price changed
        val existingHistory = priceHistoryDao.getRecentHistoryForProduct(product.id, 1).firstOrNull()
        val lastPrice = existingHistory?.price
        if (lastPrice != product.currentPrice) {
            val history = PriceHistory(
                productId = product.id,
                price = product.currentPrice,
                originalPrice = product.originalPrice,
                discountPercentage = product.discountPercentage,
                isAvailable = product.isAvailable,
                store = product.store.name
            )
            priceHistoryDao.insert(history)
        }
    }

    suspend fun deleteOldProducts(cutoffDate: Long) = productDao.deleteOldProducts(cutoffDate)

    suspend fun getProductCount() = productDao.getProductCount()

    // Price History
    fun getPriceHistory(productId: String): Flow<List<PriceHistory>> = priceHistoryDao.getHistoryForProduct(productId)

    suspend fun getRecentPriceHistory(productId: String, limit: Int): List<PriceHistory> =
        priceHistoryDao.getRecentHistoryForProduct(productId, limit)

    fun getPriceHistorySince(productId: String, startDate: Long): Flow<List<PriceHistory>> =
        priceHistoryDao.getHistoryForProductSince(productId, startDate)

    suspend fun getLowestPrice(productId: String): Double? = priceHistoryDao.getLowestPrice(productId)

    suspend fun getHighestPrice(productId: String): Double? = priceHistoryDao.getHighestPrice(productId)

    suspend fun deleteOldPriceHistory(cutoffDate: Long) = priceHistoryDao.deleteOldHistory(cutoffDate)
}