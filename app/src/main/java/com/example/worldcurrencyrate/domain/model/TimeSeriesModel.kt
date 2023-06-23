package com.example.worldcurrencyrate.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class TimeSeriesModel (
    val success: Boolean,
    val base: String,
    @SerialName("end_date") val endDate: String,
    @SerialName("start_date") val startDate: String,
    var rates: Map<String, Map<String, Float>>
)