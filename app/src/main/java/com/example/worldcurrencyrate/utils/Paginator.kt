package com.example.worldcurrencyrate.utils

interface Paginator<Key, Item1, Item2> {
    suspend fun loadNextItem()
    fun reset()
}