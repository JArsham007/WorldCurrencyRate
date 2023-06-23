package com.example.worldcurrencyrate.data.remote

import com.example.worldcurrencyrate.domain.model.ConvertModel
import com.example.worldcurrencyrate.domain.model.FluctuationModel
import com.example.worldcurrencyrate.domain.model.LatestModel
import com.example.worldcurrencyrate.domain.model.TimeSeriesModel
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApi {

    companion object {
        const val API_KEY = "Bf08e9E2MMvvJBNn2njbZY19C1SD95MM"
    }

    @GET("convert")
    suspend fun convertCurrency(
        @Query("amount") amount: String,
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("date") date: String,
        @Query("apikey") apikey: String = API_KEY
    ): ConvertModel

    @GET("timeseries")
    suspend fun timeSeriesCurrency(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("base") from: String,
        @Query("symbols") to: String,
        @Query("apikey") apikey: String = API_KEY
    ): TimeSeriesModel

    @GET("fluctuation")
    suspend fun fluctuationCurrency(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("base") from: String,
        @Query("symbols") to: String,
        @Query("apikey") apikey: String = API_KEY
    ): FluctuationModel

    @GET("latest")
    suspend fun latestData(
        @Query("base") from: String,
        @Query("symbols") to: String,
        @Query("apikey") apikey: String = API_KEY
    ): LatestModel

}