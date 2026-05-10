package com.finoldigital.ygolp.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
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
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.finoldigital.ygolp.R
import com.finoldigital.ygolp.presentation.components.PLAYER_1
import com.finoldigital.ygolp.presentation.components.PLAYER_2
import com.finoldigital.ygolp.presentation.components.PlayerIndicator

const val INITIAL_LIFE_POINTS = 8000
const val MAX_LIFE_POINTS = 99999

@Composable
fun LifePointsScreen(
    playerId: Int = PLAYER_1,
    displayedLifePoints: Int = INITIAL_LIFE_POINTS,
    isMuted: Boolean = false,
    onToggleMute: () -> Unit = {},
    onShowCalculatorWithMode: (CalculatorMode) -> Unit = {},
    onSwipePlayer: () -> Unit = {},
    onRestart: (() -> Unit)? = null,
) {
    val isLost = displayedLifePoints <= 0 && onRestart != null
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .pointerInput(Unit) {
                var swipeHandled = false
                detectHorizontalDragGestures(
                    onDragStart = { swipeHandled = false },
                    onDragEnd = { swipeHandled = false },
                    onDragCancel = { swipeHandled = false },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        if (!swipeHandled) {
                            if ((playerId == PLAYER_1 && dragAmount < 0) // Player 1 swipe left
                                || (playerId == PLAYER_2 && dragAmount > 0) // Player 2 swipe right
                            ) {
                                swipeHandled = true
                                onSwipePlayer()
                            }
                        }
                    }
                )
            }
            .then(if (isLost) Modifier.clickable { onRestart() } else Modifier)
    ) {
        if (playerId == PLAYER_1) {
            Image(
                painterResource(R.drawable.lifepoints_background),
                contentDescription = stringResource(R.string.bg_player_1),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
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

        if (!isLost) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clickable { onShowCalculatorWithMode(CalculatorMode.SUBTRACT) }
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clickable { onShowCalculatorWithMode(CalculatorMode.SET) }
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clickable { onShowCalculatorWithMode(CalculatorMode.ADD) }
                )
            }
        }
        LifePointsText(displayedLifePoints)

        PlayerIndicator(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            playerId = playerId
        )

        // Mute/Unmute toggle button at top center
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 4.dp)
                .size(48.dp)
                .clickable { onToggleMute() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                contentDescription = if (isMuted) stringResource(R.string.unmute) else stringResource(R.string.mute),
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun LifePointsText(displayedLifePoints: Int) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val lifePointsText =
            if (displayedLifePoints > 0) displayedLifePoints.toString() else stringResource(R.string.zero)
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = Color(0xFFFBFF0C),
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
    LifePointsScreen()
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun LifePointsScreenPreview2() {
    LifePointsScreen(PLAYER_2)
}