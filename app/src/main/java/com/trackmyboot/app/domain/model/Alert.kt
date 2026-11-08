package com.trackmyboot.app.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.trackmyboot.app.data.db.converter.DateConverter
import kotlinx.serialization.Serializable
import java.util.Date

@Entity(tableName = "alerts")
@Serializable
@TypeConverters(DateConverter::class)
data class Alert(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productId: String,
    val productName: String,
    val productImageUrl: String,
    val store: String,
    val previousPrice: Double,
    val currentPrice: Double,
    val targetPrice: Double? = null,
    val alertType: AlertType,
    val message: String,
    val isRead: Boolean = false,
    val createdAt: Date = Date()
) {
    val priceDrop: Double
        get() = previousPrice - currentPrice

    val priceDropPercentage: Int
        get() = if (previousPrice > 0) ((priceDrop / previousPrice) * 100).toInt() else 0

    val formattedPreviousPrice: String
        get() = String.format("%.2f", previousPrice).replace(".", ",")

    val formattedCurrentPrice: String
        get() = String.format("%.2f", currentPrice).replace(".", ",")
}

@Serializable
enum class AlertType {
    PRICE_DROP("Queda de preço"),
    TARGET_PRICE_REACHED("Preço alvo atingido"),
    BACK_IN_STOCK("Volta ao estoque"),
    NEW_LOWEST_PRICE("Novo menor preço");

    val displayName: String
}