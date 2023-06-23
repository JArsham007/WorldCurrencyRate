package com.example.worldcurrencyrate.utils


sealed class Resource<T>(val data: T? = null, val error: Exception? = null) {

    class Success<T>(data: T?): Resource<T>(data)
    class Error<T>(error: Exception?, data: T? = null): Resource<T>(data, error)
    class Loading<T>(val isLoading: Boolean = true): Resource<T>(null)

}