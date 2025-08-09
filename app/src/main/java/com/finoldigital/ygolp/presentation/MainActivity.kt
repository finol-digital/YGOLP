package com.finoldigital.ygolp.presentation

import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.KeyEvent
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
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
const val LIFE_POINTS_2_KEY = "LIFE_POINTS_2_KEY"
const val EXTRA_LIFE_POINTS = "com.finoldigital.ygolp.EXTRA_LIFE_POINTS"
const val STARTING_LIFE_POINTS = 8000

class MainActivity : ComponentActivity() {

    private var lifePoints by mutableIntStateOf(0)
    private var displayedLifePoints by mutableIntStateOf(0)
    private var lifePoints2 by mutableIntStateOf(0)
    private var displayedLifePoints2 by mutableIntStateOf(0)

    private var duelStartMP: MediaPlayer? = null
    private var lifePointsChangeMP: MediaPlayer? = null
    private var itsTimeToDuelMP: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            start()
        } else {
            changeLifePoints(savedInstanceState.getInt(LIFE_POINTS_KEY), 1, false)
            lifePoints2 = savedInstanceState.getInt(LIFE_POINTS_2_KEY)
            displayedLifePoints2 = lifePoints2
        }

        setContent {
            WearApp()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.run {
            putInt(LIFE_POINTS_KEY, lifePoints)
            putInt(LIFE_POINTS_2_KEY, lifePoints2)
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

    fun start() {
        lifePoints2 = STARTING_LIFE_POINTS
        displayedLifePoints2 = STARTING_LIFE_POINTS
        if (itsTimeToDuelMP == null) {
            itsTimeToDuelMP = MediaPlayer.create(this, R.raw.its_time_to_duel)
            itsTimeToDuelMP?.setOnCompletionListener {
                itsTimeToDuelMP?.setOnCompletionListener {}
                stopItsTimeToDuel()
                restart()
            }
        }
        itsTimeToDuelMP?.start()
    }

    fun restart() {
        lifePoints = 0
        displayedLifePoints = 0
        if (duelStartMP == null) {
            duelStartMP = MediaPlayer.create(this, R.raw.duel_start)
            duelStartMP?.setOnCompletionListener {
                stopDuelStart()
                changeLifePoints(STARTING_LIFE_POINTS, 1)
            }
        }
        duelStartMP?.start()
    }

    fun changeLifePoints(lp: Int, player: Int, playSound: Boolean = true) {
        val currentLp = if (player == 1) lifePoints else lifePoints2
        if (currentLp != lp) {
            if (player == 1) {
                lifePoints = lp
            } else {
                lifePoints2 = lp
            }
            if (playSound) {
                if (lifePointsChangeMP == null) {
                    lifePointsChangeMP = MediaPlayer.create(this, R.raw.lifepoints_change)
                    lifePointsChangeMP?.setOnCompletionListener {
                        stopLifePointsChange()
                    }
                }
                lifePointsChangeMP?.start()
            }
            object : CountDownTimer(if (playSound) 2100 else 0, 50) {
                override fun onTick(millisUntilFinished: Long) {
                    val min = 1000
                    val max = 9999
                    val tick = Random().nextInt(max - min + 1) + min
                    if (player == 1) {
                        displayedLifePoints = tick
                    } else {
                        displayedLifePoints2 = tick
                    }
                }

                override fun onFinish() {
                    if (player == 1) {
                        displayedLifePoints = lifePoints
                    } else {
                        displayedLifePoints2 = lifePoints2
                    }
                }
            }.start()
        }
        if (player == 1) {
            displayedLifePoints = lifePoints
        } else {
            displayedLifePoints2 = lifePoints2
        }
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
            AmbientAware { _ ->
                SwipeDismissableNavHost(
                    navController = navController,
                    startDestination = "lifepoints/1" // Start with player 1
                ) {
                    composable("lifepoints/{player}") { backStackEntry ->
                        val player =
                            backStackEntry.arguments?.getString("player")?.toIntOrNull() ?: 1
                        if (player == 1) {
                            LifePointsScreen(
                                displayedLifePoints = displayedLifePoints,
                                onShowCalculatorWithMode = { mode -> navController.navigate("calculator/1/$mode") },
                                onNavigateToPlayer2 = { navController.navigate("lifepoints/2") }
                            )
                        } else {
                            LifePointsScreen2(
                                displayedLifePoints = displayedLifePoints2,
                                onShowCalculatorWithMode = { mode -> navController.navigate("calculator/2/$mode") },
                                onNavigateToPlayer1 = {
                                    navController.navigate("lifepoints/1") {
                                        popUpTo(
                                            "lifepoints/1"
                                        ) { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                    composable(
                        "calculator/{player}/{initialCalculatorMode}",
                        arguments = listOf(
                            navArgument("player") { type = NavType.IntType },
                            navArgument("initialCalculatorMode") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        val player = backStackEntry.arguments?.getInt("player") ?: 1
                        val initialCalculatorMode =
                            backStackEntry.arguments?.getInt("initialCalculatorMode") ?: 1
                        val currentLifePoints = if (player == 1) lifePoints else lifePoints2
                        CalculatorScreen(
                            currentLifePoints, initialCalculatorMode,
                            { result ->
                                changeLifePoints(result, player)
                                navController.popBackStack()
                            }, { navController.popBackStack() })
                    }
                }
            }
        }
    }

    @Composable
    fun LifePointsScreen(
        displayedLifePoints: Int,
        onShowCalculatorWithMode: (Int) -> Unit,
        onNavigateToPlayer2: () -> Unit
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { change, dragAmount ->
                        change.consume()
                        if (dragAmount < 0) { // Swipe left
                            onNavigateToPlayer2()
                        }
                    }
                }
        ) {
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
    fun LifePointsScreen2(
        displayedLifePoints: Int,
        onShowCalculatorWithMode: (Int) -> Unit,
        onNavigateToPlayer1: () -> Unit
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Blue)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { change, dragAmount ->
                        change.consume()
                        if (dragAmount > 0) { // Swipe right
                            onNavigateToPlayer1()
                        }
                    }
                }
        ) {
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
                if (displayedLifePoints > 0) displayedLifePoints.toString() else "YGOLP"
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
        LifePointsScreen(0, {}, {})
    }

    @WearPreviewDevices
    @WearPreviewFontScales
    @Composable
    fun LifePointsScreen2Preview() {
        LifePointsScreen2(STARTING_LIFE_POINTS, {}, {})
    }
}
