package com.trackmyboot.app.data.remote

import com.trackmyboot.app.domain.model.Product
import com.trackmyboot.app.domain.model.Store
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApiService {
    @GET("search")
    suspend fun searchProducts(
        @Query("q") query: String,
        @Query("store") store: String?,
        @Query("category") category: String?
    ): ApiResponse<List<Product>>

    @GET("products/{id}")
    suspend fun getProductDetails(@Path("id") id: String): ApiResponse<Product>

    @GET("stores/{store}/products")
    suspend fun getStoreProducts(
        @Path("store") store: String,
        @Query("category") category: String?
    ): ApiResponse<List<Product>>

    @GET("categories/{category}/products")
    suspend fun getCategoryProducts(
        @Path("category") category: String,
        @Query("store") store: String?
    ): ApiResponse<List<Product>>

    companion object {
        // Mock data generator for development/testing
        fun createMockService(): ProductApiService = object : ProductApiService {
            override suspend fun searchProducts(query: String, store: String?, category: String?): ApiResponse<List<Product>> {
                return ApiResponse.Success(generateMockProducts(query, store, category))
            }

            override suspend fun getProductDetails(id: String): ApiResponse<Product> {
                return ApiResponse.Success(generateMockProducts("", null, null).first { it.id == id })
            }

            override suspend fun getStoreProducts(store: String, category: String?): ApiResponse<List<Product>> {
                return ApiResponse.Success(generateMockProducts("", store, category))
            }

            override suspend fun getCategoryProducts(category: String, store: String?): ApiResponse<List<Product>> {
                return ApiResponse.Success(generateMockProducts("", store, category))
            }

            private fun generateMockProducts(query: String?, store: String?, category: String?): List<Product> {
                val stores = if (store != null) listOf(Store.valueOf(store.uppercase())) else Store.values().toList()
                val categories = if (category != null) listOf(com.trackmyboot.app.domain.model.ProductCategory.valueOf(category.uppercase())) else com.trackmyboot.app.domain.model.ProductCategory.values().toList()

                return stores.flatMap { s ->
                    categories.flatMap { c ->
                        generateProductsForStoreAndCategory(s, c, query).take(5)
                    }
                }.shuffled().take(20)
            }

            private fun generateProductsForStoreAndCategory(
                store: Store,
                category: com.trackmyboot.app.domain.model.ProductCategory,
                query: String?
            ): List<Product> {
                val brands = when (category) {
                    com.trackmyboot.app.domain.model.ProductCategory.JERSEYS -> listOf("Nike", "Adidas", "Puma", "Umbro", "New Balance")
                    com.trackmyboot.app.domain.model.ProductCategory.BALLS -> listOf("Nike", "Adidas", "Puma", "Select", "Mikasa")
                    com.trackmyboot.app.domain.model.ProductCategory.CLEATS -> listOf("Nike", "Adidas", "Puma", "Mizuno", "New Balance")
                    else -> listOf("Nike", "Adidas", "Puma", "Umbro", "Mizuno")
                }

                val baseNames = when (category) {
                    com.trackmyboot.app.domain.model.ProductCategory.JERSEYS -> listOf("Camisa Brasil", "Camisa Argentina", "Camisa Flamengo", "Camisa Palmeiras", "Camisa Corinthians")
                    com.trackmyboot.app.domain.model.ProductCategory.BALLS -> listOf("Bola Oficial Copa", "Bola Treino", "Bola Society", "Bola Futsal", "Bola Campo")
                    com.trackmyboot.app.domain.model.ProductCategory.CLEATS -> listOf("Chuteira Mercurial", "Chuteira Predator", "Chuteira Future", "Chuteira Tiempo", "Chuteira Phantom")
                    else -> listOf("Meião", "Caneleira", "Luvas Goleiro", "Bolsa Esportiva", "Garrafa")
                }

                return brands.flatMap { brand ->
                    baseNames.map { name ->
                        val originalPrice = (100.0 + Math.random() * 400).toDouble()
                        val discount = (Math.random() * 50).toInt()
                        val currentPrice = originalPrice * (100 - discount) / 100
                        val id = "${store.name.toLowerCase()}_${category.name.toLowerCase()}_${brand.toLowerCase()}_${name.replace(" ", "_").lowercase()}".replace("/", "_")

                        Product(
                            id = id,
                            name = name,
                            brand = brand,
                            category = category,
                            description = "$name da $brand - Produto oficial licenciado",
                            imageUrl = "https://picsum.photos/seed/$id/400/400.jpg",
                            productUrl = "${store.baseUrl}/produto/$id",
                            store = store,
                            currentPrice = currentPrice,
                            originalPrice = originalPrice,
                            discountPercentage = discount,
                            sizes = if (category == com.trackmyboot.app.domain.model.ProductCategory.JERSEYS || category == com.trackmyboot.app.domain.model.ProductCategory.CLEATS)
                                listOf("P", "M", "G", "GG") else emptyList(),
                            colors = listOf("Preto", "Branco", "Vermelho", "Azul"),
                            tags = listOf("futebol", category.displayName.toLowerCase(), brand.toLowerCase()),
                            rating = 3.5 + Math.random() * 1.5,
                            reviewCount = (Math.random() * 500).toInt()
                        )
                    }
                }
            }
        }
    }
}

sealed class ApiResponse<out T> {
    data class Success<out T>(val data: T) : ApiResponse<T>()
    data class Error(val message: String, val code: Int?) : ApiResponse<Nothing>()
    object Loading : ApiResponse<Nothing>()
}