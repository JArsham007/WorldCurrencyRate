package com.example.worldcurrencyrate.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.worldcurrencyrate.navigation.BottomNavScreens
import com.example.worldcurrencyrate.presentation.screens.chart_screen.ChartScreen
import com.example.worldcurrencyrate.presentation.screens.convert_screen.ConvertScreen
import com.example.worldcurrencyrate.presentation.screens.track_screen.TrackScreen
import com.example.worldcurrencyrate.utils.Constants

@ExperimentalFoundationApi
@Composable
fun Navigation(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = BottomNavScreens.TrackScreen.rout
    ) {
        composable(
            route = BottomNavScreens.ConvertScreen.rout
        ) {
            ConvertScreen(navController = navController)
        }
        composable(
            route = BottomNavScreens.ChartsScreen.rout,
            arguments = listOf(
                navArgument(name = Constants.CHART_SCREEN_MAIN_CURRENCY_ARG) {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument(name = Constants.CHART_SCREEN_SUB_CURRENCY_ARG) {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) {
            ChartScreen(navController = navController)
        }
        composable(
            route = BottomNavScreens.TrackScreen.rout
        ) {
            TrackScreen(navController = navController)
        }
        composable(
            route = BottomNavScreens.MoreScreen.rout
        ) {

        }
    }

}