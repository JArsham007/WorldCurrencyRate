package com.example.worldcurrencyrate.presentation.screens.convert_screen

import android.appwidget.AppWidgetProvider
import android.util.Log
import androidx.compose.animation.*
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.worldcurrencyrate.R
import com.example.worldcurrencyrate.navigation.BottomNavScreens
import com.example.worldcurrencyrate.presentation.common.ErrorSnackBar
import com.example.worldcurrencyrate.presentation.common.SymbolsDialog
import com.example.worldcurrencyrate.presentation.common.datePicker
import com.example.worldcurrencyrate.presentation.components.CurrencyListItem
import com.example.worldcurrencyrate.presentation.utils.UiEvent
import com.example.worldcurrencyrate.presentation.utils.getCurrencyIcon
import com.example.worldcurrencyrate.ui.theme.*
import com.example.worldcurrencyrate.utils.Constants
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ConvertScreen(
    navController: NavHostController,
    viewModel: ConvertScreenViewModel = hiltViewModel()
) {

    val state = viewModel.state.collectAsState()

    var showSymbolsDialog by remember {
        mutableStateOf(false)
    }

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
            ConvertTitle()
            DatePart()
            Spacer(modifier = Modifier.height(LARGE_PADDING))
            MainCurrencyBox()
            Spacer(modifier = Modifier.height(MEDIUM_LARGE_PADDING))
            SpacerLine()
            Spacer(modifier = Modifier.height(MEDIUM_LARGE_PADDING))
            CurrencyList(
                navController = navController,
                onAddCurrency = {
                    showSymbolsDialog = !showSymbolsDialog
                }
            )

        }

        if (showSymbolsDialog) {
            SymbolsDialog(
                onDismissMenu = { showSymbolsDialog = false },
                symbols = state.value.symbols!!,
                onSelectedItem = {
                    Log.i("TAG", "ConvertScreen: $it")
                    viewModel.onEvent(ConvertScreenEvent.OnAddCurrency(it))
                }
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
fun ConvertTitle() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(85.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(id = R.string.convert),
            fontWeight = FontWeight.Medium,
            fontSize = CURRENCY_NAME_TEXT_SIZE,
            color = TextColor
        )
    }

}

@Composable
fun DatePart(
    viewModel: ConvertScreenViewModel = hiltViewModel()
) {

    val state = viewModel.state.collectAsState()

    val dateDialogState = rememberMaterialDialogState()

    val formattedDateForUser by remember {
        derivedStateOf {
            DateTimeFormatter
                .ofPattern("dd MMM yyyy")
                .format(state.value.date)
        }
    }

    var date by remember {
        mutableStateOf(LocalDate.now())
    }

    var isOkClicked by remember {
        mutableStateOf(false)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(color = BackgroundColor)
            ) {
                dateDialogState.show()
            },
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
            text = stringResource(R.string.Date),
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
        Spacer(modifier = Modifier.width(SMALL_PADDING))
        Icon(
            modifier = Modifier.size(MORE_ITEMS_ICON_SIZE),
            painter = painterResource(id = R.drawable.edit),
            contentDescription = "Edit date",
            tint = TextColor
        )
    }

    date = datePicker(
        initialDate = state.value.date,
        dateDialogState = dateDialogState,
        onOkClicked = {
            isOkClicked = !isOkClicked
        }
    )

    if (isOkClicked) {
        viewModel.onEvent(ConvertScreenEvent.OnDateChange(date))
        isOkClicked = false
    }

}

