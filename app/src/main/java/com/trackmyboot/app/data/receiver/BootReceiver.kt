package com.trackmyboot.app.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.trackmyboot.app.data.worker.PriceCheckWorker
import java.util.concurrent.TimeUnit

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            schedulePriceCheckWork(context)
        }
    }

    private fun schedulePriceCheckWork(context: Context) {
        val workRequest = PeriodicWorkRequest.Builder(PriceCheckWorker::class.java, 6, TimeUnit.HOURS)
            .setInitialDelay(15, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "price_check_work",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    companion object {
        fun schedulePriceCheckWork(context: Context, intervalHours: Int = 6) {
            val workRequest = PeriodicWorkRequest.Builder(PriceCheckWorker::class.java, intervalHours.toLong(), TimeUnit.HOURS)
                .setInitialDelay(15, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "price_check_work",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }
    }
}