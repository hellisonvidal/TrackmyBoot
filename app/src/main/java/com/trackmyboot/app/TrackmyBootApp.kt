package com.trackmyboot.app

import android.app.Application
import android.content.Context
import com.trackmyboot.app.data.OfferRepository
import com.trackmyboot.app.data.local.AppDatabase
import com.trackmyboot.app.data.prefs.UserPrefs
import com.trackmyboot.app.data.source.Http
import com.trackmyboot.app.data.worker.PriceCheckWorker

/** Injeção de dependências manual: simples e sem geração de código. */
class AppContainer(context: Context) {
    val prefs = UserPrefs(context)
    val database = AppDatabase.create(context)
    val repository = OfferRepository(Http(), prefs, database.watchDao())
}

class TrackmyBootApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        PriceCheckWorker.ensureChannel(this)
        PriceCheckWorker.schedule(this)
    }
}
