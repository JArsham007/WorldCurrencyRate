package com.example.worldcurrencyrate.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.worldcurrencyrate.navigation.BottomNavScreens
import com.example.worldcurrencyrate.ui.theme.*

@Composable
fun BottomNavigationBar(
    navController: NavHostController
) {

    val items = listOf(
        BottomNavScreens.ConvertScreen,
        BottomNavScreens.ChartsScreen,
        BottomNavScreens.TrackScreen,
        BottomNavScreens.MoreScreen
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Row(
        modifier = Modifier
            .background(color = BackgroundColor)
            .fillMaxWidth()
            .padding(LARGE_PADDING),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        items.forEach { item ->
            BottomNavItem(
                icon = item.icon,
                title = item.title,
                isSelected = currentRoute == item.rout,
                onClick = {
                    navController.navigate(item.rout) {
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }

}

@Composable
fun BottomNavItem(
    modifier: Modifier = Modifier,
    icon: Int,
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .size(68.dp)
            .clip(RoundedCornerShape(MEDIUM_PADDING))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = false
                )
            ) {
                onClick()
            },
        shape = RoundedCornerShape(MEDIUM_PADDING),
        backgroundColor = if (isSelected) ButtonsColor else UnSelectedNavigationButtonColor,
    ) {
        Box(
            modifier = modifier
                .size(68.dp)
                .background(
                    color = if (isSelected) ButtonsColor else UnSelectedNavigationButtonColor,
                    shape = RoundedCornerShape(MEDIUM_PADDING)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .background(
                        color = Color.Transparent,
                        shape = RoundedCornerShape(MEDIUM_PADDING)
                    ),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    modifier = Modifier.size(26.dp),
                    painter = painterResource(id = icon),
                    contentDescription = "Item",
                    tint = if (isSelected) Color.White else UnSelectedNavIconColor
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = title,
                    color = TextColor,
                    fontSize = 12.sp
                )
            }
        }
    }

}
