package com.finoldigital.ygolp.presentation

import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.material.Text
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.finoldigital.ygolp.R
import com.finoldigital.ygolp.presentation.theme.WearAppTheme
import com.google.android.horologist.compose.ambient.AmbientAware
import java.util.Random

const val LIFE_POINTS_KEY = "LIFE_POINTS_KEY"
const val EXTRA_LIFE_POINTS = "com.finoldigital.ygolp.EXTRA_LIFE_POINTS"
const val STARTING_LIFE_POINTS = 8000

class MainActivity : ComponentActivity() {

    private var lifePoints by mutableIntStateOf(0)
    private var displayedLifePoints by mutableIntStateOf(0)

    private var duelStartMP: MediaPlayer? = null
    private var lifePointsChangeMP: MediaPlayer? = null
    private var itsTimeToDuelMP: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restore state now instead of onRestoreInstanceState
        if (savedInstanceState == null)
            restart()
        else
            changeLifePoints(savedInstanceState.getInt(LIFE_POINTS_KEY))

        setContent {
            WearApp()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.run {
            putInt(LIFE_POINTS_KEY, lifePoints)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (event.repeatCount == 0) {
            when (keyCode) {
                KeyEvent.KEYCODE_STEM_1 -> {
                    restart()
                    true
                }

                KeyEvent.KEYCODE_STEM_2 -> {
                    startItsTimeToDuel()
                    true
                }

                else -> {
                    super.onKeyDown(keyCode, event)
                }
            }
        } else {
            super.onKeyDown(keyCode, event)
        }
    }

    fun restart() {
        lifePoints = 0
        displayedLifePoints = 0
        if (duelStartMP == null) {
            duelStartMP = MediaPlayer.create(this, R.raw.duel_start)
            duelStartMP?.setOnCompletionListener {
                stopDuelStart()
                changeLifePoints(STARTING_LIFE_POINTS)
            }
        }
        duelStartMP?.start()
    }

    fun changeLifePoints(lp: Int) {
        if (lifePoints != lp) {
            lifePoints = lp
            if (lifePointsChangeMP == null) {
                lifePointsChangeMP = MediaPlayer.create(this, R.raw.lifepoints_change)
                lifePointsChangeMP?.setOnCompletionListener {
                    stopLifePointsChange()
                }
            }
            lifePointsChangeMP?.start()
            object : CountDownTimer(2100, 50) {
                override fun onTick(millisUntilFinished: Long) {
                    val min = 1000
                    val max = 9999
                    val tick = Random().nextInt(max - min + 1) + min
                    displayedLifePoints = tick
                }

                override fun onFinish() {
                    displayedLifePoints = lifePoints
                }
            }.start()
        }
        displayedLifePoints = lifePoints
    }

    private fun startItsTimeToDuel() {
        if (itsTimeToDuelMP == null) {
            itsTimeToDuelMP = MediaPlayer.create(this, R.raw.its_time_to_duel)
            itsTimeToDuelMP?.setOnCompletionListener {
                stopItsTimeToDuel()
            }
        }
        itsTimeToDuelMP?.start()
    }

    override fun onStop() {
        stopDuelStart()
        stopLifePointsChange()
        stopItsTimeToDuel()
        super.onStop()
    }

    private fun stopDuelStart() {
        if (duelStartMP != null) {
            duelStartMP?.release()
            duelStartMP = null
        }
    }

    private fun stopLifePointsChange() {
        if (lifePointsChangeMP != null) {
            lifePointsChangeMP?.release()
            lifePointsChangeMP = null
        }
    }

    private fun stopItsTimeToDuel() {
        if (itsTimeToDuelMP != null) {
            itsTimeToDuelMP?.release()
            itsTimeToDuelMP = null
        }
    }

    @Composable
    fun WearApp() {
        val navController = rememberSwipeDismissableNavController()

        WearAppTheme {
            AmbientAware { ambientStateUpdate ->
                SwipeDismissableNavHost(
                    navController = navController,
                    startDestination = "lifepoints"
                ) {
                    composable("lifepoints") {
                        LifePointsScreen(
                            displayedLifePoints
                        ) { mode -> navController.navigate("calculator/$mode") }
                    }
                    composable(
                        "calculator/{initialCalculatorMode}",
                        arguments = listOf(navArgument("initialCalculatorMode") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val initialCalculatorMode = backStackEntry.arguments?.getInt("initialCalculatorMode") ?: 1
                        CalculatorScreen(
                            lifePoints, initialCalculatorMode,
                            { result ->
                                changeLifePoints(result)
                                navController.popBackStack()
                            }, { navController.popBackStack() })
                    }
                }
            }
        }
    }

    @Composable
    fun LifePointsScreen(displayedLifePoints: Int, onShowCalculatorWithMode: (Int) -> Unit) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painterResource(R.drawable.lifepoints_background),
                contentDescription = "",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )
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
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = Color(0xFFFBFF0C.toInt()),
                fontFamily = FontFamily(Font(R.font.nationalyze_alp)),
                fontSize = 32.sp,
                text = displayedLifePoints.toString()
            )
        }
    }

    @WearPreviewDevices
    @WearPreviewFontScales
    @Composable
    fun LifePointsScreenPreview() {
        LifePointsScreen(STARTING_LIFE_POINTS) {}
    }
}
