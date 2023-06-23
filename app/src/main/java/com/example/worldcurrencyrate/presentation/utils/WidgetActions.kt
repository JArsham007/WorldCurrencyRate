package com.example.worldcurrencyrate.presentation.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.example.worldcurrencyrate.utils.Constants

class OpenDialogAction : ActionCallback {

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        Log.i("TAG", "onAction: ")
        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) {
            it.toMutablePreferences().apply {
                val isOpen = this[booleanPreferencesKey(Constants.IS_DIALOG_OPEN_PREF)] ?: false
                this[booleanPreferencesKey(Constants.IS_DIALOG_OPEN_PREF)] = !isOpen
                Log.i("TAG", "onAction: ${this[booleanPreferencesKey(Constants.IS_DIALOG_OPEN_PREF)]}")
            }
        }

    }

}

class OnSymbolClickedAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {

        val currencyName = parameters[ActionParameters.Key<String>
            (Constants.ADD_NEW_CURRENCY_ACTION_PARAMETERS)]

        currencyName?.let {
            updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) {
                it.toMutablePreferences().apply {
                    this[stringPreferencesKey(Constants.SUB_CURRENCIES_PREF)] =
                        this[stringPreferencesKey(Constants.SUB_CURRENCIES_PREF)] + ",$currencyName"
                    this[booleanPreferencesKey(Constants.IS_DIALOG_OPEN_PREF)] = false

                    val intent = Intent(Constants.REFRESH_WIDGET_INTENT_EXTRA)
                    context.sendBroadcast(intent)
                }
            }
        }

    }

}