package com.example.worldcurrencyrate.presentation.screens.chart_screen

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.worldcurrencyrate.R
import com.example.worldcurrencyrate.presentation.common.ErrorSnackBar
import com.example.worldcurrencyrate.presentation.common.SymbolsDialog
import com.example.worldcurrencyrate.presentation.common.datePicker
import com.example.worldcurrencyrate.presentation.utils.*
import com.example.worldcurrencyrate.ui.theme.*
import com.example.worldcurrencyrate.utils.Constants
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.component.shape.shader.fromBrush
import com.patrykandpatrick.vico.core.DefaultAlpha
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.component.text.TextComponent
import com.patrykandpatrick.vico.core.dimensions.MutableDimensions
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.flow.collectLatest
import java.text.NumberFormat

@ExperimentalFoundationApi
@Composable
fun ChartScreen(
    navController: NavHostController,
    viewModel: ChartScreenViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsState()

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
            ChartTitle()
            CompareBox()
            Spacer(modifier = Modifier.height(MEDIUM_PADDING))
            ConversionAndAverageDataBox()
            Spacer(modifier = Modifier.height(MEDIUM_PADDING))
            ChartFilterBox()
            Spacer(modifier = Modifier.height(MEDIUM_PADDING))
            AnimatedVisibility(
                visible = state.chartFilter == Constants.CHART_CUSTOM,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                ChartCustomDatePicker()
            }
//            Spacer(modifier = Modifier.height(MEDIUM_PADDING))
            ChartBox()
            Spacer(modifier = Modifier.height(MEDIUM_PADDING))
            StatsBox()
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
fun ChartTitle() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(85.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.charts),
            fontWeight = FontWeight.Medium,
            fontSize = CURRENCY_NAME_TEXT_SIZE,
            color = TextColor
        )
    }

}

@ExperimentalFoundationApi
@Composable
fun CompareBox(
    viewModel: ChartScreenViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsState()

    var isMainSymbolBoxClicked by remember {
        mutableStateOf(false)
    }
    var isSubSymbolBoxClicked by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current

    Log.i("TAG", "CompareBox: ${state.secondCurrency}")

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        SymbolBox(
            modifier = Modifier
                .clickable {
                    isMainSymbolBoxClicked = !isMainSymbolBoxClicked
                },
            icon = state.mainCurrency.lowercase().getCurrencyIcon(context),
            name = state.mainCurrency
        )
        Icon(
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = false)
            ) {
                viewModel.onEvent(ChartScreenEvent.OnMoveCurrencies)
            },
            painter = painterResource(id = R.drawable.move_currency),
            contentDescription = "Move",
            tint = TextColor
        )
        SymbolBox(
            modifier = Modifier
                .clickable {
                    isSubSymbolBoxClicked = !isSubSymbolBoxClicked
                },
            icon = state.secondCurrency.lowercase().getCurrencyIcon(context),
            name = state.secondCurrency
        )
    }

    if (isMainSymbolBoxClicked) {
        SymbolsDialog(
            onDismissMenu = { isMainSymbolBoxClicked = false },
            symbols = state.symbols!!,
            onSelectedItem = {
                viewModel.onEvent(ChartScreenEvent.OnMainCurrencyChanged(newCurrency = it))
            }
        )
    }

    if (isSubSymbolBoxClicked) {
        SymbolsDialog(
            onDismissMenu = { isSubSymbolBoxClicked = false },
            symbols = state.symbols!!,
            onSelectedItem = {
                viewModel.onEvent(ChartScreenEvent.OnSecondCurrencyChanged(newCurrency = it))
            }
        )
    }

}

