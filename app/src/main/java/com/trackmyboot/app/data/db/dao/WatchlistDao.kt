package com.trackmyboot.app.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.trackmyboot.app.domain.model.WatchlistItem
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: WatchlistItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<WatchlistItem>)

    @Update
    suspend fun update(item: WatchlistItem): Int

    @Delete
    suspend fun delete(item: WatchlistItem): Int

    @Query("DELETE FROM watchlist_items WHERE productId = :productId")
    suspend fun deleteByProductId(productId: String): Int

    @Query("SELECT * FROM watchlist_items WHERE id = :id")
    fun getById(id: Long): Flow<WatchlistItem?>

    @Query("SELECT * FROM watchlist_items WHERE productId = :productId")
    fun getByProductId(productId: String): Flow<WatchlistItem?>

    @Query("SELECT * FROM watchlist_items WHERE isActive = 1 ORDER BY addedAt DESC")
    fun getActiveWatchlistItems(): Flow<List<WatchlistItem>>

    @Query("SELECT * FROM watchlist_items ORDER BY addedAt DESC")
    fun getAllWatchlistItems(): Flow<List<WatchlistItem>>

    @Query("SELECT * FROM watchlist_items WHERE isActive = 1 AND notifyOnPriceDrop = 1")
    fun getItemsForPriceDropNotifications(): Flow<List<WatchlistItem>>

    @Query("SELECT * FROM watchlist_items WHERE isActive = 1 AND targetPrice IS NOT NULL AND targetPrice > 0")
    fun getItemsWithTargetPrice(): Flow<List<WatchlistItem>>

    @Query("SELECT COUNT(*) FROM watchlist_items WHERE isActive = 1")
    suspend fun getActiveCount(): Int
}