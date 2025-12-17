package com.finoldigital.ygolp.presentation

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.finoldigital.ygolp.R
import com.google.android.horologist.compose.ambient.AmbientAware
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Random

const val LIFE_POINTS_KEY = "LIFE_POINTS_KEY"
const val LIFE_POINTS_2_KEY = "LIFE_POINTS_2_KEY"

// At the top-level, outside MainActivity
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "lifepoints_settings")

class MainActivity : ComponentActivity() {

    companion object {
        val LIFE_POINTS_P1_DS_KEY = intPreferencesKey("life_points_p1")
        val LIFE_POINTS_P2_DS_KEY = intPreferencesKey("life_points_p2")
    }

    private var lifePoints by mutableIntStateOf(0)
    private var displayedLifePoints by mutableIntStateOf(0)
    private var lifePoints2 by mutableIntStateOf(0)
    private var displayedLifePoints2 by mutableIntStateOf(0)

    private var duelStartMP: MediaPlayer? = null
    private var lifePointsChangeMP: MediaPlayer? = null
    private var itsTimeToDuelMP: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load data from DataStore
        runBlocking { // Use runBlocking for simplicity in onCreate
            val preferences = dataStore.data.first()
            lifePoints = preferences[LIFE_POINTS_P1_DS_KEY] ?: 0
            lifePoints2 = preferences[LIFE_POINTS_P2_DS_KEY] ?: 0
        }
        displayedLifePoints = lifePoints
        displayedLifePoints2 = lifePoints2


        if (savedInstanceState == null) {
            // If it's the very first launch (no saved instance state and no datastore values other than defaults)
            if (lifePoints == 0 && lifePoints2 == 0) {
                // Check if it was really 0 or the default from datastore
                runBlocking {
                    val preferences = dataStore.data.first()
                    if (preferences[LIFE_POINTS_P1_DS_KEY] == null) { // Only call start if P1 was not in datastore
                        start()
                    }
                }
            }
        } else {
            // Data is already loaded from DataStore, onSaveInstanceState is for in-memory state
            // We can choose to trust DataStore more or onSaveInstanceState if it exists
            // For now, let's assume if savedInstanceState exists, it's more current for the active session.
            lifePoints = savedInstanceState.getInt(LIFE_POINTS_KEY, lifePoints)
            lifePoints2 = savedInstanceState.getInt(LIFE_POINTS_2_KEY, lifePoints2)
            displayedLifePoints = lifePoints
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
        // Reset lifePoints and save
        lifePoints = 0
        lifePoints2 = 0
        lifecycleScope.launch {
            dataStore.edit { settings ->
                settings[LIFE_POINTS_P1_DS_KEY] = 0
                settings[LIFE_POINTS_P2_DS_KEY] = 0
            }
        }
        displayedLifePoints = 0
        displayedLifePoints2 = 0

        if (duelStartMP == null) {
            duelStartMP = MediaPlayer.create(this, R.raw.duel_start)
            duelStartMP?.setOnCompletionListener {
                stopDuelStart()
                changeLifePoints(STARTING_LIFE_POINTS, 1)
                changeLifePoints(STARTING_LIFE_POINTS, 2)
            }
        }
        duelStartMP?.start()
    }

    fun changeLifePoints(lp: Int, player: Int, playSound: Boolean = true) {
        val currentLp = if (player == 1) lifePoints else lifePoints2
        if (currentLp != lp) {
            if (player == 1) {
                lifePoints = lp
                lifecycleScope.launch {
                    dataStore.edit { settings ->
                        settings[LIFE_POINTS_P1_DS_KEY] = lp
                    }
                }
            } else {
                lifePoints2 = lp
                lifecycleScope.launch {
                    dataStore.edit { settings ->
                        settings[LIFE_POINTS_P2_DS_KEY] = lp
                    }
                }
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

        AmbientAware { _ ->
            SwipeDismissableNavHost(
                navController = navController,
                startDestination = "lifepoints/1" // Start with player 1
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
                            onRestart = if (displayedLifePoints <= 0) ({ start() }) else null
                        )
                    } else {
                        LifePointsScreen(
                            displayedLifePoints = displayedLifePoints2,
                            onShowCalculatorWithMode = { mode -> navController.navigate("calculator/2/$mode") },
                            onSwipePlayer = { navController.popBackStack() },
                            playerId = player,
                            onRestart = if (displayedLifePoints2 <= 0) ({ start() }) else null
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
                        }, { navController.popBackStack() },
                        playerId = player
                    )
                }
            }
        }
    }
}
