package com.example.worldcurrencyrate.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class FluctuationModel(
    val success: Boolean,
    val base: String,
    @SerialName("end_date") val endDate: String,
    @SerialName("start_date") val startDate: String,
    val rates: Map<String, FluctuationRatesModel>
)