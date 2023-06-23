package com.example.worldcurrencyrate.domain.usecases

import com.example.worldcurrencyrate.domain.model.LatestModel
import com.example.worldcurrencyrate.domain.model.TrackModel
import com.example.worldcurrencyrate.domain.repository.CurrencyRepository
import com.example.worldcurrencyrate.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LatestCurrency @Inject constructor(
    private val repository: CurrencyRepository
) {

    suspend operator fun invoke(
        from: String,
        to: String
    ): Flow<Resource<TrackModel>> {
        return repository.latestData(from, to)
    }

}