@Composable
fun MainCurrencyBox(
    viewModel: ConvertScreenViewModel = hiltViewModel()
) {

    val state = viewModel.state.collectAsState()
    val context = LocalContext.current

    val currencyIcon = state.value.mainCurrencyName.lowercase().getCurrencyIcon(context)

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
                    text = state.value.mainCurrencyName,
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
            TextField(
                value = state.value.amount.ifEmpty { "1" },
                onValueChange = {
                    viewModel.onEvent(ConvertScreenEvent.OnAmountChange(amount = it))
                },
                textStyle = TextStyle(
                    color = TextColor,
                    fontWeight = FontWeight.Medium,
                    fontSize = CURRENCY_NAME_TEXT_SIZE,
                    textDirection = TextDirection.Rtl
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Go
                ),
                keyboardActions = KeyboardActions(
                    onGo = {
                        viewModel.onEvent(ConvertScreenEvent.UpdateValues)
                    }
                ),
                trailingIcon = {
                    Icon(
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(bounded = false)
                            ) {
                                viewModel.onEvent(ConvertScreenEvent.UpdateValues)
                            },
                        imageVector = Icons.Default.Done,
                        contentDescription = "",
                        tint = TextColor
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    textColor = TextColor,
                    backgroundColor = Color.Transparent,
                    cursorColor = TextColor,
                    focusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,

                    ),
                maxLines = 1,
                singleLine = true
            )
        }
    }

    if (showSymbolsDialog) {
        SymbolsDialog(
            onDismissMenu = { showSymbolsDialog = false },
            symbols = state.value.symbols!!,
            onSelectedItem = {
                viewModel.onEvent(ConvertScreenEvent.OnMainCurrencyChanged(newCurrency = it))
            }
        )
    }

}

@Composable
fun SpacerLine() {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(color = MainItemBackgroundColor)
    ) {}

}

@Composable
fun CurrencyList(
    viewModel: ConvertScreenViewModel = hiltViewModel(),
    navController: NavHostController,
    onAddCurrency: () -> Unit
) {

    val state = viewModel.state.collectAsState()

    var showSymbolsMenu by remember {
        mutableStateOf(false)
    }
    var clickedItemIndex by remember {
        mutableStateOf(0)
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(
            items = state.value.subCurrencies
        ) { model ->
            val result = model.result.div(model.query.amount)
            val limitedResultDecimals = String.format("%.5f", result)
            val formattedResult =
                NumberFormat.getNumberInstance().format(limitedResultDecimals.toDouble())
            val secondaryText =
                "1 ${model.query.from} = " + formattedResult + " " + model.query.to

            CurrencyListItem(
                symbolName = model.query.to,
                mainText = model.result.toString(),
                secondaryText = secondaryText,
                secondaryTextColor = SecondaryTextColor,
                isLoading = state.value.isLoadingValues,
                onChangeCurrency = {
                    clickedItemIndex = state.value.subCurrencies.indexOf(model)
                    showSymbolsMenu = !showSymbolsMenu
                },
                onMenuItemsClicked = {
                    when (it) {
                        Constants.CURRENCY_MENU_GO_TO_CHARTS -> {
                            navController.navigate(
                                BottomNavScreens.ChartsScreen.passCurrencies(
                                    mainCurrency = state.value.mainCurrencyName,
                                    subCurrency = model.query.to
                                )
                            ) {
                                popUpTo(
                                    BottomNavScreens.TrackScreen.rout
                                ) {
                                    saveState = true
                                }
                            }
                        }
                        Constants.CURRENCY_MENU_REMOVE -> {
                            viewModel.onEvent(
                                ConvertScreenEvent.OnCurrencyRemoved(
                                    state.value.subCurrencies.indexOf(
                                        model
                                    )
                                )
                            )
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(SMALL_PADDING))
        }
        item {
            if (state.value.isLoadingNewCurrency) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(MORE_ITEMS_ICON_SIZE),
                    color = TextColor,
                    strokeWidth = 1.dp
                )
                Spacer(modifier = Modifier.height(SMALL_PADDING))
            }
        }
        item {
            Button(
                onClick = { onAddCurrency() },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = ButtonsColor,
                    contentColor = TextColor
                ),
                shape = RoundedCornerShape(ADD_CURRENCY_BUTTON_CORNER_RADIUS),
                contentPadding = PaddingValues(
                    horizontal = MEDIUM_LARGE_PADDING,
                    vertical = MENU_CORNER_RADIUS
                )
            ) {
                Text(
                    text = stringResource(R.string.add_currency),
                    fontWeight = FontWeight.Medium,
                    fontSize = TEXT_MENU_FONT_SIZE,
                    color = TextColor
                )
            }
        }
    }

    if (showSymbolsMenu) {
        SymbolsDialog(
            onDismissMenu = { showSymbolsMenu = false },
            symbols = state.value.symbols!!,
            onSelectedItem = {
                viewModel.onEvent(
                    ConvertScreenEvent.OnSubCurrencyChanged(
                        index = clickedItemIndex,
                        newCurrency = it
                    )
                )
            }
        )
    }

}