@Composable
fun SymbolBox(
    modifier: Modifier = Modifier,
    icon: Int,
    name: String
) {

    Card(
        modifier = modifier.clip(RoundedCornerShape(MEDIUM_PADDING)),
        shape = RoundedCornerShape(MEDIUM_PADDING),
        backgroundColor = MainItemBackgroundColor,
//        elevation = (-1).dp
    ) {
        Row(
            modifier = modifier
                .padding(SMALL_PADDING),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .size(COUNTRY_FLAG_ICON_SIZE)
                    .padding(EXTRA_SMALL_PADDING),
                painter = painterResource(id = icon),
                contentDescription = "Currency",
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(EXTRA_SMALL_PADDING))
            Text(
                text = name,
                fontSize = TEXT_SYMBOLS_MENU_FONT_SIZE,
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

}

@Composable
fun ConversionAndAverageDataBox(
    viewModel: ChartScreenViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsState()

    val changeToString = if (state.change >= 0) "+${state.change}" else "${state.change}"
    val changeColor = if (state.change >= 0) PositiveColor else NegativeColor
    val changeIcon =
        if (state.change >= 0) R.drawable.round_arrow_upward_24 else R.drawable.round_arrow_downward_24

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            if (state.isLoadingConversionData) {
                CircularProgressIndicator(
                    modifier = Modifier.size(MORE_ITEMS_ICON_SIZE),
                    color = TextColor,
                    strokeWidth = 1.dp
                )
            } else {
                Text(
                    modifier = Modifier.onSizeChanged {

                    },
                    text = "1 ${state.mainCurrency} = ${state.mainToSecondCurrency} ${state.secondCurrency}",
                    fontSize = CURRENCY_NAME_TEXT_SIZE,
                    fontWeight = FontWeight.Medium,
                    color = TextColor
                )
            }
            Spacer(modifier = Modifier.height(EXTRA_SMALL_PADDING))
            if (state.isLoadingOtherData) {
                CircularProgressIndicator(
                    modifier = Modifier.size(PROGRESS_BAR_SIZE),
                    color = TextColor,
                    strokeWidth = 1.dp
                )
            } else {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = SecondaryTextColor
                            )
                        ) {
                            append(
                                when (state.chartFilter) {
                                    Constants.CHART_MONTHLY -> {
                                        "Past month "
                                    }
                                    Constants.CHART_WEEKLY -> {
                                        "Past week "
                                    }
                                    Constants.CHART_YEARLY -> {
                                        "Past year "
                                    }
                                    else -> {
                                        "Custom "
                                    }
                                }
                            )
                        }
                        withStyle(
                            style = SpanStyle(
                                color = changeColor
                            )
                        ) {
                            append("$changeToString (${state.changePct}%)")
                        }
                    },
                    fontSize = TEXT_MENU_FONT_SIZE,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
        Icon(
            modifier = Modifier.size(CURRENCY_LIST_ITEM_MENU_ICON_SIZE),
            painter = painterResource(id = changeIcon),
            contentDescription = "Arrow",
            tint = changeColor
        )
    }

}

@Composable
fun ChartFilterBox(
    viewModel: ChartScreenViewModel = hiltViewModel()
) {

    val state = viewModel.state.collectAsState()

    val listOfButtons = mutableListOf(
        stringResource(R.string.monthly),
        stringResource(R.string.weekly),
        stringResource(R.string.yearly),
        stringResource(R.string.custom)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(times = 4) {
            ChartsButton(
                text = listOfButtons[it],
                isSelected = state.value.chartFilter == listOfButtons[it],
                onClick = {
                    viewModel.onEvent(ChartScreenEvent.OnChartFilterSelected(listOfButtons[it]))
                },
            )
        }
    }

}

