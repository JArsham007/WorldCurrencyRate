package com.example.worldcurrencyrate.domain.repository

import com.example.worldcurrencyrate.domain.model.*
import com.example.worldcurrencyrate.utils.Resource
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Query

interface CurrencyRepository {

    suspend fun convertCurrency(
        amount: String,
        from: String,
        to: String,
        date: String,
    ): Flow<Resource<ConvertModel>>

    suspend fun timeSeriesCurrency(
        startDate: String,
        endDate: String,
        from: String,
        to: String,
    ): Flow<Resource<TimeSeriesModel>>

    suspend fun fluctuationCurrency(
        startDate: String,
        endDate: String,
        from: String,
        to: String,
    ): Flow<Resource<FluctuationModel>>

    suspend fun latestData(
        from: String,
        to: String
    ): Flow<Resource<TrackModel>>

}