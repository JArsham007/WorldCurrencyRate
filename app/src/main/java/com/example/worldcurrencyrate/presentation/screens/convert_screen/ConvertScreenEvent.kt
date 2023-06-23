package com.example.worldcurrencyrate.presentation.screens.convert_screen

import java.time.LocalDate

sealed class ConvertScreenEvent {

    class OnDateChange(val date: LocalDate): ConvertScreenEvent()
    object UpdateValues: ConvertScreenEvent()
    class OnAmountChange(val amount: String): ConvertScreenEvent()
    class OnMainCurrencyChanged(val newCurrency: String): ConvertScreenEvent()
    class OnSubCurrencyChanged(val index: Int, val newCurrency: String): ConvertScreenEvent()
    class OnCurrencyRemoved(val index: Int): ConvertScreenEvent()
    class OnAddCurrency(val newCurrency: String): ConvertScreenEvent()

}