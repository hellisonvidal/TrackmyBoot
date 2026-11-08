package com.trackmyboot.app.data.repository

import com.trackmyboot.app.data.db.dao.WatchlistDao
import com.trackmyboot.app.domain.model.WatchlistItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WatchlistRepository @Inject constructor(
    private val watchlistDao: WatchlistDao
) {
    fun getActiveWatchlistItems(): Flow<List<WatchlistItem>> = watchlistDao.getActiveWatchlistItems()

    fun getAllWatchlistItems(): Flow<List<WatchlistItem>> = watchlistDao.getAllWatchlistItems()

    fun getWatchlistItem(productId: String): Flow<WatchlistItem?> = watchlistDao.getByProductId(productId)

    fun getItemsForPriceDropNotifications(): Flow<List<WatchlistItem>> =
        watchlistDao.getItemsForPriceDropNotifications()

    fun getItemsWithTargetPrice(): Flow<List<WatchlistItem>> = watchlistDao.getItemsWithTargetPrice()

    suspend fun addToWatchlist(item: WatchlistItem): Long = watchlistDao.insert(item)

    suspend fun updateWatchlistItem(item: WatchlistItem): Int = watchlistDao.update(item)

    suspend fun removeFromWatchlist(productId: String): Int = watchlistDao.deleteByProductId(productId)

    suspend fun removeWatchlistItem(item: WatchlistItem): Int = watchlistDao.delete(item)

    suspend fun getActiveCount(): Int = watchlistDao.getActiveCount()

    suspend fun isInWatchlist(productId: String): Boolean =
        watchlistDao.getByProductId(productId).first() != null
}