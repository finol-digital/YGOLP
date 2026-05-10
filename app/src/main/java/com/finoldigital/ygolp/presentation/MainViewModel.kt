package com.finoldigital.ygolp.presentation

import android.app.Application
import android.os.CountDownTimer
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.finoldigital.ygolp.R
import com.finoldigital.ygolp.presentation.enums.Player
import com.finoldigital.ygolp.presentation.screens.STARTING_LIFE_POINTS
import com.finoldigital.ygolp.presentation.screens.MAX_LIFE_POINTS
import com.finoldigital.ygolp.presentation.screens.MIN_LIFE_POINTS
import com.finoldigital.ygolp.presentation.util.SoundManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.random.Random

private val Application.dataStore: DataStore<Preferences> by preferencesDataStore(name = "lifepoints_settings")

class MainViewModel(
    application: Application,
    private val soundManager: SoundManager
) : AndroidViewModel(application) {

    companion object {
        val LIFE_POINTS_P1_DS_KEY = intPreferencesKey("life_points_p1")
        val LIFE_POINTS_P2_DS_KEY = intPreferencesKey("life_points_p2")
        val IS_MUTED_DS_KEY = booleanPreferencesKey("is_muted")
    }

    private val _lifePoints = MutableStateFlow(0)
    val lifePoints: StateFlow<Int> = _lifePoints.asStateFlow()

    private val _lifePoints2 = MutableStateFlow(0)
    val lifePoints2: StateFlow<Int> = _lifePoints2.asStateFlow()

    private val _displayedLifePoints = MutableStateFlow(0)
    val displayedLifePoints: StateFlow<Int> = _displayedLifePoints.asStateFlow()

    private val _displayedLifePoints2 = MutableStateFlow(0)
    val displayedLifePoints2: StateFlow<Int> = _displayedLifePoints2.asStateFlow()

    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

    private val _isOnCalculatorScreen = MutableStateFlow(false)

    private var countDownTimerP1: CountDownTimer? = null
    private var countDownTimerP2: CountDownTimer? = null

    init {
        viewModelScope.launch {
            getApplication<Application>().dataStore.data.collectLatest { preferences ->
                val p1 = preferences[LIFE_POINTS_P1_DS_KEY]
                val p2 = preferences[LIFE_POINTS_P2_DS_KEY]
                _isMuted.value = preferences[IS_MUTED_DS_KEY] ?: false
                soundManager.isMuted = _isMuted.value

                if (p1 != null) {
                    _lifePoints.value = p1
                    _lifePoints2.value = p2 ?: 0

                    // Only update displayed if no timer is running to avoid jumpiness during collection
                    if (countDownTimerP1 == null) {
                        _displayedLifePoints.value = _lifePoints.value
                    }
                    if (countDownTimerP2 == null) {
                        _displayedLifePoints2.value = _lifePoints2.value
                    }
                } else if (_lifePoints.value == 0 && _lifePoints2.value == 0) {
                    // First launch — trigger start if we haven't already started
                    start()
                }
            }
        }
    }

    fun start() {
        if (_isMuted.value) {
            restart()
            return
        }
        soundManager.play(R.raw.its_time_to_duel) {
            restart()
        }
    }

    fun toggleMute() {
        val nextMuted = !_isMuted.value
        _isMuted.value = nextMuted
        soundManager.isMuted = nextMuted
        viewModelScope.launch {
            getApplication<Application>().dataStore.edit { settings ->
                settings[IS_MUTED_DS_KEY] = nextMuted
            }
        }
    }

    fun setOnCalculatorScreen(isOnCalculator: Boolean) {
        _isOnCalculatorScreen.value = isOnCalculator
    }

    fun handleStemKey(keyCode: Int): Boolean {
        if (_isOnCalculatorScreen.value) return false
        return when (keyCode) {
            android.view.KeyEvent.KEYCODE_STEM_1 -> {
                restart()
                true
            }
            android.view.KeyEvent.KEYCODE_STEM_2 -> {
                startItsTimeToDuel()
                true
            }
            else -> false
        }
    }

    fun restart() {
        viewModelScope.launch {
            getApplication<Application>().dataStore.edit { settings ->
                settings[LIFE_POINTS_P1_DS_KEY] = 0
                settings[LIFE_POINTS_P2_DS_KEY] = 0
            }

            if (_isMuted.value) {
                changeLifePoints(STARTING_LIFE_POINTS, Player.ONE, playSound = false)
                changeLifePoints(STARTING_LIFE_POINTS, Player.TWO, playSound = false)
                return@launch
            }

            soundManager.play(R.raw.duel_start) {
                changeLifePoints(STARTING_LIFE_POINTS, Player.ONE)
                changeLifePoints(STARTING_LIFE_POINTS, Player.TWO)
            }
        }
    }

    fun startItsTimeToDuel() {
        if (_isMuted.value) return
        soundManager.play(R.raw.its_time_to_duel)
    }

    fun changeLifePoints(lp: Int, player: Player, playSound: Boolean = true) {
        val clampedLp = lp.coerceIn(MIN_LIFE_POINTS, MAX_LIFE_POINTS)
        val currentLp = if (player == Player.ONE) _lifePoints.value else _lifePoints2.value
        if (currentLp != clampedLp) {
            viewModelScope.launch {
                getApplication<Application>().dataStore.edit { settings ->
                    if (player == Player.ONE) {
                        settings[LIFE_POINTS_P1_DS_KEY] = clampedLp
                    } else {
                        settings[LIFE_POINTS_P2_DS_KEY] = clampedLp
                    }
                }
            }
            if (playSound && !_isMuted.value) {
                soundManager.play(R.raw.lifepoints_change)
            }

            // Cancel any existing timer for this player
            if (player == Player.ONE) {
                countDownTimerP1?.cancel()
                countDownTimerP1 = null
            } else {
                countDownTimerP2?.cancel()
                countDownTimerP2 = null
            }

            if (!playSound) {
                // Skip timer creation when no sound — set displayed value directly
                if (player == Player.ONE) {
                    _lifePoints.value = clampedLp
                    _displayedLifePoints.value = clampedLp
                } else {
                    _lifePoints2.value = clampedLp
                    _displayedLifePoints2.value = clampedLp
                }
                return
            }

            val timer = object : CountDownTimer(2100, 50) {
                override fun onTick(millisUntilFinished: Long) {
                    val tick = Random.nextInt(1000, 10000)
                    if (player == Player.ONE) {
                        _displayedLifePoints.value = tick
                    } else {
                        _displayedLifePoints2.value = tick
                    }
                }

                override fun onFinish() {
                    if (player == Player.ONE) {
                        _displayedLifePoints.value = _lifePoints.value
                        countDownTimerP1 = null
                    } else {
                        _displayedLifePoints2.value = _lifePoints2.value
                        countDownTimerP2 = null
                    }
                }
            }

            if (player == Player.ONE) {
                countDownTimerP1 = timer
            } else {
                countDownTimerP2 = timer
            }
            timer.start()
        }
    }

    override fun onCleared() {
        super.onCleared()
        countDownTimerP1?.cancel()
        countDownTimerP2?.cancel()
    }

    class Factory(
        private val application: Application,
        private val soundManager: SoundManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(application, soundManager) as T
        }
    }
}
