package com.trackmyboot.app.data.repository

import com.trackmyboot.app.data.remote.ProductApiService
import com.trackmyboot.app.domain.model.Product
import com.trackmyboot.app.domain.model.Store
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteRepository @Inject constructor(
    private val apiService: ProductApiService,
    private val productRepository: ProductRepository,
    private val settingsRepository: SettingsRepository
) {
    suspend fun syncAllProducts(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val enabledCategories = settingsRepository.enabledCategories.first()
                .split(",")
                .filter { it.isNotBlank() }
            val enabledStores = settingsRepository.enabledStores.first()
                .split(",")
                .filter { it.isNotBlank() }

            val categories = if (enabledCategories.isNotEmpty()) {
                enabledCategories.map { com.trackmyboot.app.domain.model.ProductCategory.valueOf(it.uppercase()) }
            } else {
                com.trackmyboot.app.domain.model.ProductCategory.values().toList()
            }

            val stores = if (enabledStores.isNotEmpty()) {
                enabledStores.map { Store.valueOf(it.uppercase()) }
            } else {
                Store.values().toList()
            }

            var totalSynced = 0

            for (store in stores) {
                for (category in categories) {
                    val response = apiService.getStoreProducts(store.name, category.name)
                    when (response) {
                        is ProductApiService.ApiResponse.Success -> {
                            val products = response.data
                            if (products.isNotEmpty()) {
                                productRepository.insertProducts(products)
                                totalSynced += products.size
                            }
                        }
                        is ProductApiService.ApiResponse.Error -> {
                            // Log error but continue with other stores/categories
                        }
                    }
                }
            }

            settingsRepository.setLastSyncTime(System.currentTimeMillis())
            Result.success(totalSynced)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchProducts(query: String): Flow<List<Product>> = flow {
        val response = apiService.searchProducts(query, null, null)
        emit(response.getOrNull()?.data ?: emptyList())
    }.flattenMerge()

    suspend fun getProductDetails(productId: String): Product? = withContext(Dispatchers.IO) {
        apiService.getProductDetails(productId).getOrNull()?.data
    }

    suspend fun syncCategory(category: com.trackmyboot.app.domain.model.ProductCategory): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val enabledStores = settingsRepository.enabledStores.first()
                .split(",")
                .filter { it.isNotBlank() }

            val stores = if (enabledStores.isNotEmpty()) {
                enabledStores.map { Store.valueOf(it.uppercase()) }
            } else {
                Store.values().toList()
            }

            var totalSynced = 0

            for (store in stores) {
                val response = apiService.getStoreProducts(store.name, category.name)
                when (response) {
                    is ProductApiService.ApiResponse.Success -> {
                        val products = response.data
                        if (products.isNotEmpty()) {
                            productRepository.insertProducts(products)
                            totalSynced += products.size
                        }
                    }
                    is ProductApiService.ApiResponse.Error -> { }
                }
            }

            Result.success(totalSynced)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}