package com.example.worldcurrencyrate.domain.usecases

import com.example.worldcurrencyrate.domain.model.ConvertModel
import com.example.worldcurrencyrate.domain.repository.CurrencyRepository
import com.example.worldcurrencyrate.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ConvertCurrency @Inject constructor(
    private val repository: CurrencyRepository
) {

    suspend operator fun invoke(
        amount: String,
        from: String,
        to: String,
        date: String,
    ): Flow<Resource<ConvertModel>> {
        return repository.convertCurrency(amount, from, to, date)
    }

}