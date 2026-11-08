package com.trackmyboot.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.trackmyboot.app.data.db.converter.DateConverter
import com.trackmyboot.app.data.db.converter.ProductCategoryConverter
import com.trackmyboot.app.data.db.converter.StoreConverter
import com.trackmyboot.app.data.db.dao.AlertDao
import com.trackmyboot.app.data.db.dao.AppSettingsDao
import com.trackmyboot.app.data.db.dao.PriceHistoryDao
import com.trackmyboot.app.data.db.dao.ProductDao
import com.trackmyboot.app.data.db.dao.WatchlistDao
import com.trackmyboot.app.domain.model.Alert
import com.trackmyboot.app.domain.model.AppSettings
import com.trackmyboot.app.domain.model.PriceHistory
import com.trackmyboot.app.domain.model.Product
import com.trackmyboot.app.domain.model.WatchlistItem

@Database(
    entities = [
        Product::class,
        WatchlistItem::class,
        PriceHistory::class,
        Alert::class,
        AppSettings::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class, ProductCategoryConverter::class, StoreConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun watchlistDao(): WatchlistDao
    abstract fun priceHistoryDao(): PriceHistoryDao
    abstract fun alertDao(): AlertDao
    abstract fun appSettingsDao(): AppSettingsDao

    companion object {
        const val DATABASE_NAME = "trackmyboot.db"
    }
}