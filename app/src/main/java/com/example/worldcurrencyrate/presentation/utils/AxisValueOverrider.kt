package com.example.worldcurrencyrate.presentation.utils

import android.util.Log
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import java.text.NumberFormat

class AxisValueOverrider(
) : AxisValuesOverrider<ChartEntryModel> {

    override fun getMaxY(model: ChartEntryModel): Float? {

        val limitedChangeDecimals = String.format(
            "%.2f",
            model.maxY
        )

        return limitedChangeDecimals.toFloat()

    }

    override fun getMinY(model: ChartEntryModel): Float? {

        val limitedChangeDecimals = String.format(
            "%.2f",
            model.minY
        )

        return limitedChangeDecimals.toFloat()

    }

}