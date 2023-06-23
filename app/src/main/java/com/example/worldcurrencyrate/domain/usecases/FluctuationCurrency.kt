package com.example.worldcurrencyrate.domain.usecases

import com.example.worldcurrencyrate.domain.model.FluctuationModel
import com.example.worldcurrencyrate.domain.model.TimeSeriesModel
import com.example.worldcurrencyrate.domain.repository.CurrencyRepository
import com.example.worldcurrencyrate.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FluctuationCurrency @Inject constructor(
    private val repository: CurrencyRepository
) {

    suspend operator fun invoke(
        startDate: String,
        endDate: String,
        from: String,
        to: String,
    ): Flow<Resource<FluctuationModel>> {
        return repository.fluctuationCurrency(startDate, endDate, from, to)
    }

}