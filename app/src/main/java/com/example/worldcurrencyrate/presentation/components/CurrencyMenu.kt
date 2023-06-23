package com.example.worldcurrencyrate.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.worldcurrencyrate.R
import com.example.worldcurrencyrate.ui.theme.*

@Composable
fun CurrencyMenu(
    modifier: Modifier = Modifier,
    onDismissMenu: () -> Unit,
    items: Map<String, Int>,
    showMenu: Boolean,
    onClick: (String) -> Unit
) {

    DropdownMenu(
        modifier = modifier
            .background(
                color = UnSelectedNavigationButtonColor,
                shape = RoundedCornerShape(MENU_CORNER_RADIUS)
            ),
        offset = DpOffset(x = (8).dp, y = (-45).dp),
        expanded = showMenu,
        onDismissRequest = { onDismissMenu() }
    ) {

        items.forEach {
            DropdownMenuItem(
                modifier = Modifier
                    .background(UnSelectedNavigationButtonColor)
                    .fillMaxWidth()
                    .padding(2.dp),
                onClick = {
                    onClick(it.key)
                    onDismissMenu()
                }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = it.key,
                        fontSize = TEXT_MENU_FONT_SIZE,
                        color = TextColor
                    )
                    Spacer(modifier = Modifier.width(EXTRA_LARGE_PADDING))
                    Icon(
                        modifier = Modifier.size(MENU_ICON_SIZE),
                        painter = painterResource(id = it.value),
                        contentDescription = "Item",
                        tint = TextColor
                    )
                }
            }
            if (it.key != items.keys.last()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(SubItemBackgroundColor),
                ) {}
            }
        }

    }

}





