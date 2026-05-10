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
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.finoldigital.ygolp.R
import com.finoldigital.ygolp.presentation.components.PlayerIndicator
import com.finoldigital.ygolp.presentation.enums.CalculatorMode
import com.finoldigital.ygolp.presentation.enums.Player
import com.finoldigital.ygolp.presentation.theme.AppColors

const val STARTING_LIFE_POINTS = 8000

@Composable
fun LifePointsScreen(
    player: Player = Player.ONE,
    lifePoints: Int = STARTING_LIFE_POINTS,
    isMuted: Boolean = false,
    onToggleMute: () -> Unit = {},
    onShowCalculatorWithMode: (CalculatorMode) -> Unit = {},
    onSwipePlayer: () -> Unit = {},
    onRestart: (() -> Unit)? = null,
) {
    val isLost = lifePoints <= MIN_LIFE_POINTS && onRestart != null
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    var swipeHandled = false
                    detectHorizontalDragGestures(
                        onDragStart = { swipeHandled = false },
                        onDragEnd = { swipeHandled = false },
                        onDragCancel = { swipeHandled = false },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            if (!swipeHandled) {
                                if ((player == Player.ONE && dragAmount < 0)
                                    || (player == Player.TWO && dragAmount > 0)
                                ) {
                                    swipeHandled = true
                                    onSwipePlayer()
                                }
                            }
                        }
                    )
                }
                .then(
                    if (isLost) {
                        Modifier.clickable { onRestart() }
                    } else {
                        Modifier
                    }
                )
        ) {
            if (player == Player.ONE) {
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
                                    AppColors.P2GradientStart,
                                    AppColors.P2GradientEnd
                                )
                            )
                        )
                )
            }

            if (!isLost) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .clickable { onShowCalculatorWithMode(CalculatorMode.ADD) }
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
                            .clickable { onShowCalculatorWithMode(CalculatorMode.SUBTRACT) }
                    )
                }
            }
            LifePointsText(lifePoints)

            PlayerIndicator(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
                player = player
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
                    tint = AppColors.MuteIconTint,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
fun LifePointsText(lifePoints: Int) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val lifePointsText =
            if (lifePoints > MIN_LIFE_POINTS) lifePoints.toString() else stringResource(R.string.game_lost)
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = AppColors.LpTextYellow,
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
    LifePointsScreen(Player.TWO)
}