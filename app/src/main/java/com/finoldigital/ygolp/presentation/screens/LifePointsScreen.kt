package com.finoldigital.ygolp.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.finoldigital.ygolp.R
import com.finoldigital.ygolp.presentation.components.LifePointsPage
import com.finoldigital.ygolp.presentation.components.PlayerIndicator
import com.finoldigital.ygolp.presentation.constants.STARTING_LIFE_POINTS
import com.finoldigital.ygolp.presentation.enums.CalculatorMode
import com.finoldigital.ygolp.presentation.enums.Player
import com.finoldigital.ygolp.presentation.theme.AppColors

@Composable
fun LifePointsScreen(
    pagerState: PagerState,
    displayedLifePoints1: Int = STARTING_LIFE_POINTS,
    displayedLifePoints2: Int = STARTING_LIFE_POINTS,
    isMuted: Boolean = false,
    onToggleMute: () -> Unit = {},
    onShowCalculatorWithMode: (Player, CalculatorMode) -> Unit = { _, _ -> },
    onRestart: () -> Unit = {},
) {
    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(state = pagerState) { page ->
            val player = if (page == 0) Player.ONE else Player.TWO
            val displayedLifePoints = if (player == Player.ONE) displayedLifePoints1 else displayedLifePoints2
            LifePointsPage(
                player = player,
                lifePoints = displayedLifePoints,
                onShowCalculatorWithMode = { calculatorMode ->
                    onShowCalculatorWithMode(player, calculatorMode)
                },
                onRestart = if (displayedLifePoints <= 0) onRestart else null
            )
        }

        PlayerIndicator(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            player = if (pagerState.currentPage == 0) Player.ONE else Player.TWO
        )

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

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun LifePointsScreenPreview() {
    MaterialTheme {
        val pagerState = rememberPagerState(pageCount = { 2 })
        LifePointsScreen(pagerState)
    }
}