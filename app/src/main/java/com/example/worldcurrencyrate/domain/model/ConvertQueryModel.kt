package com.example.worldcurrencyrate.domain.model

@kotlinx.serialization.Serializable
data class ConvertQueryModel (
    val amount: Double,
    val from: String,
    val to: String
)