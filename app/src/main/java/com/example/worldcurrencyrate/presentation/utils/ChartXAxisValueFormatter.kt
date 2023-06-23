package com.example.worldcurrencyrate.presentation.utils

import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.values.ChartValues
import java.time.format.DateTimeFormatter

class ChartXAxisValueFormatter : AxisValueFormatter<AxisPosition.Horizontal.Bottom> {

    override fun formatValue(value: Float, chartValues: ChartValues): CharSequence {
        return (chartValues.chartEntryModel.entries
            .first()
            .getOrNull(value.toInt()) as? Entry)
            ?.localDate
            ?.run {
                DateTimeFormatter
                    .ofPattern("MM/dd")
                    .format(this)
            }
            .orEmpty()
    }

}