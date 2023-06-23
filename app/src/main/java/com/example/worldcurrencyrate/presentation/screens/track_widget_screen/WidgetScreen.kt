package com.example.worldcurrencyrate.presentation.screens.track_widget_screen

import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.glance.*
import androidx.glance.action.Action
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.layout.*
import androidx.glance.layout.Alignment.Companion.CenterVertically
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.worldcurrencyrate.R
import com.example.worldcurrencyrate.presentation.components.WidgetListItem
import com.example.worldcurrencyrate.presentation.utils.OnSymbolClickedAction
import com.example.worldcurrencyrate.presentation.utils.OpenDialogAction
import com.example.worldcurrencyrate.presentation.utils.getCurrencyIcon
import com.example.worldcurrencyrate.ui.theme.*
import com.example.worldcurrencyrate.utils.Constants

@Composable
fun WidgetScreen(
    state: WidgetScreenState,
    onItemClicked: () -> Action
) {

//    val pref = currentState<Preferences>()
//    val isOpen = pref[booleanPreferencesKey(Constants.IS_DIALOG_OPEN_PREF)] ?: false

//    var isOpen by remember {
//        mutableStateOf(false)
//    }

//    Log.i("TAG", "WidgetScreen: $isOpen")

//    if (!isOpen) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(SMALL_PADDING)
                .background(color = BackgroundColor)
        ) {
            MainCurrencyBox(state)
            Spacer(modifier = GlanceModifier.height(SMALL_PADDING))
            SpacerLine()
            Spacer(modifier = GlanceModifier.height(SMALL_PADDING))
            SubCurrenciesList(
                state = state,
                onItemClicked = {
                    onItemClicked()
                }
            )
        }
//    } else {
//        SymbolsList(state = state)
//    }

}

@Composable
fun MainCurrencyBox(state: WidgetScreenState) {

    val currencyIcon = state.mainCurrency
        .lowercase()
        .getCurrencyIcon(LocalContext.current)

    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .cornerRadius(SMALL_PADDING)
            .background(
                color = MainItemBackgroundColor
            ),
        verticalAlignment = CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            Image(
                modifier = GlanceModifier
                    .size(MAIN_CURRENCY_WIDGET_ICON_SIZE)
                    .padding(EXTRA_SMALL_PADDING),
                provider = ImageProvider(currencyIcon),
                contentDescription = "Currency",
            )
            Spacer(modifier = GlanceModifier.width(EXTRA_SMALL_PADDING))
            Text(
                text = state.mainCurrencyActualName,
                style = TextStyle(
                    color = ColorProvider(TextColor),
                    fontSize = WIDGET_MAIN_CURRENCY_TEXT_SIZE,
                    fontWeight = FontWeight.Medium,
                )
            )
        }
    }

}

@Composable
fun SpacerLine() {

    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .height(1.dp)
            .background(color = MainItemBackgroundColor)
    ) {}

}

@Composable
fun SubCurrenciesList(
    state: WidgetScreenState,
    onItemClicked: () -> Action
) {

    val listData = state.subCurrenciesValue.toList()

    val context = LocalContext.current

    LazyColumn(
        modifier = GlanceModifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Horizontal.CenterHorizontally
    ) {
        items(count = listData.size) { index ->

            val change = state.subCurrenciesFluctuation.toList()[index].second.first
            val changePct = state.subCurrenciesFluctuation.toList()[index].second.second

            val changeNegOrPositive = if (change > 0) "+$change" else change.toString()
            val changePctNegOrPositive = if (changePct > 0) "+$changePct%" else "$changePct%"

            val secondaryText = "$changeNegOrPositive ($changePctNegOrPositive)"
            val secondaryTextColor = if (change > 0) PositiveColor else NegativeColor

            if (!state.isLoading) {
                WidgetListItem(
                    modifier = GlanceModifier.fillMaxWidth(),
                    symbolName = listData[index].first,
                    mainText = listData[index].second.toString(),
                    secondaryText = secondaryText,
                    secondaryTextColor = secondaryTextColor,
                )
            }

        }
        item {
            Button(
                modifier = GlanceModifier
                    .cornerRadius(ADD_CURRENCY_BUTTON_CORNER_RADIUS),
                text = context.getString(R.string.add_currency),
                style = TextStyle(
                    fontWeight = FontWeight.Medium,
                    fontSize = TEXT_MENU_FONT_SIZE,
                    color = ColorProvider(TextColor)
                ),
                onClick = onItemClicked(),
                colors = ButtonColors(
                    backgroundColor = ColorProvider(ButtonsColor),
                    contentColor = ColorProvider(TextColor)
                ),
            )
        }
        item {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = GlanceModifier
                        .size(MORE_ITEMS_ICON_SIZE),
                    color = ColorProvider(TextColor),
                )
                Spacer(modifier = GlanceModifier.height(SMALL_PADDING))
            }
        }
    }

}

@Composable
fun SymbolsList(
    state: WidgetScreenState
) {

    val context = LocalContext.current

    val symbols = state.symbols.toMap().toList().slice(IntRange(0, 10))

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(EXTRA_SMALL_PADDING)
    ) {
        Image(
            modifier = GlanceModifier.size(PROGRESS_BAR_SIZE),
            provider = ImageProvider(R.drawable.round_arrow_back_24),
            contentDescription = "Back"
        )
        LazyColumn(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally
        ) {
            symbols.toMap().let { symbol ->
                items(count = symbol.size) { index ->
                    val item = symbol.keys.toList()[index]
                    SymbolsListItem(
                        icon = item.getCurrencyIcon(context),
                        currencySymbol = item,
                        onItemClicked = {
                            actionRunCallback<OnSymbolClickedAction>(
                                parameters = actionParametersOf(
                                    ActionParameters.Key<String>
                                        (Constants.ADD_NEW_CURRENCY_ACTION_PARAMETERS)
                                            to ""
                                )
                            )
                        }
                    )
                }
            }
        }

    }

}

@Composable
fun SymbolsListItem(
    icon: Int,
    currencySymbol: String,
    onItemClicked: () -> Action
) {

    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .clickable(
                onClick = onItemClicked()
            ),
        verticalAlignment = Alignment.Vertical.CenterVertically
    ) {
        Text(
            text = currencySymbol,
            style = TextStyle(
                fontSize = WIDGET_MAIN_CURRENCY_TEXT_SIZE,
                fontWeight = FontWeight.Medium,
                color = ColorProvider(TextColor)
            )
        )
        Spacer(modifier = GlanceModifier.height(SMALL_PADDING))
        Image(
            modifier = GlanceModifier.size(MENU_ICON_SIZE),
            provider = ImageProvider(icon),
            contentDescription = currencySymbol
        )
    }

}





