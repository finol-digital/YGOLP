package com.finoldigital.ygolp.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.finoldigital.ygolp.R
import com.finoldigital.ygolp.presentation.constants.MIN_LIFE_POINTS
import com.finoldigital.ygolp.presentation.constants.STARTING_LIFE_POINTS
import com.finoldigital.ygolp.presentation.enums.CalculatorMode
import com.finoldigital.ygolp.presentation.enums.Player
import com.finoldigital.ygolp.presentation.theme.AppColors

@Composable
fun LifePointsPage(
    player: Player = Player.ONE,
    lifePoints: Int = STARTING_LIFE_POINTS,
    onShowCalculatorWithMode: (CalculatorMode) -> Unit = {},
    onRestart: (() -> Unit)? = null,
) {
    val isLost = lifePoints <= MIN_LIFE_POINTS && onRestart != null
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
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
fun LifePointsPagePreview() {
    LifePointsPage()
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun LifePointsPagePreview2() {
    LifePointsPage(Player.TWO)
}