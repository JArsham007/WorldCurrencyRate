package com.example.worldcurrencyrate.presentation.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.worldcurrencyrate.R
import com.example.worldcurrencyrate.presentation.utils.getCurrencyIcon
import com.example.worldcurrencyrate.ui.theme.*
import com.example.worldcurrencyrate.utils.Constants
import java.text.NumberFormat

@Composable
fun LatestRateListItem(
    modifier: Modifier = Modifier,
    symbolName: String,
    mainText: String,
    secondaryTextColor: Color,
    secondaryText: String,
    onMenuItemsClicked: (String) -> Unit
) {

    val context = LocalContext.current
    val currencyIcon = symbolName.lowercase().getCurrencyIcon(context)

    val formattedResult = NumberFormat.getNumberInstance().format(mainText.toDouble())

    Log.i("TAG", "LatestRateListItem: $symbolName")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = SubItemBackgroundColor, RoundedCornerShape(MEDIUM_PADDING)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .padding(SMALL_PADDING),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .size(COUNTRY_FLAG_ICON_SIZE)
                    .padding(EXTRA_SMALL_PADDING),
                painter = painterResource(id = currencyIcon),
                contentDescription = "Currency",
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(EXTRA_SMALL_PADDING))
            Text(
                text = symbolName,
                fontSize = CURRENCY_NAME_TEXT_SIZE,
                fontWeight = FontWeight.Medium,
                color = TextColor
            )
        }
        Spacer(modifier = Modifier.width(EXTRA_SMALL_PADDING))
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                modifier = Modifier.width(CURRENCY_LIST_ITEM_MAIN_TEXT_WIDTH),
                text = formattedResult,
                fontSize = CURRENCY_ITEM_MAIN_TEXT_FONT_SIZE,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(textDirection = TextDirection.Rtl),
                color = TextColor,
                maxLines = 1,
            )
            Spacer(modifier = Modifier.height(SMALL_PADDING))
            Text(
                modifier = Modifier.width(CURRENCY_LIST_ITEM_MAIN_TEXT_WIDTH),
                text = secondaryText,
                fontSize = SECONDARY_TEXT_SIZE,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.End,
                overflow = TextOverflow.Ellipsis,
                color = secondaryTextColor,
                maxLines = 1
            )
        }
        Spacer(modifier = Modifier.width(1.dp))
        var showMenu by remember {
            mutableStateOf(false)
        }
        Card(
            modifier = Modifier
                .padding(start = SMALL_PADDING)
                .width(60.dp)
                .height(90.dp)
                .background(
                    MenuButtonColor,
                    RoundedCornerShape(
                        topEnd = MEDIUM_PADDING,
                        bottomEnd = MEDIUM_PADDING
                    )
                )
                .clip(
                    RoundedCornerShape(
                        topEnd = MEDIUM_PADDING,
                        bottomEnd = MEDIUM_PADDING
                    )
                )
                .clickable {
                    showMenu = !showMenu
                },
            shape = RoundedCornerShape(MEDIUM_PADDING),
            backgroundColor = MainItemBackgroundColor,
        ) {
            Box(
                modifier = Modifier.background(
                    MenuButtonColor,
                    RoundedCornerShape(
                        topEnd = MEDIUM_PADDING,
                        bottomEnd = MEDIUM_PADDING
                    )
                ),
                contentAlignment = Alignment.Center
            ) {
                CurrencyMenu(
                    onDismissMenu = { showMenu = false },
                    items = mapOf(
                        Constants.CURRENCY_MENU_GO_TO_CHARTS to R.drawable.charts
                    ),
                    showMenu = showMenu,
                    onClick = {
                        onMenuItemsClicked(it)
                    }
                )

                Icon(
                    modifier = Modifier.size(CURRENCY_LIST_ITEM_MENU_ICON_SIZE),
                    painter = painterResource(id = R.drawable.menu),
                    contentDescription = "Menu",
                    tint = TextColor
                )
            }
        }
    }

}



