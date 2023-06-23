package com.example.worldcurrencyrate.di

import com.example.worldcurrencyrate.data.remote.CurrencyApi
import com.example.worldcurrencyrate.data.repository.CurrencyRepositoryImpl
import com.example.worldcurrencyrate.domain.repository.CurrencyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    @Provides
    @Singleton
    fun provideCurrencyRepository(
        currencyApi: CurrencyApi,
        ): CurrencyRepository {
        return CurrencyRepositoryImpl(
            currencyApi = currencyApi
        )
    }

}