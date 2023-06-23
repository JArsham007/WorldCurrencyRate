package com.example.worldcurrencyrate.navigation

import com.example.worldcurrencyrate.R

sealed class BottomNavScreens(val title: String, val icon: Int, val rout: String) {

    object ConvertScreen : BottomNavScreens("Convert", R.drawable.currency_exchange, "convert_screen")
    object ChartsScreen : BottomNavScreens("Charts", R.drawable.charts, "charts_screen/{mainCurrency}/{subCurrency}") {
        fun passCurrencies(
            mainCurrency: String,
            subCurrency: String
        ): String {
            return "charts_screen/$mainCurrency/$subCurrency"
        }
    }
    object TrackScreen : BottomNavScreens("Track", R.drawable.track, "track_screen")
    object MoreScreen : BottomNavScreens("More", R.drawable.more, "more_screen")

}
