package com.example.worldcurrencyrate.presentation.screens.track_screen

sealed class TrackScreenEvent {
    class OnMainCurrencyChanged(val newCurrency: String): TrackScreenEvent()
}
