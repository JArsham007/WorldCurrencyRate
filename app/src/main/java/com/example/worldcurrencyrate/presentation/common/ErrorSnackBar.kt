package com.example.worldcurrencyrate.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.worldcurrencyrate.R
import com.example.worldcurrencyrate.ui.theme.*

@Composable
fun ErrorSnackBar(
    message: String
) {

    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.error)
    )

    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = true,
        speed = 1f,
        restartOnPlay = false
    )

    Snackbar(
        backgroundColor = Color.Transparent,
        elevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .padding(SMALL_PADDING)
                .fillMaxWidth()
                .background(SecondaryTextColor, shape = RoundedCornerShape(ADD_CURRENCY_BUTTON_CORNER_RADIUS)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LottieAnimation(
                modifier = Modifier.size(SNACK_BAR_ANIMATION_SIZE),
                composition = composition,
                progress = {
                    progress
                }
            )
            Text(
                modifier = Modifier.padding(end = EXTRA_SMALL_PADDING),
                text = message,
                color = TextColor
            )
        }
    }

}