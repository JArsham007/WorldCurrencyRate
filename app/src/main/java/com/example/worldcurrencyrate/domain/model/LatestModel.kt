package com.example.worldcurrencyrate.domain.model

@kotlinx.serialization.Serializable
data class LatestModel(
    val success: Boolean,
    val base: String,
    val date: String,
    val rates: Map<String, Double>,
    val timestamp: Long
)
