package com.example.worldcurrencyrate.domain.model

data class TrackModel(
    val base: String,
    val timeStamp: Long,
    val latestRate: Map<String, Double>,
    val fluctuationRate: Map<String, Pair<Double, Double>>
)