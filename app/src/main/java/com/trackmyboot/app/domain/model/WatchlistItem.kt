package com.trackmyboot.app.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.trackmyboot.app.data.db.converter.DateConverter
import kotlinx.serialization.Serializable
import java.util.Date

@Entity(
    tableName = "watchlist_items",
    foreignKeys = [
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["productId"], unique = true)]
)
@Serializable
@TypeConverters(DateConverter::class)
data class WatchlistItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productId: String,
    val targetPrice: Double? = null,
    val notifyOnPriceDrop: Boolean = true,
    val notifyOnBackInStock: Boolean = true,
    val addedAt: Date = Date(),
    val lastNotifiedAt: Date? = null,
    val isActive: Boolean = true
) {
    val hasTargetPrice: Boolean
        get() = targetPrice != null && targetPrice!! > 0

    val formattedTargetPrice: String?
        get() = targetPrice?.let { String.format("%.2f", it).replace(".", ",") }
}