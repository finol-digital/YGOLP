package com.finoldigital.ygolp.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

@Composable
fun PlayerIndicator(modifier: Modifier = Modifier, playerId: Int) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val indicatorColor = Color.White
        val cornerRadius = CornerRadius(4.dp.value, 4.dp.value)
        val indicatorSize =
            DpSize(width = 12.dp, height = 6.dp) // Adjusted size, now uses framework DpSize
        val indicatorPadding = 4.dp // Padding between indicators


        // Player 1 Indicator
        Canvas(
            modifier = Modifier.size(
                width = indicatorSize.width,
                height = indicatorSize.height
            )
        ) {
            if (playerId == 1) {
                drawRoundRect(
                    color = indicatorColor,
                    cornerRadius = cornerRadius
                )
            } else {
                drawRoundRect(
                    color = indicatorColor,
                    cornerRadius = cornerRadius,
                    style = Stroke(width = 1.dp.toPx())
                )
            }
        }

        Spacer(modifier = Modifier.width(indicatorPadding))

        // Player 2 Indicator
        Canvas(
            modifier = Modifier.size(
                width = indicatorSize.width,
                height = indicatorSize.height
            )
        ) {
            if (playerId == 2) {
                drawRoundRect(
                    color = indicatorColor,
                    cornerRadius = cornerRadius
                )
            } else {
                drawRoundRect(
                    color = indicatorColor,
                    cornerRadius = cornerRadius,
                    style = Stroke(width = 1.dp.toPx())
                )
            }
        }
    }
}
