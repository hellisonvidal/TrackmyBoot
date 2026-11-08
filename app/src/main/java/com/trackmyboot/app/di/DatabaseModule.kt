package com.trackmyboot.app.di

import android.content.Context
import androidx.room.Room
import com.trackmyboot.app.data.db.AppDatabase
import com.trackmyboot.app.data.db.dao.AlertDao
import com.trackmyboot.app.data.db.dao.AppSettingsDao
import com.trackmyboot.app.data.db.dao.PriceHistoryDao
import com.trackmyboot.app.data.db.dao.ProductDao
import com.trackmyboot.app.data.db.dao.WatchlistDao
import com.trackmyboot.app.data.remote.ProductApiService
import com.trackmyboot.app.data.repository.AlertRepository
import com.trackmyboot.app.data.repository.ProductRepository
import com.trackmyboot.app.data.repository.RemoteRepository
import com.trackmyboot.app.data.repository.SettingsRepository
import com.trackmyboot.app.data.repository.WatchlistRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideProductDao(database: AppDatabase): ProductDao = database.productDao()

    @Provides
    fun provideWatchlistDao(database: AppDatabase): WatchlistDao = database.watchlistDao()

    @Provides
    fun providePriceHistoryDao(database: AppDatabase): PriceHistoryDao = database.priceHistoryDao()

    @Provides
    fun provideAlertDao(database: AppDatabase): AlertDao = database.alertDao()

    @Provides
    fun provideAppSettingsDao(database: AppDatabase): AppSettingsDao = database.appSettingsDao()

    @Provides
    @Singleton
    fun provideApiService(): ProductApiService = ProductApiService.createMockService()

    @Provides
    @Singleton
    fun provideProductRepository(
        productDao: ProductDao,
        priceHistoryDao: PriceHistoryDao
    ): ProductRepository = ProductRepository(productDao, priceHistoryDao)

    @Provides
    @Singleton
    fun provideWatchlistRepository(watchlistDao: WatchlistDao): WatchlistRepository =
        WatchlistRepository(watchlistDao)

    @Provides
    @Singleton
    fun provideAlertRepository(alertDao: AlertDao): AlertRepository = AlertRepository(alertDao)

    @Provides
    @Singleton
    fun provideSettingsRepository(appSettingsDao: AppSettingsDao): SettingsRepository =
        SettingsRepository(appSettingsDao)

    @Provides
    @Singleton
    fun provideRemoteRepository(
        apiService: ProductApiService,
        productRepository: ProductRepository,
        settingsRepository: SettingsRepository
    ): RemoteRepository = RemoteRepository(apiService, productRepository, settingsRepository)
}