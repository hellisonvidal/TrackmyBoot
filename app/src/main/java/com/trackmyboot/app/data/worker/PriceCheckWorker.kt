package com.trackmyboot.app.data.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.trackmyboot.app.R
import com.trackmyboot.app.data.repository.AlertRepository
import com.trackmyboot.app.data.repository.ProductRepository
import com.trackmyboot.app.data.repository.SettingsRepository
import com.trackmyboot.app.data.repository.WatchlistRepository
import com.trackmyboot.app.domain.model.Alert
import com.trackmyboot.app.domain.model.AlertType
import com.trackmyboot.app.domain.model.Product
import com.trackmyboot.app.presentation.MainActivity
import dagger.hilt.android.scopes.WorkerScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltWorker
class PriceCheckWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val productRepository: ProductRepository,
    private val watchlistRepository: WatchlistRepository,
    private val alertRepository: AlertRepository,
    private val settingsRepository: SettingsRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Check if notifications are enabled
            val notificationsEnabled = settingsRepository.notificationsEnabled.first()
            if (!notificationsEnabled) {
                return@withContext Result.success()
            }

            // Get watchlist items that need price drop notifications
            val itemsForPriceDrop = watchlistRepository.getItemsForPriceDropNotifications().first()
            val itemsWithTargetPrice = watchlistRepository.getItemsWithTargetPrice().first()

            var alertsCreated = 0

            // Check price drops for all watched items
            for (watchlistItem in itemsForPriceDrop) {
                val product = productRepository.getProductByIdSync(watchlistItem.productId)
                if (product != null) {
                    // Get previous price from history
                    val history = productRepository.getRecentPriceHistory(watchlistItem.productId, 2)
                    if (history.size >= 2) {
                        val previousPrice = history[1].price
                        val currentPrice = product.currentPrice

                        if (currentPrice < previousPrice) {
                            // Price dropped!
                            val alert = Alert(
                                productId = product.id,
                                productName = product.name,
                                productImageUrl = product.imageUrl,
                                store = product.store.name,
                                previousPrice = previousPrice,
                                currentPrice = currentPrice,
                                alertType = AlertType.PRICE_DROP,
                                message = "${product.name} caiu de R$ ${String.format("%.2f", previousPrice).replace(".", ",")} para R$ ${product.formattedCurrentPrice}"
                            )
                            alertRepository.createAlert(alert)
                            alertsCreated++
                        }
                    }

                    // Check if target price reached
                    if (watchlistItem.targetPrice != null && product.currentPrice <= watchlistItem.targetPrice!!) {
                        val alert = Alert(
                            productId = product.id,
                            productName = product.name,
                            productImageUrl = product.imageUrl,
                            store = product.store.name,
                            previousPrice = watchlistItem.targetPrice!!,
                            currentPrice = product.currentPrice,
                            targetPrice = watchlistItem.targetPrice,
                            alertType = AlertType.TARGET_PRICE_REACHED,
                            message = "${product.name} atingiu seu preço alvo de R$ ${watchlistItem.formattedTargetPrice}!"
                        )
                        alertRepository.createAlert(alert)
                        alertsCreated++
                    }

                    // Check if back in stock
                    if (watchlistItem.notifyOnBackInStock && !watchlistItem.isActive && product.isAvailable) {
                        val alert = Alert(
                            productId = product.id,
                            productName = product.name,
                            productImageUrl = product.imageUrl,
                            store = product.store.name,
                            previousPrice = product.currentPrice,
                            currentPrice = product.currentPrice,
                            alertType = AlertType.BACK_IN_STOCK,
                            message = "${product.name} voltou ao estoque na ${product.store.displayName}!"
                        )
                        alertRepository.createAlert(alert)
                        alertsCreated++
                    }
                }
            }

            // Show summary notification if any alerts were created
            if (alertsCreated > 0) {
                showSummaryNotification(alertsCreated)
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun showSummaryNotification(count: Int) {
        val channelId = "price_alerts"
        createNotificationChannel(channelId)

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "alerts")
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("TrackmyBoot - Alertas de Preço")
            .setContentText("$count novo(s) alerta(s) de preço!")
            .setStyle(NotificationCompat.BigTextStyle().bigText("$count produto(s) na sua lista de desejos tiveram queda de preço ou atingiram seu preço alvo."))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(
            System.currentTimeMillis().toInt(), notification
        )
    }

    private fun createNotificationChannel(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alertas de Preço",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificações quando produtos na sua lista de desejos têm queda de preço"
            }
            val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}