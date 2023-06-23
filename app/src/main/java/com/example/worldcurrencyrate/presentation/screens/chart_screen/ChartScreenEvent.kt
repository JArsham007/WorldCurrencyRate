package com.example.worldcurrencyrate.presentation.screens.chart_screen

import java.time.LocalDate

sealed class ChartScreenEvent {

    data class OnMainCurrencyChanged(val newCurrency: String) : ChartScreenEvent()
    data class OnSecondCurrencyChanged(val newCurrency: String) : ChartScreenEvent()
    object OnMoveCurrencies : ChartScreenEvent()
    data class OnCustomDateChanged(
        val startDate: LocalDate,
        val endDate: LocalDate,
    ) : ChartScreenEvent()
    data class OnChartFilterSelected(val chartFilter: String): ChartScreenEvent()

}
