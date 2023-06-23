package com.example.worldcurrencyrate.presentation.utils

import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.values.ChartValues
import java.text.NumberFormat

class ChartYAxisValueFormatter : AxisValueFormatter<AxisPosition.Vertical.Start> {

    override fun formatValue(value: Float, chartValues: ChartValues): CharSequence {

        val limitedChangeDecimals = String.format(
            "%.2f",
            value
        )
        val formattedChange = NumberFormat.getNumberInstance()
            .format(limitedChangeDecimals.toFloat())

        return formattedChange

    }

}