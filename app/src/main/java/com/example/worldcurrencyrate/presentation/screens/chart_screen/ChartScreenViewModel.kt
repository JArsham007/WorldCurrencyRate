package com.example.worldcurrencyrate.presentation.screens.chart_screen

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.worldcurrencyrate.domain.usecases.UseCases
import com.example.worldcurrencyrate.presentation.utils.*
import com.example.worldcurrencyrate.utils.Constants
import com.example.worldcurrencyrate.utils.Resource
import com.example.worldcurrencyrate.utils.Symbols
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.UnknownHostException
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ChartScreenViewModel @Inject constructor(
    private val useCases: UseCases,
    private val axisValueOverrider: AxisValueOverrider,
    private val chartXAxisValueFormatter: ChartXAxisValueFormatter,
    private val chartYAxisValueFormatter: ChartYAxisValueFormatter,
    savedStateHandle: SavedStateHandle,
    private val symbols: Symbols
) : ViewModel() {

    private val _state = MutableStateFlow(ChartScreenState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<UiEvent>()
    val event = _event.asSharedFlow()

    init {

        val mainCurrency = savedStateHandle.get<String>(Constants.CHART_SCREEN_MAIN_CURRENCY_ARG)
        val subCurrency = savedStateHandle.get<String>(Constants.CHART_SCREEN_SUB_CURRENCY_ARG)

        _state.update {
            it.copy(
                mainCurrency = if (mainCurrency == "{mainCurrency}" || mainCurrency == null) "EUR" else mainCurrency,
                secondCurrency = if (subCurrency == "{subCurrency}" || subCurrency == null) "USD" else subCurrency
            )
        }

        val formattedStartDate = DateTimeFormatter
            .ofPattern("yyyy/MM/dd")
            .format(_state.value.startDate)

        val formattedEndDate = DateTimeFormatter
            .ofPattern("yyyy/MM/dd")
            .format(_state.value.endDate)

        _state.update {
            it.copy(
                symbols = symbols,
                axisValueOverrider = axisValueOverrider,
                chartXAxisValueFormatter = chartXAxisValueFormatter,
                chartYAxisValueFormatter = chartYAxisValueFormatter,
                formattedStartDate = formattedStartDate,
                formattedEndDate = formattedEndDate
            )
        }
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoadingOtherData = true,
                    isLoadingStatsData = true,
                    isLoadingChartData = true,
                )
            }
            updateOtherData()
            updateChartData()
            updateConversionData()
            initHighAndLowStats()
            initChartEntryModel()
        }

    }

    fun onEvent(event: ChartScreenEvent) {

        when (event) {
            is ChartScreenEvent.OnCustomDateChanged -> {

                val formattedStartDate = DateTimeFormatter
                    .ofPattern("yyyy/MM/dd")
                    .format(event.startDate)

                val formattedEndDate = DateTimeFormatter
                    .ofPattern("yyyy/MM/dd")
                    .format(event.endDate)

                viewModelScope.launch {
                    if (event.startDate.isEqual(event.endDate) || event.startDate.isAfter(event.endDate)) {
                        _event.emit(UiEvent.ShowErrorSnackBar("Choose a valid timeframe"))
                    }
                    _state.update {
                        it.copy(
                            startDate = event.startDate,
                            endDate = event.endDate,
                            formattedStartDate = formattedStartDate,
                            formattedEndDate = formattedEndDate,
                            isLoadingStatsData = true
                        )
                    }
                    updateOtherData()
                    updateChartData()
                    initHighAndLowStats()
                    initChartEntryModel()
                }
            }
            is ChartScreenEvent.OnMainCurrencyChanged -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            mainCurrency = event.newCurrency,
                            isLoadingStatsData = true
                        )
                    }
                    updateOtherData()
                    updateChartData()
                    updateConversionData()
                    initHighAndLowStats()
                    initChartEntryModel()
                }
            }
            ChartScreenEvent.OnMoveCurrencies -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            mainCurrency = _state.value.secondCurrency,
                            secondCurrency = _state.value.mainCurrency,
                            isLoadingStatsData = true
                        )
                    }
                    updateOtherData()
                    updateChartData()
                    updateConversionData()
                    initHighAndLowStats()
                    initChartEntryModel()
                }
            }
            is ChartScreenEvent.OnSecondCurrencyChanged -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            secondCurrency = event.newCurrency,
                            isLoadingStatsData = true
                        )
                    }
                    updateOtherData()
                    updateChartData()
                    updateConversionData()
                    initHighAndLowStats()
                    initChartEntryModel()
                }
            }
            is ChartScreenEvent.OnChartFilterSelected -> {
                viewModelScope.launch {
                    val startDate: LocalDate = when (event.chartFilter) {
                        Constants.CHART_MONTHLY -> LocalDate.now().minusDays(30)
                        Constants.CHART_WEEKLY -> LocalDate.now().minusDays(7)
                        Constants.CHART_YEARLY -> LocalDate.now().minusDays(365)
                        else -> LocalDate.now().minusDays(30)
                    }

                    val formattedStartDate = DateTimeFormatter
                        .ofPattern("yyyy/MM/dd")
                        .format(startDate)

                    val formattedEndDate = DateTimeFormatter
                        .ofPattern("yyyy/MM/dd")
                        .format(LocalDate.now())

                    _state.update {
                        it.copy(
                            chartFilter = event.chartFilter,
                            startDate = startDate,
                            endDate = LocalDate.now(),
                            formattedStartDate = formattedStartDate,
                            formattedEndDate = formattedEndDate,
                            isLoadingStatsData = true
                        )
                    }
                    updateChartData()
                    updateOtherData()
                    initHighAndLowStats()
                    initChartEntryModel()
                }
            }
        }

    }

    private suspend fun updateOtherData() {

        useCases.fluctuationCurrency.invoke(
            startDate = _state.value.startDate.toString(),
            endDate = _state.value.endDate.toString(),
            from = _state.value.mainCurrency,
            to = _state.value.secondCurrency
        ).collect { data ->
            when (data) {
                is Resource.Loading -> {
                    _state.update {
                        it.copy(
                            isLoadingOtherData = data.isLoading
                        )
                    }
                }
                is Resource.Success -> {
                    _state.update {
                        val limitedChangeDecimals = String.format(
                            "%.5f",
                            data.data?.rates?.get(_state.value.secondCurrency)?.change ?: 0.0
                        )
                        val formattedChange = NumberFormat.getNumberInstance()
                            .format(limitedChangeDecimals.toDouble())

                        val limitedChangePctDecimals = String.format(
                            "%.1f",
                            data.data?.rates?.get(_state.value.secondCurrency)?.changePct ?: 0.0
                        )
                        val formattedChangePct = NumberFormat.getNumberInstance()
                            .format(limitedChangePctDecimals.toDouble())

                        it.copy(
                            change = formattedChange.toDouble(),
                            changePct = formattedChangePct.toDouble()
                        )
                    }
                }
                is Resource.Error -> {
                    when (data.error) {
                        is ConnectException -> {
                            _event.emit(
                                UiEvent.ShowErrorSnackBar(message = "Timeout! Try again later.")
                            )
                        }
                        is UnknownHostException -> {
                            _event.emit(
                                UiEvent.ShowErrorSnackBar(message = "Connection lost, check your internet connection.")
                            )
                        }
                        is Exception -> {
                            _event.emit(
                                UiEvent.ShowErrorSnackBar(message = "Something went wrong!")
                            )
                        }
                    }
                }
            }
        }

    }

    private suspend fun updateChartData() {

        useCases.timeSeriesCurrency.invoke(
            startDate = _state.value.startDate.toString(),
            endDate = _state.value.endDate.toString(),
            from = _state.value.mainCurrency,
            to = _state.value.secondCurrency
        ).collect { data ->
            when (data) {
                is Resource.Success -> {
                    Log.i("TAG", "updateChartData: ${data.data?.rates}")
                    _state.update {
                        it.copy(
                            chartData = data.data
                        )
                    }
                }
                is Resource.Error -> {
                    when (data.error) {
                        is ConnectException -> {
                            _event.emit(
                                UiEvent.ShowErrorSnackBar(message = "Timeout! Try again later.")
                            )
                        }
                        is UnknownHostException -> {
                            _event.emit(
                                UiEvent.ShowErrorSnackBar(message = "Connection lost, check your internet connection.")
                            )
                        }
                        is Exception -> {
                            _event.emit(
                                UiEvent.ShowErrorSnackBar(message = "Something went wrong!")
                            )
                        }
                    }
                }
                is Resource.Loading -> {
                    _state.update {
                        it.copy(
                            isLoadingChartData = data.isLoading
                        )
                    }
                }
            }
        }

    }

    private suspend fun updateConversionData() {
        useCases.convertCurrency.invoke(
            amount = "1",
            from = _state.value.mainCurrency,
            to = _state.value.secondCurrency,
            date = LocalDate.now().toString()
        ).collect { data ->
            when (data) {
                is Resource.Success -> {

                    val limitedChangeDecimals = String.format(
                        "%.4f",
                        data.data?.result ?: 0.0
                    )
                    val formattedChange = NumberFormat.getNumberInstance()
                        .format(limitedChangeDecimals.toDouble())

                    _state.update {
                        it.copy(
                            mainToSecondCurrency = formattedChange
                        )
                    }
                }
                is Resource.Loading -> {
                    _state.update {
                        it.copy(
                            isLoadingConversionData = data.isLoading
                        )
                    }
                }
                is Resource.Error -> {
                    when (data.error) {
                        is ConnectException -> {
                            _event.emit(
                                UiEvent.ShowErrorSnackBar(message = "Timeout! Try again later.")
                            )
                        }
                        is UnknownHostException -> {
                            _event.emit(
                                UiEvent.ShowErrorSnackBar(message = "Connection lost, check your internet connection.")
                            )
                        }
                        is Exception -> {
                            _event.emit(
                                UiEvent.ShowErrorSnackBar(message = "Something went wrong!")
                            )
                        }
                    }
                }
            }
        }
    }

    private fun initHighAndLowStats() {

        val highPrice = _state.value.chartData?.rates?.values
            ?.flatMap { it.values }
            ?.maxByOrNull { it }

        val lowPrice = _state.value.chartData?.rates?.values
            ?.flatMap { it.values }
            ?.minByOrNull { it }

        _state.update {
            it.copy(
                highPrice = highPrice?.toDouble() ?: 0.0,
                lowPrice = lowPrice?.toDouble() ?: 0.0,
                isLoadingStatsData = false
            )
        }

    }

    private fun initChartEntryModel() {

        val chartEntryModelProducer =
            _state.value.chartData?.rates?.toList()
                ?.filterIndexed { index, pair ->
                    if (_state.value.chartFilter == Constants.CHART_MONTHLY) {
                        (index) % 6 == 0
                    } else {
                        true
                    }
                }
                ?.mapIndexed { index, (dateString, y) ->
                    Entry(
                        localDate = LocalDate.parse(dateString),
                        prices = y[_state.value.secondCurrency]?.toString() ?: "0",
                        x = index.toFloat(),
                        y = y[_state.value.secondCurrency] ?: 0f
                    )
                }.let { ChartEntryModelProducer(it ?: emptyList()) }

        _state.update {
            it.copy(
                chartEntryModelProducer = chartEntryModelProducer
            )
        }

    }

}











