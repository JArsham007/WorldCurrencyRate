package com.example.worldcurrencyrate.presentation.screens.track_screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.worldcurrencyrate.R
import com.example.worldcurrencyrate.navigation.BottomNavScreens
import com.example.worldcurrencyrate.presentation.common.ErrorSnackBar
import com.example.worldcurrencyrate.presentation.common.SymbolsDialog
import com.example.worldcurrencyrate.presentation.common.datePicker
import com.example.worldcurrencyrate.presentation.components.CurrencyListItem
import com.example.worldcurrencyrate.presentation.components.LatestRateListItem
import com.example.worldcurrencyrate.presentation.screens.convert_screen.*
import com.example.worldcurrencyrate.presentation.utils.UiEvent
import com.example.worldcurrencyrate.presentation.utils.getCurrencyIcon
import com.example.worldcurrencyrate.ui.theme.*
import com.example.worldcurrencyrate.utils.Constants
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.flow.collectLatest
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun TrackScreen(
    navController: NavHostController,
    viewModel: TrackScreenViewModel = hiltViewModel()
) {

    val state = viewModel.state.collectAsState()

    val errorSnackState = remember {
        SnackbarHostState()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = BackgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = BackgroundColor)
                .padding(MEDIUM_PADDING),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            TrackTitle()
            DateBox()
            Spacer(modifier = Modifier.height(LARGE_PADDING))
            BaseCurrencyBox()
            Spacer(modifier = Modifier.height(MEDIUM_LARGE_PADDING))
            SpacerLine()
            Spacer(modifier = Modifier.height(MEDIUM_LARGE_PADDING))
            LatestRateCurrencyList(
                navController = navController
            )
        }

        LaunchedEffect(key1 = true) {
            viewModel.event.collectLatest {
                when (it) {
                    UiEvent.ShowDatePicker -> {}
                    is UiEvent.ShowErrorSnackBar -> {
                        errorSnackState.showSnackbar(
                            message = it.message
                        )
                    }
                    UiEvent.ShowSnackBar -> {}
                }
            }
        }

        SnackbarHost(
            modifier = Modifier
                .align(Alignment.BottomStart),
            hostState = errorSnackState,
        ) {
            ErrorSnackBar(
                message = it.message
            )
        }

    }

}

@Composable
fun TrackTitle() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(85.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.track),
            fontWeight = FontWeight.Medium,
            fontSize = CURRENCY_NAME_TEXT_SIZE,
            color = TextColor
        )
    }

}

@Composable
fun DateBox() {

    val formattedDateForUser by remember {
        mutableStateOf(
            DateTimeFormatter
                .ofPattern("dd MMM yyyy")
                .format(LocalDate.now())
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            modifier = Modifier.size(CALENDAR_ICON_SIZE),
            painter = painterResource(id = R.drawable.calendar),
            contentDescription = "Date",
            tint = TextColor
        )
        Spacer(modifier = Modifier.width(SMALL_PADDING))
        Text(
            text = stringResource(R.string.last_update),
            fontSize = TEXT_MENU_FONT_SIZE,
            fontWeight = FontWeight.Medium,
            color = SecondaryTextColor
        )
        Spacer(modifier = Modifier.width(EXTRA_SMALL_PADDING))
        Text(
            text = formattedDateForUser,
            fontSize = TEXT_MENU_FONT_SIZE,
            fontWeight = FontWeight.Medium,
            color = TextColor
        )
    }

}

@Composable
fun BaseCurrencyBox(
    viewModel: TrackScreenViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    val currencyIcon = state.mainCurrencyName.lowercase().getCurrencyIcon(context)

    var showSymbolsDialog by remember {
        mutableStateOf(false)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MainItemBackgroundColor, RoundedCornerShape(MEDIUM_PADDING)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier
                .padding(SMALL_PADDING)
                .clip(RoundedCornerShape(SMALL_PADDING))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = true)
                ) {
                    showSymbolsDialog = !showSymbolsDialog
                },
            shape = RoundedCornerShape(SMALL_PADDING),
            backgroundColor = MainItemBackgroundColor,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .size(COUNTRY_FLAG_ICON_SIZE)
                        .padding(EXTRA_SMALL_PADDING),
                    painter = painterResource(id = currencyIcon),
                    contentDescription = "Currency",
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.width(EXTRA_SMALL_PADDING))
                Text(
                    text = state.mainCurrencyName,
                    fontSize = CURRENCY_NAME_TEXT_SIZE,
                    fontWeight = FontWeight.Medium,
                    color = TextColor
                )
                Spacer(modifier = Modifier.width(EXTRA_SMALL_PADDING))
                Icon(
                    modifier = Modifier.size(MORE_ITEMS_ICON_SIZE),
                    painter = painterResource(id = R.drawable.more_items),
                    contentDescription = "More",
                    tint = TextColor
                )
            }
        }
        Spacer(modifier = Modifier.width(EXTRA_SMALL_PADDING))
        Column(
            modifier = Modifier
                .padding(SMALL_PADDING),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = state.symbols?.toMap()?.get(state.mainCurrencyName) ?: "",
                color = TextColor,
                fontWeight = FontWeight.Medium,
                fontSize = TEXT_SYMBOLS_MENU_FONT_SIZE,
                style = TextStyle(
                    textDirection = TextDirection.Rtl
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

    if (showSymbolsDialog) {
        SymbolsDialog(
            onDismissMenu = { showSymbolsDialog = false },
            symbols = state.symbols!!,
            onSelectedItem = {
                viewModel.onEvent(TrackScreenEvent.OnMainCurrencyChanged(newCurrency = it))
            }
        )
    }

}

@Composable
fun LatestRateCurrencyList(
    viewModel: TrackScreenViewModel = hiltViewModel(),
    navController: NavHostController
) {

    val state = viewModel.state.collectAsState()

    val listData = state.value.subCurrenciesValue.toList()

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(
            count = listData.size
        ) { index ->

            val change = state.value.subCurrenciesFluctuation.toList()[index].second.first
            val changePct = state.value.subCurrenciesFluctuation.toList()[index].second.second

            val changeNegOrPositive = if (change > 0) "+$change" else change.toString()
            val changePctNegOrPositive = if (changePct > 0) "+$changePct%" else "$changePct%"

            val secondaryText = "$changeNegOrPositive ($changePctNegOrPositive)"
            val secondaryTextColor = if (change > 0) PositiveColor else NegativeColor

            if (!state.value.isLoadingCurrencies) {
                Spacer(modifier = Modifier.height(SMALL_PADDING))
                LatestRateListItem(
                    symbolName = listData[index].first,
                    mainText = listData[index].second.toString(),
                    secondaryText = secondaryText,
                    secondaryTextColor = secondaryTextColor,
                    onMenuItemsClicked = {
                        when (it) {
                            Constants.CURRENCY_MENU_GO_TO_CHARTS -> {
                                navController.navigate(
                                    BottomNavScreens.ChartsScreen.passCurrencies(
                                        mainCurrency = state.value.mainCurrencyName,
                                        subCurrency = listData[index].first
                                    )
                                ) {
                                    popUpTo(
                                        BottomNavScreens.TrackScreen.rout
                                    ) {
                                        saveState = true
                                    }
                                }
                            }
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(SMALL_PADDING))
        }
        item {
            if (state.value.isLoadingCurrencies) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(MORE_ITEMS_ICON_SIZE),
                    color = TextColor,
                    strokeWidth = 1.dp
                )
                Spacer(modifier = Modifier.height(SMALL_PADDING))
            }
        }
    }

}