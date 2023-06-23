package com.example.worldcurrencyrate.domain.model

@kotlinx.serialization.Serializable
data class ConvertModel(
    val date: String,
    val result: Double,
    val success: Boolean,
    val info: ConvertInfoModel,
    val query: ConvertQueryModel
)