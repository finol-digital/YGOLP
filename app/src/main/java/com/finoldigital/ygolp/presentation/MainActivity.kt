package com.finoldigital.ygolp.presentation

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.google.android.horologist.compose.ambient.AmbientAware

class MainActivity : ComponentActivity() {

    private lateinit var soundManager: SoundManager
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        soundManager = SoundManager(this)
        lifecycle.addObserver(soundManager)

        viewModel = ViewModelProvider(
            this,
            MainViewModel.Factory(applicationContext, soundManager)
        )[MainViewModel::class.java]

        setContent {
            WearApp(viewModel)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (event.repeatCount == 0) {
            when (keyCode) {
                KeyEvent.KEYCODE_STEM_1 -> {
                    viewModel.restart()
                    true
                }

                KeyEvent.KEYCODE_STEM_2 -> {
                    viewModel.startItsTimeToDuel()
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
}

@Composable
fun WearApp(viewModel: MainViewModel) {
    val navController = rememberSwipeDismissableNavController()

    val displayedLifePoints by viewModel.displayedLifePoints.collectAsState()
    val displayedLifePoints2 by viewModel.displayedLifePoints2.collectAsState()
    val lifePoints by viewModel.lifePoints.collectAsState()
    val lifePoints2 by viewModel.lifePoints2.collectAsState()
    val isMuted by viewModel.isMuted.collectAsState()

    AmbientAware { _ ->
        SwipeDismissableNavHost(
            navController = navController,
            startDestination = "lifepoints/1"
        ) {
            composable(
                "lifepoints/{player}",
                arguments = listOf(
                    navArgument("player") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val player = backStackEntry.arguments?.getInt("player") ?: 1
                if (player == 1) {
                    LifePointsScreen(
                        displayedLifePoints = displayedLifePoints,
                        onShowCalculatorWithMode = { mode -> navController.navigate("calculator/1/$mode") },
                        onSwipePlayer = { navController.navigate("lifepoints/2") },
                        playerId = player,
                        onRestart = if (displayedLifePoints <= 0) ({ viewModel.start() }) else null,
                        isMuted = isMuted,
                        onToggleMute = { viewModel.toggleMute() }
                    )
                } else {
                    LifePointsScreen(
                        displayedLifePoints = displayedLifePoints2,
                        onShowCalculatorWithMode = { mode -> navController.navigate("calculator/2/$mode") },
                        onSwipePlayer = { navController.popBackStack() },
                        playerId = player,
                        onRestart = if (displayedLifePoints2 <= 0) ({ viewModel.start() }) else null,
                        isMuted = isMuted,
                        onToggleMute = { viewModel.toggleMute() }
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
                        viewModel.changeLifePoints(result, player)
                        navController.popBackStack()
                    }, { navController.popBackStack() },
                    playerId = player
                )
            }
        }
    }
}
