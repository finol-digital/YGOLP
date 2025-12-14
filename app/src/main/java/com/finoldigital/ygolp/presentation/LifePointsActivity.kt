package com.finoldigital.ygolp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.finoldigital.ygolp.R

const val EXTRA_LIFE_POINTS = "com.finoldigital.ygolp.EXTRA_LIFE_POINTS"
const val STARTING_LIFE_POINTS = 8000

class LifePointsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val displayedLifePoints = intent.getIntExtra(EXTRA_LIFE_POINTS, STARTING_LIFE_POINTS)

        setContent {
            LifePointsScreen(
                displayedLifePoints = displayedLifePoints,
                onShowCalculatorWithMode = { /* do nothing */ },
                onSwipePlayer = { /* do nothing */ }
            )
        }
    }
}

@Composable
fun LifePointsScreen(
    displayedLifePoints: Int,
    onShowCalculatorWithMode: (Int) -> Unit,
    onSwipePlayer: () -> Unit,
    playerId: Int = 1,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent) // Changed to Transparent to see PlayerIndicator if background image is not full
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, dragAmount ->
                    change.consume()
                    if ((playerId == 1 && dragAmount < 0) // Player 1 swipe left
                        || (playerId == 2 && dragAmount > 0) // Player 2 swipe right
                    ) {
                        onSwipePlayer()
                    }
                }
            }
    ) {
        if (playerId == 1) {
            Image(
                painterResource(R.drawable.lifepoints_background),
                contentDescription = "Background for Player 1",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Blue,
                                Color.Black
                            )
                        )
                    )
            ) // Gradient background for P2
        }

        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clickable { onShowCalculatorWithMode(2) } // Decrease LP
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clickable { onShowCalculatorWithMode(0) } // Set LP
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clickable { onShowCalculatorWithMode(1) } // Increase LP
            )
        }
        LifePointsText(displayedLifePoints)

        PlayerIndicator(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp), // Adjust padding as needed
            playerId = playerId
        )
    }
}

@Composable
fun LifePointsText(displayedLifePoints: Int) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val lifePointsText =
            if (displayedLifePoints > 0) displayedLifePoints.toString() else stringResource(R.string.app_name)
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = Color(0xFFFBFF0C.toInt()),
            fontFamily = FontFamily(Font(R.font.nationalyze_alp)),
            fontSize = 32.sp,
            text = lifePointsText
        )
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun LifePointsScreenPreview() {
    LifePointsScreen(STARTING_LIFE_POINTS, {}, {}, playerId = 1)
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun LifePointsScreenPreview2() {
    LifePointsScreen(STARTING_LIFE_POINTS, {}, {}, playerId = 2)
}
