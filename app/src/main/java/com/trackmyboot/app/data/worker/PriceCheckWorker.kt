package com.trackmyboot.app.data.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.trackmyboot.app.R
import com.trackmyboot.app.TrackmyBootApp
import com.trackmyboot.app.util.toBrl
import java.util.concurrent.TimeUnit

/** Reconsulta periodicamente os preços dos produtos monitorados e avisa quando baixam. */
class PriceCheckWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val container = (applicationContext as TrackmyBootApp).container
        val changes = runCatching { container.repository.refreshWatched() }.getOrElse { return Result.retry() }
        if (container.prefs.current().notificationsEnabled) {
            changes.filter { it.dropped || it.hitTarget }.forEach(::notify)
        }
        return Result.success()
    }

    private fun notify(change: com.trackmyboot.app.data.OfferRepository.PriceChange) {
        val ctx = applicationContext
        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED &&
            android.os.Build.VERSION.SDK_INT >= 33
        ) return
        ensureChannel(ctx)
        val title = if (change.hitTarget) "🎯 Preço alvo atingido!" else "📉 Baixou de preço!"
        val text = "${change.item.title}: ${change.oldPrice.toBrl()} → ${change.newPrice.toBrl()} na ${change.item.storeName}"
        val open = PendingIntent.getActivity(
            ctx, change.item.id.hashCode(),
            Intent(Intent.ACTION_VIEW, Uri.parse(change.item.url)),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val notification = NotificationCompat.Builder(ctx, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setContentIntent(open)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(ctx).notify(change.item.id.hashCode(), notification)
    }

    companion object {
        private const val CHANNEL_ID = "price_drops"
        private const val PERIODIC = "price_check_periodic"
        private const val NOW = "price_check_now"

        fun ensureChannel(context: Context) {
            val manager = context.getSystemService(NotificationManager::class.java)
            if (manager.getNotificationChannel(CHANNEL_ID) == null) {
                manager.createNotificationChannel(
                    NotificationChannel(CHANNEL_ID, "Quedas de preço", NotificationManager.IMPORTANCE_DEFAULT).apply {
                        description = "Avisos quando um produto monitorado fica mais barato"
                    },
                )
            }
        }

        private val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<PriceCheckWorker>(6, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(PERIODIC, ExistingPeriodicWorkPolicy.KEEP, request)
        }

        fun runNow(context: Context) {
            val request = OneTimeWorkRequestBuilder<PriceCheckWorker>().setConstraints(constraints).build()
            WorkManager.getInstance(context).enqueueUniqueWork(NOW, ExistingWorkPolicy.REPLACE, request)
        }
    }
}
