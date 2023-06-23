package com.example.worldcurrencyrate.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.worldcurrencyrate.presentation.utils.getCurrencyIcon
import com.example.worldcurrencyrate.ui.theme.*
import com.example.worldcurrencyrate.utils.Symbols

@Composable
fun SymbolsDialog(
    onDismissMenu: () -> Unit,
    symbols: Symbols,
    onSelectedItem: (String) -> Unit
) {

    val context = LocalContext.current

    Dialog(
        onDismissRequest = { onDismissMenu() }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = UnSelectedNavigationButtonColor),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(symbols.toMap().size) { i ->
                val item = symbols.toMap().keys.toList()[i]
                Box(
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(bounded = true)
                        ) {
                            onSelectedItem(item)
                            onDismissMenu()
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .padding(SMALL_PADDING)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = item,
                            fontSize = TEXT_SYMBOLS_MENU_FONT_SIZE,
                            fontWeight = FontWeight.Medium,
                            color = TextColor
                        )
                        Spacer(modifier = Modifier.width(EXTRA_LARGE_PADDING))
                        Icon(
                            modifier = Modifier.size(EXTRA_LARGE_PADDING),
                            painter = painterResource(
                                id = item.lowercase().getCurrencyIcon(context)
                            ),
                            contentDescription = "Item",
                            tint = Color.Unspecified
                        )
                    }
                }
                if (item != symbols.toMap().keys.last()) {
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

}




