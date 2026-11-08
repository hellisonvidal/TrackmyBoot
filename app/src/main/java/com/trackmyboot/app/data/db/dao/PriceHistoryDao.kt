package com.trackmyboot.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.trackmyboot.app.domain.model.PriceHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface PriceHistoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(history: PriceHistory): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(histories: List<PriceHistory>)

    @Query("SELECT * FROM price_history WHERE productId = :productId ORDER BY recordedAt DESC")
    fun getHistoryForProduct(productId: String): Flow<List<PriceHistory>>

    @Query("SELECT * FROM price_history WHERE productId = :productId ORDER BY recordedAt DESC LIMIT :limit")
    suspend fun getRecentHistoryForProduct(productId: String, limit: Int): List<PriceHistory>

    @Query("SELECT * FROM price_history WHERE productId = :productId AND recordedAt >= :startDate ORDER BY recordedAt ASC")
    fun getHistoryForProductSince(productId: String, startDate: Long): Flow<List<PriceHistory>>

    @Query("SELECT MIN(price) FROM price_history WHERE productId = :productId")
    suspend fun getLowestPrice(productId: String): Double?

    @Query("SELECT MAX(price) FROM price_history WHERE productId = :productId")
    suspend fun getHighestPrice(productId: String): Double?

    @Query("DELETE FROM price_history WHERE recordedAt < :cutoffDate")
    suspend fun deleteOldHistory(cutoffDate: Long): Int

    @Query("SELECT COUNT(*) FROM price_history WHERE productId = :productId")
    suspend fun getHistoryCount(productId: String): Int
}