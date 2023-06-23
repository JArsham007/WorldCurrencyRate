package com.example.worldcurrencyrate.presentation.screens.track_widget_screen

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.example.worldcurrencyrate.domain.usecases.UseCases
import com.example.worldcurrencyrate.utils.Constants
import com.example.worldcurrencyrate.utils.Resource
import com.example.worldcurrencyrate.utils.Symbols
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TrackWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = TrackWidget()

    private val coroutineScope = MainScope()

    @Inject
    lateinit var useCases: UseCases

    @Inject
    lateinit var symbols: Symbols

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        Log.i("TAG", "aduseufhsufh: onUpdate")
        observeData(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == Constants.REFRESH_WIDGET_INTENT_EXTRA) {
            observeData(
                context = context
            )
        }

    }

    private fun observeData(
        context: Context,
    ) {

        coroutineScope.launch {

            val glanceId =
                GlanceAppWidgetManager(context).getGlanceIds(TrackWidget::class.java).firstOrNull()

            glanceId?.let { id ->
                updateAppWidgetState(
                    context = context,
                    definition = PreferencesGlanceStateDefinition,
                    glanceId = id
                ) { pref ->
                    pref.toMutablePreferences().apply {

                        this[booleanPreferencesKey(Constants.IS_DIALOG_OPEN_PREF)] = false

                        val baseCurrency = this[stringPreferencesKey(Constants.MAIN_CURRENCY_PREF)] ?: "EUR"
                        val secondCurrencies = this[stringPreferencesKey(Constants.SUB_CURRENCIES_PREF)] ?: "USD,"

                        this[stringPreferencesKey(Constants.MAIN_CURRENCY_ACTUAL_NAME_PREF)] =
                            symbols.toMap()[baseCurrency] ?: "Euro"

                        useCases.latestCurrency.invoke(from = baseCurrency, to = secondCurrencies)
                            .collect { data ->
                                when (data) {
                                    is Resource.Success -> {
                                        this[stringPreferencesKey(Constants.SUB_CURRENCIES_VALUES_PREF)] =
                                            data.data?.latestRate?.toList()?.joinToString {
                                                "${it.first} = ${it.second}"
                                            } ?: ""

                                        this[stringPreferencesKey(Constants.SUB_CURRENCIES_FLUCTUATION_PREF)] =
                                            data.data?.fluctuationRate?.toList()?.joinToString("&") {
                                                "${it.first} = (${it.second.first}, ${it.second.second})"
                                            } ?: ""

                                        Log.i("TAG", "observeData: ${
                                            data.data?.latestRate?.toList()?.joinToString()
                                        }")
                                    }
                                    is Resource.Error -> {
                                        this[booleanPreferencesKey(Constants.IS_THERE_AN_ERROR_PREF)] =
                                            data.error != null
                                        Log.i("TAG", "observeData: error")
                                    }
                                    is Resource.Loading -> {
                                        this[booleanPreferencesKey(Constants.IS_LOADING_PREF)] =
                                            data.isLoading
                                        Log.i("TAG", "observeData: loading...")
                                    }
                                }
                            }

                    }
                }
                glanceAppWidget.update(context, id)
            }

        }

    }

}