@Composable
fun ChartCustomDatePicker(
    viewModel: ChartScreenViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsState()

    val startDatePickerState = rememberMaterialDialogState()
    val endDatePickerState = rememberMaterialDialogState()

    var isEndDateSelected by remember {
        mutableStateOf(false)
    }
    var isStartDateSelected by remember {
        mutableStateOf(false)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.from),
            fontSize = TEXT_MENU_FONT_SIZE,
            fontWeight = FontWeight.Medium,
            color = SecondaryTextColor,
        )
        Spacer(modifier = Modifier.width(SMALL_PADDING))
        Card(
            modifier = Modifier
                .clip(RoundedCornerShape(MEDIUM_PADDING))
                .clickable {
                    startDatePickerState.show()
                },
            shape = RoundedCornerShape(MEDIUM_PADDING),
            backgroundColor = BackgroundColor,
        ) {
            Text(
                modifier = Modifier.padding(4.dp),
                text = state.formattedStartDate,
                fontSize = TEXT_SYMBOLS_MENU_FONT_SIZE,
                fontWeight = FontWeight.Medium,
                color = TextColor,
            )
        }
        Spacer(modifier = Modifier.width(SMALL_PADDING))
        Text(
            text = stringResource(R.string.to),
            fontSize = TEXT_MENU_FONT_SIZE,
            fontWeight = FontWeight.Medium,
            color = SecondaryTextColor,
        )
        Spacer(modifier = Modifier.width(SMALL_PADDING))
        Card(
            modifier = Modifier
                .clip(RoundedCornerShape(MEDIUM_PADDING))
                .clickable {
                    endDatePickerState.show()
                },
            shape = RoundedCornerShape(MEDIUM_PADDING),
            backgroundColor = BackgroundColor,
        ) {
            Text(
                modifier = Modifier.padding(4.dp),
                text = state.formattedEndDate,
                fontSize = TEXT_SYMBOLS_MENU_FONT_SIZE,
                fontWeight = FontWeight.Medium,
                color = TextColor,
            )
        }
    }

    val startDate = datePicker(
        initialDate = state.startDate,
        dateDialogState = startDatePickerState,
        onOkClicked = {
            isStartDateSelected = !isStartDateSelected
        }
    )

    if (isStartDateSelected) {
        viewModel.onEvent(
            ChartScreenEvent.OnCustomDateChanged(
                startDate = startDate,
                endDate = state.endDate
            )
        )
        isStartDateSelected = false
    }

    val endDate = datePicker(
        initialDate = state.endDate,
        dateDialogState = endDatePickerState,
        onOkClicked = {
            isEndDateSelected = !isEndDateSelected
        }
    )

    if (isEndDateSelected) {
        viewModel.onEvent(
            ChartScreenEvent.OnCustomDateChanged(
                startDate = state.startDate,
                endDate = endDate
            )
        )
        isEndDateSelected = false
    }

}

