package com.example.worldcurrencyrate.presentation.components

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.glance.*
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.worldcurrencyrate.presentation.utils.getCurrencyIcon
import com.example.worldcurrencyrate.ui.theme.*
import java.text.NumberFormat

@Composable
fun WidgetListItem(
    modifier: GlanceModifier = GlanceModifier,
    symbolName: String,
    mainText: String,
    secondaryTextColor: Color,
    secondaryText: String,
) {

    val context = LocalContext.current
    val currencyIcon = symbolName.trim().lowercase().getCurrencyIcon(context)

    Log.i("TAG", "WidgetListItem: $currencyIcon")

    val formattedResult = NumberFormat.getNumberInstance().format(mainText.toDouble())

    Row(
        modifier = modifier
            .background(color = BackgroundColor),
        verticalAlignment = Alignment.Vertical.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = GlanceModifier
                    .size(MAIN_CURRENCY_WIDGET_ICON_SIZE)
                    .padding(EXTRA_SMALL_PADDING),
                provider = ImageProvider(currencyIcon),
                contentDescription = "Currency",
            )
            Spacer(modifier = GlanceModifier.width(EXTRA_SMALL_PADDING))
            Text(
                text = symbolName,
                style = TextStyle(
                    fontSize = WIDGET_MAIN_CURRENCY_TEXT_SIZE,
                    fontWeight = FontWeight.Medium,
                    color = ColorProvider(TextColor)
                )
            )
        }
        Spacer(modifier = GlanceModifier.width(EXTRA_SMALL_PADDING))
        Column(
            horizontalAlignment = Alignment.Horizontal.End,
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            Text(
                modifier = GlanceModifier.width(CURRENCY_LIST_ITEM_MAIN_TEXT_WIDTH),
                text = formattedResult,
                style = TextStyle(
                    fontSize = WIDGET_MAIN_CURRENCY_TEXT_SIZE,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.End,
                    color = ColorProvider(TextColor)
                ),
                maxLines = 1,
            )
            Spacer(modifier = GlanceModifier.height(EXTRA_SMALL_PADDING))
            Text(
                modifier = GlanceModifier.width(CURRENCY_LIST_ITEM_MAIN_TEXT_WIDTH),
                text = secondaryText,
                style = TextStyle(
                    fontSize = SECONDARY_WIDGET_TEXT_SIZE,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.End,
                    color = ColorProvider(secondaryTextColor)
                ),
                maxLines = 1,
            )
        }
    }

}