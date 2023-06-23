package com.example.worldcurrencyrate.presentation.screens.track_screen

import com.example.worldcurrencyrate.utils.Symbols

data class TrackScreenState(
    val mainCurrencyName: String = "EUR",
    val subCurrenciesValue: Map<String, Double> = emptyMap(),
    val subCurrenciesFluctuation: Map<String, Pair<Double, Double>> = emptyMap(),
    val symbols: Symbols? = null,
    val isLoadingValues: Boolean = false,
    val isLoadingCurrencies: Boolean = false,
    val isLoadingFluctuation: Boolean = false
)
