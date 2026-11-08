package com.trackmyboot.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.trackmyboot.app.domain.model.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<Product>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product): Long

    @Update
    suspend fun update(product: Product): Int

    @Query("SELECT * FROM products WHERE id = :id")
    fun getProductById(id: String): Flow<Product?>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductByIdSync(id: String): Product?

    @Query("SELECT * FROM products ORDER BY lastUpdated DESC")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE category = :category ORDER BY lastUpdated DESC")
    fun getProductsByCategory(category: String): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE store = :store ORDER BY lastUpdated DESC")
    fun getProductsByStore(store: String): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE category = :category AND store = :store ORDER BY lastUpdated DESC")
    fun getProductsByCategoryAndStore(category: String, store: String): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE name LIKE :query OR brand LIKE :query OR tags LIKE :query ORDER BY discountPercentage DESC, lastUpdated DESC")
    fun searchProducts(query: String): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE discountPercentage > 0 ORDER BY discountPercentage DESC, currentPrice ASC")
    fun getProductsOnSale(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE isAvailable = 1 ORDER BY currentPrice ASC")
    fun getAvailableProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id IN (:productIds)")
    fun getProductsByIds(productIds: List<String>): Flow<List<Product>>

    @Query("DELETE FROM products WHERE lastUpdated < :cutoffDate")
    suspend fun deleteOldProducts(cutoffDate: Long): Int

    @Query("SELECT COUNT(*) FROM products")
    suspend fun getProductCount(): Int
}