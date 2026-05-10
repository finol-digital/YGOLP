package com.finoldigital.ygolp.presentation

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.pager.HorizontalPager
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

    val displayedLifePoints1 by viewModel.displayedLifePoints.collectAsState()
    val displayedLifePoints2 by viewModel.displayedLifePoints2.collectAsState()
    val lifePoints1 by viewModel.lifePoints.collectAsState()
    val lifePoints2 by viewModel.lifePoints2.collectAsState()
    val isMuted by viewModel.isMuted.collectAsState()

    SwipeDismissableNavHost(
        navController = navController,
        startDestination = Screen.LifePoints.route
    ) {
        composable(Screen.LifePoints.route) {
            val pagerState = rememberPagerState(pageCount = { 2 })
            ScreenScaffold {
                HorizontalPager(state = pagerState) { page ->
                    val player = if (page == 0) Player.ONE else Player.TWO
                    val displayedLifePoints = if (player == Player.ONE) displayedLifePoints1 else displayedLifePoints2
                    if (player == Player.ONE) {
                        LifePointsScreen(
                            player,
                            displayedLifePoints,
                            isMuted,
                            onToggleMute = { viewModel.toggleMute() },
                            onShowCalculatorWithMode = { calculatorMode ->
                                navController.navigate(
                                    Screen.Calculator.createRoute(
                                        Player.ONE,
                                        calculatorMode
                                    )
                                )
                            },
                            onRestart = if (displayedLifePoints1 <= 0) ({ viewModel.start() }) else null
                        )
                    } else {
                        LifePointsScreen(
                            player,
                            displayedLifePoints,
                            isMuted,
                            onToggleMute = { viewModel.toggleMute() },
                            onShowCalculatorWithMode = { calculatorMode ->
                                navController.navigate(
                                    Screen.Calculator.createRoute(
                                        Player.TWO,
                                        calculatorMode
                                    )
                                )
                            },
                            onRestart = if (displayedLifePoints2 <= 0) ({ viewModel.start() }) else null
                        )
                    }
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
