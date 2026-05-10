package com.finoldigital.ygolp.presentation

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.finoldigital.ygolp.presentation.enums.Player
import com.finoldigital.ygolp.presentation.enums.CalculatorMode
import com.finoldigital.ygolp.presentation.screens.CalculatorScreen
import com.finoldigital.ygolp.presentation.screens.LifePointsScreen
import com.finoldigital.ygolp.presentation.screens.Screen
import com.finoldigital.ygolp.presentation.util.SoundManager
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.layout.ScreenScaffold

class MainActivity : ComponentActivity() {

    private lateinit var soundManager: SoundManager
    private lateinit var viewModel: MainViewModel

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
                    WearApp(viewModel, navController)
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (event.repeatCount == 0 && viewModel.handleStemKey(keyCode)) {
            true
        } else {
            super.onKeyDown(keyCode, event)
        }
    }
}

@Composable
fun WearApp(viewModel: MainViewModel, navController: androidx.navigation.NavHostController) {

    val displayedLifePoints by viewModel.displayedLifePoints.collectAsState()
    val displayedLifePoints2 by viewModel.displayedLifePoints2.collectAsState()
    val lifePoints1 by viewModel.lifePoints.collectAsState()
    val lifePoints2 by viewModel.lifePoints2.collectAsState()
    val isMuted by viewModel.isMuted.collectAsState()

    SwipeDismissableNavHost(
        navController = navController,
        startDestination = Screen.LifePoints.createRoute(Player.ONE)
    ) {
        composable(
            Screen.LifePoints.route,
            arguments = listOf(
                navArgument("player") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val player = Player.fromInt(backStackEntry.arguments?.getInt("player") ?: 1)
            ScreenScaffold {
                if (player == Player.ONE) {
                    LifePointsScreen(
                        player,
                        lifePoints = displayedLifePoints,
                        isMuted = isMuted,
                        onToggleMute = { viewModel.toggleMute() },
                        onShowCalculatorWithMode = { calculatorMode ->
                            navController.navigate(Screen.Calculator.createRoute(Player.ONE, calculatorMode))
                        },
                        onSwipePlayer = {
                            navController.navigate(Screen.LifePoints.createRoute(Player.TWO)) {
                                launchSingleTop = true
                            }
                        },
                        onRestart = if (displayedLifePoints <= 0) ({ viewModel.start() }) else null
                    )
                } else {
                    LifePointsScreen(
                        player,
                        lifePoints = displayedLifePoints2,
                        isMuted = isMuted,
                        onToggleMute = { viewModel.toggleMute() },
                        onShowCalculatorWithMode = { calculatorMode ->
                            navController.navigate(Screen.Calculator.createRoute(Player.TWO, calculatorMode))
                        },
                        onSwipePlayer = { navController.popBackStack() },
                        onRestart = if (displayedLifePoints2 <= 0) ({ viewModel.start() }) else null
                    )
                }
            }
        }
        composable(
            Screen.Calculator.route,
            arguments = listOf(
                navArgument("player") { type = NavType.IntType },
                navArgument("calculatorMode") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val player = Player.fromInt(backStackEntry.arguments?.getInt("player") ?: 1)
            val lifePoints = if (player == Player.ONE) lifePoints1 else lifePoints2
            val calculatorMode = CalculatorMode.fromInt(
                backStackEntry.arguments?.getInt("calculatorMode") ?: CalculatorMode.SET.value
            )

            // Notify ViewModel we're on the calculator screen
            DisposableEffect(Unit) {
                viewModel.setOnCalculatorScreen(true)
                onDispose { viewModel.setOnCalculatorScreen(false) }
            }

            ScreenScaffold {
                CalculatorScreen(
                    player,
                    lifePoints,
                    calculatorMode,
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
