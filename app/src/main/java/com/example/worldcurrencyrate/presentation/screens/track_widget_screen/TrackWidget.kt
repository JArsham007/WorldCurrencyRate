package com.example.worldcurrencyrate.presentation.screens.track_widget_screen

import android.util.Log
import androidx.compose.runtime.*
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.size
import androidx.glance.unit.ColorProvider
import com.example.worldcurrencyrate.presentation.utils.OpenDialogAction
import com.example.worldcurrencyrate.ui.theme.BackgroundColor
import com.example.worldcurrencyrate.ui.theme.MORE_ITEMS_ICON_SIZE
import com.example.worldcurrencyrate.ui.theme.TextColor
import com.example.worldcurrencyrate.utils.Constants

class TrackWidget : GlanceAppWidget() {

    @Composable
    override fun Content() {
        Log.i("TAG", "aduseufhsufh: Content")
        val prefs = currentState<Preferences>()
        val mainCurrency = prefs[stringPreferencesKey(Constants.MAIN_CURRENCY_PREF)] ?: "EUR"
        val mainCurrencyActualName =
            prefs[stringPreferencesKey(Constants.MAIN_CURRENCY_ACTUAL_NAME_PREF)] ?: ""
        val subCurrencies = prefs[stringPreferencesKey(Constants.SUB_CURRENCIES_PREF)] ?: "USD,"
        val subCurrenciesValue =
            prefs[stringPreferencesKey(Constants.SUB_CURRENCIES_VALUES_PREF)] ?: ""
        val subCurrenciesFluctuation =
            prefs[stringPreferencesKey(Constants.SUB_CURRENCIES_FLUCTUATION_PREF)] ?: ""
        val isThereAnError = prefs[booleanPreferencesKey(Constants.IS_THERE_AN_ERROR_PREF)] ?: false
        val isLoading = prefs[booleanPreferencesKey(Constants.IS_LOADING_PREF)]
        Log.i("TAG", "aduseufhsufh: Got values")
        if (isLoading != null && isLoading == false) {
            var isOpen = false
            val state = WidgetScreenState(
                mainCurrency = mainCurrency,
                mainCurrencyActualName = mainCurrencyActualName,
                subCurrencies = decodeSubCurrencies(subCurrencies),
                subCurrenciesValue = decodeSubCurrenciesValue(subCurrenciesValue),
                subCurrenciesFluctuation = decodeSubCurrenciesFluctuation(subCurrenciesFluctuation),
                isThereAnError = isThereAnError,
                isLoading = isLoading
            )
            Log.i("TAG", "Contentawd: $isOpen")
            if (!isOpen) {
                WidgetScreen(
                    state = state,
                    onItemClicked = {
                        Log.i("TAG", "Contentd: bbb")
                        isOpen = !isOpen
                        actionRunCallback<OpenDialogAction>()
                    }
                )
            } else {
                SymbolsList(state = state)
            }
            Log.i("TAG", "aduseufhsufh: Show the screen")
        } else {
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(BackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = GlanceModifier
                        .size(MORE_ITEMS_ICON_SIZE),
                    color = ColorProvider(TextColor),
                )
            }
            Log.i("TAG", "aduseufhsufh: Loading...")
        }
        Log.i("TAG", "aduseufhsufh: Finnish")
    }

    private fun decodeSubCurrencies(s: String): List<String> {
        return s.split(",")
    }

    private fun decodeSubCurrenciesValue(s: String): Map<String, Double> {

        val decodedString = s.split(",").map {
            val firstValue = it.substringBefore("=")
            val secondValue = it.substringAfter("=")
            firstValue to secondValue.toDouble()
        }

        return decodedString.toMap()

    }

    private fun decodeSubCurrenciesFluctuation(s: String): Map<String, Pair<Double, Double>> {

        val decodedString = s.split("&").map {
            val firstValue = it.substringBefore("=")
            val secondValue = it.substringAfter("=")
            val removedOpenParenthesesValue = secondValue.replace("(", "")
            val removedCloseParenthesesValue = removedOpenParenthesesValue.replace(")", "")
            val secondValueFirstValue = removedCloseParenthesesValue.substringBefore(",")
            val secondValueSecondValue = removedCloseParenthesesValue.substringAfter(",")
            firstValue to (secondValueFirstValue.toDouble() to secondValueSecondValue.toDouble())
        }

        return decodedString.toMap()

    }

}