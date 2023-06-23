package com.example.worldcurrencyrate.presentation.screens.convert_screen

import com.example.worldcurrencyrate.domain.model.ConvertModel
import com.example.worldcurrencyrate.utils.Symbols
import java.time.LocalDate

data class ConvertScreenState(
    val mainCurrencyName: String = "EUR",
    val amount: String = "1",
    val date: LocalDate = LocalDate.now(),
    val subCurrencies: List<ConvertModel> = emptyList(),
    val symbols: Symbols? = null,
    val isLoadingValues: Boolean = false,
    val isLoadingSubCurrencyName: Boolean = false,
    val isLoadingNewCurrency: Boolean = false
)