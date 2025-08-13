package com.example.qrmealtrack.di

import android.app.Application
import androidx.room.Room
import com.example.qrmealtrack.data.local.ReceiptDao
import com.example.qrmealtrack.data.local.ReceiptDatabase
import com.example.qrmealtrack.data.repository.ReceiptRepositoryImpl
import com.example.qrmealtrack.data.repository.WebPageRepositoryImpl
import com.example.qrmealtrack.domain.repository.ReceiptRepository
import com.example.qrmealtrack.domain.repository.WebPageRepository
import com.example.qrmealtrack.domain.usecase.GetAllReceiptsUseCase
import com.example.qrmealtrack.domain.usecase.GetPriceDynamicsUseCase
import com.example.qrmealtrack.presentation.utils.DateFormatter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(app: Application): ReceiptDatabase =
        Room.databaseBuilder(app, ReceiptDatabase::class.java, "receipts_db")
            .fallbackToDestructiveMigration(false)
            .build()

    @Provides
    fun provideDao(db: ReceiptDatabase): ReceiptDao = db.receiptDao()

    @Provides
    @Singleton
    fun provideReceiptRepository(dao: ReceiptDao): ReceiptRepository =
        ReceiptRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideWebPageRepository(
        client: OkHttpClient
    ): WebPageRepository =
        WebPageRepositoryImpl(client)

    @Provides
    @Singleton
    fun provideGetAllReceiptsUseCase(
        repository: ReceiptRepository
    ): GetAllReceiptsUseCase {
        return GetAllReceiptsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetPriceDynamicsUseCase(
        repository: ReceiptRepository
    ): GetPriceDynamicsUseCase {
        return GetPriceDynamicsUseCase(repository)
    }

    @Module
    @InstallIn(SingletonComponent::class)
    object FormatterModule {

        @Provides
        @Singleton
        fun provideDateFormatter(): DateFormatter = DateFormatter()
    }
}