package com.example.worldcurrencyrate.domain.model

@kotlinx.serialization.Serializable
data class ConvertInfoModel (
    val rate: Double,
    val timestamp: Long
)