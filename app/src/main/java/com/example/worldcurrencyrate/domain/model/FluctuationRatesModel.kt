package com.example.worldcurrencyrate.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class FluctuationRatesModel(
    val change: Double,
    @SerialName("change_pct") val changePct: Double,
    @SerialName("end_rate") val endRate: Double,
    @SerialName("start_rate") val startRate: Double
)