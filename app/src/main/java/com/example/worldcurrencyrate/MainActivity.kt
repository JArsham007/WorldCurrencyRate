package com.example.worldcurrencyrate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.worldcurrencyrate.presentation.components.BottomNavigationBar
import com.example.worldcurrencyrate.navigation.Navigation
import com.example.worldcurrencyrate.ui.theme.WorldCurrencyRateTheme
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalFoundationApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
savedInstanceState
        setContent {
            WorldCurrencyRateTheme {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { BottomNavigationBar(navController = navController) }
                ) {
                    Navigation(
                        modifier = Modifier.padding(it),
                        navController = navController
                    )
                }
            }
        }

    }

}
