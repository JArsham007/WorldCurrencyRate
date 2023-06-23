package com.example.worldcurrencyrate.presentation.utils

sealed class UiEvent {

    object ShowSnackBar: UiEvent()
    object ShowDatePicker: UiEvent()
    data class ShowErrorSnackBar(val message: String): UiEvent()

}
