package com.trackmyboot.app.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/** Produto que o usuário está monitorando. */
@Entity(tableName = "watched_offers")
data class WatchedOffer(
    @PrimaryKey val id: String,
    val storeId: String,
    val storeName: String,
    val productRef: String,
    val title: String,
    val brand: String?,
    val imageUrl: String?,
    val url: String,
    val isDemo: Boolean,
    val initialPrice: Double,
    val currentPrice: Double,
    val lowestPrice: Double,
    val listPrice: Double?,
    /** Avisar quando o preço ficar igual ou abaixo deste valor (null = qualquer queda). */
    val targetPrice: Double?,
    val available: Boolean,
    val addedAt: Long,
    val lastCheckedAt: Long,
)

@Entity(tableName = "price_points", indices = [Index("offerId")])
data class PricePoint(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val offerId: String,
    val price: Double,
    val timestamp: Long,
)

@Dao
interface WatchDao {
    @Query("SELECT * FROM watched_offers ORDER BY addedAt DESC")
    fun observeAll(): Flow<List<WatchedOffer>>

    @Query("SELECT * FROM watched_offers")
    suspend fun getAll(): List<WatchedOffer>

    @Query("SELECT id FROM watched_offers")
    fun observeIds(): Flow<List<String>>

    @Upsert
    suspend fun upsert(offer: WatchedOffer)

    @Query("DELETE FROM watched_offers WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM price_points WHERE offerId = :id")
    suspend fun deleteHistory(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoint(point: PricePoint)

    @Query("SELECT * FROM price_points ORDER BY timestamp ASC")
    fun observeAllPoints(): Flow<List<PricePoint>>
}

@Database(entities = [WatchedOffer::class, PricePoint::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun watchDao(): WatchDao

    companion object {
        fun create(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "trackmyboot.db").build()
    }
}
