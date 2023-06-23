package com.example.worldcurrencyrate.presentation.screens.track_widget_screen

import com.example.worldcurrencyrate.utils.Symbols

data class WidgetScreenState(
    val mainCurrency: String = "EUR",
    val mainCurrencyActualName: String = "Euro",
    val subCurrencies: List<String> = emptyList(),
    val subCurrenciesValue: Map<String, Double> = emptyMap(),
    val subCurrenciesFluctuation: Map<String, Pair<Double, Double>> = emptyMap(),
    val symbols: Symbols = Symbols(),
    val isThereAnError: Boolean = false,
    val isLoading: Boolean = false
)