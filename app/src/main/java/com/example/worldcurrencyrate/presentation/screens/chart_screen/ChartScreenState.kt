package com.example.worldcurrencyrate.presentation.screens.chart_screen

import com.example.worldcurrencyrate.domain.model.TimeSeriesModel
import com.example.worldcurrencyrate.presentation.utils.AxisValueOverrider
import com.example.worldcurrencyrate.presentation.utils.ChartXAxisValueFormatter
import com.example.worldcurrencyrate.presentation.utils.ChartYAxisValueFormatter
import com.example.worldcurrencyrate.utils.Constants
import com.example.worldcurrencyrate.utils.Symbols
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import java.time.LocalDate

data class ChartScreenState(
    val mainCurrency: String = "",
    val secondCurrency: String = "",
    val mainToSecondCurrency: String = "",
    val chartData: TimeSeriesModel? = null,
    val chartEntryModelProducer: ChartEntryModelProducer? = null,
    val change: Double = 0.0,
    val changePct: Double = 0.0,
    val highPrice: Double = 0.0,
    val lowPrice: Double = 0.0,
    val symbols: Symbols? = null,
    val axisValueOverrider: AxisValueOverrider? = null,
    val chartXAxisValueFormatter: ChartXAxisValueFormatter? = null,
    val chartYAxisValueFormatter: ChartYAxisValueFormatter? = null,
    val startDate: LocalDate = LocalDate.now().minusDays(30),
    val endDate: LocalDate = LocalDate.now(),
    val formattedStartDate: String = "",
    val formattedEndDate: String = "",
    val isLoadingChartData: Boolean = false,
    val isLoadingOtherData: Boolean = false,
    val isLoadingConversionData: Boolean = false,
    val isLoadingStatsData: Boolean = false,
    val chartFilter: String = Constants.CHART_MONTHLY
)
