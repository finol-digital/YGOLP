package com.finoldigital.ygolp.presentation

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.pager.rememberPagerState
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
import com.finoldigital.ygolp.presentation.constants.Routes
import com.finoldigital.ygolp.presentation.util.SoundManager
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.layout.ScreenScaffold

class MainActivity : ComponentActivity() {

    private lateinit var soundManager: SoundManager
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        soundManager = SoundManager(applicationContext)

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

    val displayedLifePoints1 by viewModel.displayedLifePoints.collectAsState()
    val displayedLifePoints2 by viewModel.displayedLifePoints2.collectAsState()
    val lifePoints1 by viewModel.lifePoints.collectAsState()
    val lifePoints2 by viewModel.lifePoints2.collectAsState()
    val isMuted by viewModel.isMuted.collectAsState()

    SwipeDismissableNavHost(
        navController = navController,
        startDestination = Routes.LifePoints.route
    ) {
        composable(Routes.LifePoints.route) {
            val pagerState = rememberPagerState(pageCount = { 2 })
            ScreenScaffold {
                LifePointsScreen(
                    pagerState = pagerState,
                    displayedLifePoints1 = displayedLifePoints1,
                    displayedLifePoints2 = displayedLifePoints2,
                    isMuted = isMuted,
                    onToggleMute = { viewModel.toggleMute() },
                    onShowCalculatorWithMode = { player, calculatorMode ->
                        navController.navigate(
                            Routes.Calculator.createRoute(player, calculatorMode)
                        )
                    },
                    onRestart = { viewModel.start() }
                )
            }
        }
        composable(
            Routes.Calculator.route,
            arguments = listOf(
                navArgument(Routes.PLAYER_ARG) { type = NavType.IntType },
                navArgument(Routes.CALCULATOR_MODE_ARG) { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val player = Player.fromInt(backStackEntry.arguments?.getInt(Routes.PLAYER_ARG) ?: 1)
            val lifePoints = if (player == Player.ONE) lifePoints1 else lifePoints2
            val calculatorMode = CalculatorMode.fromInt(
                backStackEntry.arguments?.getInt(Routes.CALCULATOR_MODE_ARG) ?: CalculatorMode.SET.value
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
                    initialCalculatorMode = calculatorMode,
                    onDiscard = { navController.popBackStack() },
                    onSubmit = { result ->
                        viewModel.changeLifePoints(result, player)
                        navController.popBackStack()
                    },
                )
            }
        }
    }
}
