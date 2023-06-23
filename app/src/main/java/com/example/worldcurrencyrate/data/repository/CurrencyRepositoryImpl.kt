package com.example.worldcurrencyrate.data.repository

import android.util.Log
import com.example.worldcurrencyrate.data.remote.CurrencyApi
import com.example.worldcurrencyrate.domain.model.*
import com.example.worldcurrencyrate.domain.repository.CurrencyRepository
import com.example.worldcurrencyrate.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import java.net.ConnectException
import java.net.UnknownHostException
import java.time.LocalDate
import java.util.concurrent.TimeoutException

class CurrencyRepositoryImpl(
    private val currencyApi: CurrencyApi
) : CurrencyRepository {

    override suspend fun convertCurrency(
        amount: String,
        from: String,
        to: String,
        date: String
    ): Flow<Resource<ConvertModel>> = flow {

        try {
            emit(Resource.Loading(isLoading = true))
            val data = currencyApi.convertCurrency(amount, from, to, date)
            emit(Resource.Success(data))
            emit(Resource.Loading(isLoading = false))
        } catch (e: ConnectException) {
            emit(Resource.Error(error = e))
        } catch (e: UnknownHostException) {
            emit(Resource.Error(error = e))
        } catch (e: Exception) {
            Log.i("TAG", "convertCurrency: $e")
            emit(Resource.Error(error = e))
        }

    }

    override suspend fun timeSeriesCurrency(
        startDate: String,
        endDate: String,
        from: String,
        to: String
    ): Flow<Resource<TimeSeriesModel>> = flow {

        try {
            emit(Resource.Loading(true))
            val data = currencyApi.timeSeriesCurrency(startDate, endDate, from, to)
            emit(Resource.Success(data))
            emit(Resource.Loading(false))
        } catch (e: ConnectException) {
            emit(Resource.Error(error = e))
        } catch (e: UnknownHostException) {
            emit(Resource.Error(error = e))
        } catch (e: Exception) {
            Log.i("TAG", "timeSeriesCurrency: $e")
            emit(Resource.Error(error = e))
        }

    }

    override suspend fun fluctuationCurrency(
        startDate: String,
        endDate: String,
        from: String,
        to: String
    ): Flow<Resource<FluctuationModel>> = flow {

        try {
            emit(Resource.Loading(true))
            val data = currencyApi.fluctuationCurrency(startDate, endDate, from, to)
            emit(Resource.Success(data))
            emit(Resource.Loading(false))
        } catch (e: ConnectException) {
            emit(Resource.Error(error = e))
        } catch (e: UnknownHostException) {
            emit(Resource.Error(error = e))
        } catch (e: Exception) {
            Log.i("TAG", "fluctuationCurrency: $e")
            emit(Resource.Error(error = e))
        }

    }

    override suspend fun latestData(from: String, to: String): Flow<Resource<TrackModel>> = flow {
        try {
            emit(Resource.Loading(true))
            val latest = currencyApi.latestData(from = from, to = to)
            val fluctuation = currencyApi.fluctuationCurrency(
                startDate = LocalDate.now().minusDays(1).toString(),
                endDate = LocalDate.now().toString(),
                from = from,
                to = to
            )
            Log.i("TAG", "latestData: ${fluctuation.rates}")
            val fluctuationRate = fluctuation.rates.mapValues {
                it.value.change to it.value.changePct
            }
            val wrappedData = TrackModel(
                base = latest.base,
                timeStamp = latest.timestamp,
                latestRate = latest.rates,
                fluctuationRate = fluctuationRate
            )
            emit(Resource.Success(wrappedData))
            emit(Resource.Loading(false))
        } catch (e: ConnectException) {
            emit(Resource.Error(error = e))
        } catch (e: UnknownHostException) {
            emit(Resource.Error(error = e))
        } catch (e: Exception) {
            Log.i("TAG", "fluctuationCurrency: $e")
            emit(Resource.Error(error = e))
        }
    }

}