@Composable
fun ChartBox(
    viewModel: ChartScreenViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsState()

    val sideLabel = TextComponent.Builder().apply {
        color = TextColor.toArgb()
        padding = MutableDimensions(startDp = 0f, endDp = 5f, topDp = 10f, bottomDp = 10f)
        textSizeSp = 14f
    }.build()

    val bottomLabel = TextComponent.Builder().apply {
        color = TextColor.toArgb()
        textSizeSp = 10f
    }.build()

    val placeHolderChartModel = entryModelOf(0f, 0f, 0f, 0f, 0f, 0f)

    if (!state.isLoadingChartData && state.chartEntryModelProducer != null) {
        Chart(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            chart = lineChart(
                axisValuesOverrider = state.axisValueOverrider,
                lines = listOf(
                    LineChart.LineSpec(
                        lineColor = ButtonsColor.toArgb(),
                        lineBackgroundShader = DynamicShaders.fromBrush(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    ButtonsColor.copy(alpha = DefaultAlpha.LINE_BACKGROUND_SHADER_START),
                                    ButtonsColor.copy(alpha = DefaultAlpha.LINE_BACKGROUND_SHADER_END),
                                )
                            )
                        )
                    )
                )
            ),
            chartModelProducer = state.chartEntryModelProducer!!,
            startAxis = startAxis(
                label = sideLabel,
                tick = null,
                axis = null,
                valueFormatter = state.chartYAxisValueFormatter!!,
                guideline = LineComponent(
                    shape = com.patrykandpatrick.vico.core.component.shape.Shapes.pillShape,
                    color = MainItemBackgroundColor.toArgb(),
                    strokeWidthDp = 1.5f
                ),
            ),
            bottomAxis = bottomAxis(
                labelRotationDegrees = 45f,
                label = bottomLabel,
                axis = LineComponent(
                    shape = com.patrykandpatrick.vico.core.component.shape.Shapes.pillShape,
                    color = MainItemBackgroundColor.toArgb(),
                    strokeWidthDp = 1.5f
                ),
                valueFormatter = state.chartXAxisValueFormatter!!,
                tick = null,
                guideline = null
            )
        )
    } else {
        Chart(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            chart = lineChart(
                lines = listOf(
                    LineChart.LineSpec(
                        lineColor = Color.Unspecified.toArgb(),
                    )
                )
            ),
            model = placeHolderChartModel,
            startAxis = startAxis(
                label = sideLabel,
                tick = null,
                axis = null,
                guideline = LineComponent(
                    shape = com.patrykandpatrick.vico.core.component.shape.Shapes.pillShape,
                    color = MainItemBackgroundColor.toArgb(),
                    strokeWidthDp = 1.5f
                ),
            ),
            bottomAxis = bottomAxis(
                labelRotationDegrees = 45f,
                label = bottomLabel,
                axis = LineComponent(
                    shape = com.patrykandpatrick.vico.core.component.shape.Shapes.pillShape,
                    color = MainItemBackgroundColor.toArgb(),
                    strokeWidthDp = 1.5f
                ),
                tick = null,
                guideline = null
            )
        )
    }

}

@Composable
fun StatsBox(
    viewModel: ChartScreenViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsState()

    val limitedHighPriceDecimals = String.format("%.6f", state.highPrice)
    val limitedLowPriceDecimals = String.format("%.6f", state.lowPrice)
    val formattedHighPrice =
        NumberFormat.getNumberInstance().format(limitedHighPriceDecimals.toDouble())
    val formattedLowPrice =
        NumberFormat.getNumberInstance().format(limitedLowPriceDecimals.toDouble())

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.stats),
            fontSize = CURRENCY_NAME_TEXT_SIZE,
            fontWeight = FontWeight.Medium,
            color = TextColor
        )
        Spacer(modifier = Modifier.height(SMALL_PADDING))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.high),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = SecondaryTextColor
            )
            if (state.isLoadingStatsData) {
                CircularProgressIndicator(
                    modifier = Modifier.size(MORE_ITEMS_ICON_SIZE),
                    color = TextColor,
                    strokeWidth = 1.dp
                )
            } else {
                Text(
                    text = formattedHighPrice,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextColor
                )
            }
        }
        Spacer(modifier = Modifier.height(SMALL_PADDING))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.low),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = SecondaryTextColor
            )
            if (state.isLoadingStatsData) {
                CircularProgressIndicator(
                    modifier = Modifier.size(MORE_ITEMS_ICON_SIZE),
                    color = TextColor,
                    strokeWidth = 1.dp
                )
            } else {
                Text(
                    text = formattedLowPrice,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextColor
                )
            }
        }
        Spacer(modifier = Modifier.height(SMALL_PADDING))
    }

}

@Composable
fun ChartsButton(
    modifier: Modifier = Modifier,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {

    Button(
        modifier = modifier,
        onClick = {
            onClick()
        },
        shape = RoundedCornerShape(LARGE_PADDING),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (isSelected) ButtonsColor else BackgroundColor
        ),
        elevation = ButtonDefaults.elevation(
            defaultElevation = (-1).dp,
            pressedElevation = (-1).dp,
            disabledElevation = (-1).dp,
            hoveredElevation = (-1).dp,
            focusedElevation = (-1).dp
        )
    ) {
        Text(
            text = text,
            fontSize = TEXT_MENU_FONT_SIZE,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) TextColor else SecondaryTextColor
        )
    }

}


