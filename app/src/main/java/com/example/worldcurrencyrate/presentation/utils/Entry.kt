package com.example.worldcurrencyrate.presentation.utils

import com.patrykandpatrick.vico.core.entry.ChartEntry
import java.time.LocalDate

class Entry(
    val localDate: LocalDate,
    val prices: String,
    override val x: Float,
    override val y: Float
) : ChartEntry {
    override fun withY(y: Float): ChartEntry {
        return Entry(localDate, prices, x, y)
    }
}