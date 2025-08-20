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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.finoldigital.ygolp.R

class LifePointsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val displayedLifePoints = intent.getIntExtra(EXTRA_LIFE_POINTS, STARTING_LIFE_POINTS)

        setContent {
            LifePointsScreen(
                displayedLifePoints = displayedLifePoints,
                onShowCalculatorWithMode = {},
                onNextPlayer = {}
            )
        }
    }
}

@Composable
fun LifePointsScreen(
    displayedLifePoints: Int,
    onShowCalculatorWithMode: (Int) -> Unit,
    onNextPlayer: () -> Unit,
    playerId: Int = 1,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Blue)
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, dragAmount ->
                    change.consume()
                    if (dragAmount < 0) { // Swipe left
                        onNextPlayer()
                    }
                }
            }
    ) {
        if (playerId == 1) {
            Image(
                painterResource(R.drawable.lifepoints_background),
                contentDescription = "",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )
        }
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clickable { onShowCalculatorWithMode(2) }
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clickable { onShowCalculatorWithMode(0) }
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clickable { onShowCalculatorWithMode(1) }
            )
        }
        LifePointsText(displayedLifePoints)
    }
}

@Composable
fun LifePointsText(displayedLifePoints: Int) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val lifePointsText =
            if (displayedLifePoints > 0) displayedLifePoints.toString() else R.string.app_name.toString()
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
    LifePointsScreen(STARTING_LIFE_POINTS, {}, {})
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun LifePointsScreenPreview2() {
    LifePointsScreen(STARTING_LIFE_POINTS, {}, {}, 2)
}