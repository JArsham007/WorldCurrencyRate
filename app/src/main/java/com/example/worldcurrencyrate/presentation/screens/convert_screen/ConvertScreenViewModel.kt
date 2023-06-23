package com.example.worldcurrencyrate.presentation.screens.convert_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.worldcurrencyrate.domain.model.ConvertModel
import com.example.worldcurrencyrate.domain.usecases.UseCases
import com.example.worldcurrencyrate.presentation.utils.UiEvent
import com.example.worldcurrencyrate.utils.Resource
import com.example.worldcurrencyrate.utils.Symbols
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

@HiltViewModel
class ConvertScreenViewModel @Inject constructor(
    private val useCases: UseCases,
    private val symbols: Symbols
) : ViewModel() {

    private val _state = MutableStateFlow(ConvertScreenState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<UiEvent>()
    val event = _event.asSharedFlow()

    init {
        _state.update {
            it.copy(
                symbols = symbols
            )
        }
    }

    fun onEvent(event: ConvertScreenEvent) {

        when (event) {
            is ConvertScreenEvent.OnAmountChange -> {
                viewModelScope.launch {
                    updateAmount(event.amount)
                }
            }
            is ConvertScreenEvent.OnCurrencyRemoved -> {

                val subCurrencies = _state.value.subCurrencies.toMutableList()
                subCurrencies.removeAt(index = event.index)

                _state.update {
                    it.copy(
                        subCurrencies = subCurrencies.toList()
                    )
                }

            }
            is ConvertScreenEvent.OnDateChange -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            date = event.date
                        )
                    }
                    updateSubCurrencies()
                }
            }
            is ConvertScreenEvent.OnMainCurrencyChanged -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            mainCurrencyName = event.newCurrency
                        )
                    }
                    _state.value.subCurrencies.forEach { model ->
                        useCases.convertCurrency.invoke(
                            amount = _state.value.amount,
                            from = event.newCurrency,
                            to = model.query.to,
                            date = _state.value.date.toString()
                        )
                    }
                    updateSubCurrencies()
                }
            }
            is ConvertScreenEvent.OnSubCurrencyChanged -> {
                viewModelScope.launch {
                    changeSubCurrency(
                        amount = _state.value.amount,
                        from = _state.value.mainCurrencyName,
                        to = event.newCurrency,
                        date = _state.value.date.toString(),
                        index = event.index
                    )
                }
            }
            is ConvertScreenEvent.OnAddCurrency -> {
                viewModelScope.launch {
                    addNewSubCurrency(
                        amount = _state.value.amount,
                        from = _state.value.mainCurrencyName,
                        to = event.newCurrency,
                        date = _state.value.date.toString()
                    )
                }
            }
            ConvertScreenEvent.UpdateValues -> {
                viewModelScope.launch {
                    updateSubCurrencies()
                }
            }
        }

    }

    private fun updateAmount(amount: String) {
        _state.update {
            it.copy(
                amount = amount
            )
        }
    }

    private suspend fun addNewSubCurrency(
        amount: String,
        from: String,
        to: String,
        date: String
    ) {

        useCases.convertCurrency.invoke(
            amount = amount,
            from = from,
            to = to,
            date = date
        ).collect { data ->
            when (data) {
                is Resource.Success -> {
                    val subCurrencies = _state.value.subCurrencies.toMutableList()
                    subCurrencies.add(data.data!!)
                    _state.update {
                        it.copy(
                            subCurrencies = subCurrencies.toList()
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
                            Log.i("TAG", "addNewSubCurrency: ${data.error}")
                        }
                    }
                }
                is Resource.Loading -> {
                    _state.update {
                        it.copy(
                            isLoadingNewCurrency = data.isLoading
                        )
                    }
                }
            }
        }

    }

    private suspend fun updateSubCurrencies() {

        _state.value.subCurrencies.forEach { model ->
            useCases.convertCurrency.invoke(
                amount = _state.value.amount.ifEmpty { "1" },
                from = _state.value.mainCurrencyName,
                to = model.query.to,
                date = _state.value.date.toString()
            ).collect { data ->
                when (data) {
                    is Resource.Success -> {

                        val currencyMutableList = _state.value.subCurrencies.toMutableList()
                        currencyMutableList[currencyMutableList.indexOf(model)] = data.data!!

                        _state.update {
                            it.copy(
                                subCurrencies = currencyMutableList.toList()
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
                                Log.i("TAG", "addNewSubCurrency: ${data.error}")
                            }
                        }
                    }
                    is Resource.Loading -> {
                        _state.update {
                            it.copy(
                                isLoadingValues = data.isLoading
                            )
                        }
                    }
                }
            }
        }

    }

    private suspend fun changeSubCurrency(
        amount: String,
        from: String,
        to: String,
        date: String,
        index: Int
    ) {

        useCases.convertCurrency.invoke(
            amount = amount,
            from = from,
            to = to,
            date = date
        ).collect { data ->
            when (data) {
                is Resource.Success -> {
                    val subCurrencies = _state.value.subCurrencies.toMutableList()
                    subCurrencies[index] = data.data!!
                    _state.update {
                        it.copy(
                            subCurrencies = subCurrencies.toList()
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
                            Log.i("TAG", "addNewSubCurrency: ${data.error}")
                        }
                    }
                }
                is Resource.Loading -> {
                    _state.update {
                        it.copy(
                            isLoadingSubCurrencyName = true
                        )
                    }
                }
            }
        }

    }

}








