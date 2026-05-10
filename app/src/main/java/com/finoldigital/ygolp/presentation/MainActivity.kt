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
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.finoldigital.ygolp.presentation.components.PLAYER_1
import com.finoldigital.ygolp.presentation.components.PLAYER_2
import com.finoldigital.ygolp.presentation.screens.CalculatorMode
import com.finoldigital.ygolp.presentation.screens.CalculatorScreen
import com.finoldigital.ygolp.presentation.screens.LifePointsScreen
import com.finoldigital.ygolp.presentation.screens.Screen
import com.finoldigital.ygolp.presentation.util.SoundManager
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.layout.ScreenScaffold

class MainActivity : ComponentActivity() {

    private lateinit var soundManager: SoundManager
    private lateinit var viewModel: MainViewModel
    private var navControllerInstance: androidx.navigation.NavHostController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        soundManager = SoundManager(this)
        lifecycle.addObserver(soundManager)

        viewModel = ViewModelProvider(
            this,
            MainViewModel.Factory(application, soundManager)
        )[MainViewModel::class.java]

        setContent {
            MaterialTheme {
                AppScaffold {
                    val navController = rememberSwipeDismissableNavController()
                    navControllerInstance = navController
                    WearApp(viewModel, navController)
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        // Check if we are on the Calculator screen
        val currentRoute = navControllerInstance?.currentDestination?.route
        val isCalculator = currentRoute?.startsWith("calculator") == true

        return if (event.repeatCount == 0) {
            when (keyCode) {
                KeyEvent.KEYCODE_STEM_1 -> {
                    if (!isCalculator) {
                        viewModel.restart()
                        true
                    } else {
                        false // Let the CalculatorScreen handle it
                    }
                }

                KeyEvent.KEYCODE_STEM_2 -> {
                    if (!isCalculator) {
                        viewModel.startItsTimeToDuel()
                        true
                    } else {
                        false // Let the CalculatorScreen handle it
                    }
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
fun WearApp(viewModel: MainViewModel, navController: androidx.navigation.NavHostController) {

    val displayedLifePoints by viewModel.displayedLifePoints.collectAsState()
    val displayedLifePoints2 by viewModel.displayedLifePoints2.collectAsState()
    val lifePoints by viewModel.lifePoints.collectAsState()
    val lifePoints2 by viewModel.lifePoints2.collectAsState()
    val isMuted by viewModel.isMuted.collectAsState()

    SwipeDismissableNavHost(
        navController = navController,
        startDestination = Screen.LifePoints.createRoute(PLAYER_1)
    ) {
        composable(
            Screen.LifePoints.route,
            arguments = listOf(
                navArgument("player") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val player = backStackEntry.arguments?.getInt("player") ?: PLAYER_1
            ScreenScaffold {
                if (player == 1) {
                    LifePointsScreen(
                        lifePoints = displayedLifePoints,
                        onShowCalculatorWithMode = { mode ->
                            navController.navigate(Screen.Calculator.createRoute(PLAYER_1, mode))
                        },
                        onSwipePlayer = {
                            navController.navigate(Screen.LifePoints.createRoute(PLAYER_2))
                        },
                        playerId = player,
                        onRestart = if (displayedLifePoints <= 0) ({ viewModel.start() }) else null,
                        isMuted = isMuted,
                        onToggleMute = { viewModel.toggleMute() }
                    )
                } else {
                    LifePointsScreen(
                        lifePoints = displayedLifePoints2,
                        onShowCalculatorWithMode = { mode ->
                            navController.navigate(Screen.Calculator.createRoute(PLAYER_2, mode))
                        },
                        onSwipePlayer = { navController.popBackStack() },
                        playerId = player,
                        onRestart = if (displayedLifePoints2 <= 0) ({ viewModel.start() }) else null,
                        isMuted = isMuted,
                        onToggleMute = { viewModel.toggleMute() }
                    )
                }
            }
        }
        composable(
            Screen.Calculator.route,
            arguments = listOf(
                navArgument("player") { type = NavType.IntType },
                navArgument("initialCalculatorMode") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val player = backStackEntry.arguments?.getInt("player") ?: PLAYER_1
            val initialCalculatorMode = CalculatorMode.fromInt(
                backStackEntry.arguments?.getInt("initialCalculatorMode") ?: CalculatorMode.SUBTRACT.value
            )
            val currentLifePoints = if (player == PLAYER_1) lifePoints else lifePoints2
            ScreenScaffold {
                CalculatorScreen(
                    playerId = player,
                    initialCalculatorMode,
                    currentLifePoints,
                    { navController.popBackStack() },
                    { result ->
                        viewModel.changeLifePoints(result, player)
                        navController.popBackStack()
                    },
                )
            }
        }
    }
}
