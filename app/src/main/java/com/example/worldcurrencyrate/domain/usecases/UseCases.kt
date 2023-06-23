package com.example.worldcurrencyrate.domain.usecases

data class UseCases(
    val convertCurrency: ConvertCurrency,
    val timeSeriesCurrency: TimeSeriesCurrency,
    val fluctuationCurrency: FluctuationCurrency,
    val latestCurrency: LatestCurrency
)
