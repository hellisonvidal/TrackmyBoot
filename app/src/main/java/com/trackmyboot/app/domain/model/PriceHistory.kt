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
    tableName = "price_history",
    foreignKeys = [
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("productId"), Index("recordedAt")]
)
@Serializable
@TypeConverters(DateConverter::class)
data class PriceHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productId: String,
    val price: Double,
    val originalPrice: Double,
    val discountPercentage: Int,
    val isAvailable: Boolean,
    val store: String,
    val recordedAt: Date = Date()
) {
    val formattedPrice: String
        get() = String.format("%.2f", price).replace(".", ",")

    val formattedOriginalPrice: String
        get() = String.format("%.2f", originalPrice).replace(".", ",")
}