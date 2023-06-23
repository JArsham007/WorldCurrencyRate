package com.example.worldcurrencyrate.presentation.screens.track_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.worldcurrencyrate.domain.usecases.UseCases
import com.example.worldcurrencyrate.presentation.utils.UiEvent
import com.example.worldcurrencyrate.utils.Resource
import com.example.worldcurrencyrate.utils.Symbols
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.UnknownHostException
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TrackScreenViewModel @Inject constructor(
    private val useCases: UseCases,
    private val symbols: Symbols
) : ViewModel() {

    private val _state = MutableStateFlow(TrackScreenState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<UiEvent>()
    val event = _event.asSharedFlow()

    init {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    symbols = symbols
                )
            }
            updateSubCurrencies()
        }
    }

    fun onEvent(event: TrackScreenEvent) {

        when (event) {
            is TrackScreenEvent.OnMainCurrencyChanged -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            mainCurrencyName = event.newCurrency
                        )
                    }
                    updateSubCurrencies()
                }
            }
        }

    }

    private suspend fun updateSubCurrencies() {

        Log.i("TAG", "updateSubCurrencies: ${_state.value.symbols?.toStringWithSeparator()}")

        useCases.latestCurrency.invoke(
            from = _state.value.mainCurrencyName,
            to = _state.value.symbols?.toStringWithSeparator() ?: ""
        ).collect { data ->
            when (data) {
                is Resource.Success -> {
                    Log.i("TAG", "updateSubCurrenciesResult: ${data.data?.latestRate?.keys}")
                    _state.update {
                        it.copy(
                            subCurrenciesValue = data.data?.latestRate ?: emptyMap(),
                            subCurrenciesFluctuation = data.data?.fluctuationRate ?: emptyMap()
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
                            isLoadingCurrencies = data.isLoading
                        )
                    }
                }
            }
        }

    }

}