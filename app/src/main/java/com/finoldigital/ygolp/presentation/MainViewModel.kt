package com.finoldigital.ygolp.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.os.CountDownTimer
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.finoldigital.ygolp.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.random.Random

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "lifepoints_settings")

@SuppressLint("StaticFieldLeak") // Always holds applicationContext via Factory, not an Activity
class MainViewModel(
    private val context: Context,
    val soundManager: SoundManager
) : ViewModel() {

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


    private var countDownTimerP1: CountDownTimer? = null
    private var countDownTimerP2: CountDownTimer? = null

    init {
        viewModelScope.launch {
            val preferences = context.dataStore.data.first()
            val p1 = preferences[LIFE_POINTS_P1_DS_KEY]
            val p2 = preferences[LIFE_POINTS_P2_DS_KEY]
            _isMuted.value = preferences[IS_MUTED_DS_KEY] ?: false
            soundManager.isMuted = _isMuted.value

            if (p1 != null) {
                _lifePoints.value = p1
                _lifePoints2.value = p2 ?: 0
                _displayedLifePoints.value = _lifePoints.value
                _displayedLifePoints2.value = _lifePoints2.value
            } else {
                // First launch — trigger start
                start()
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
        _isMuted.value = !_isMuted.value
        soundManager.isMuted = _isMuted.value
        viewModelScope.launch {
            context.dataStore.edit { settings ->
                settings[IS_MUTED_DS_KEY] = _isMuted.value
            }
        }
    }

    fun restart() {
        _lifePoints.value = 0
        _lifePoints2.value = 0
        _displayedLifePoints.value = 0
        _displayedLifePoints2.value = 0
        viewModelScope.launch {
            context.dataStore.edit { settings ->
                settings[LIFE_POINTS_P1_DS_KEY] = 0
                settings[LIFE_POINTS_P2_DS_KEY] = 0
            }
        }

        if (_isMuted.value) {
            changeLifePoints(STARTING_LIFE_POINTS, 1, playSound = false)
            changeLifePoints(STARTING_LIFE_POINTS, 2, playSound = false)
            return
        }

        soundManager.play(R.raw.duel_start) {
            changeLifePoints(STARTING_LIFE_POINTS, 1)
            changeLifePoints(STARTING_LIFE_POINTS, 2)
        }
    }

    fun startItsTimeToDuel() {
        if (_isMuted.value) return
        soundManager.play(R.raw.its_time_to_duel)
    }

    fun changeLifePoints(lp: Int, player: Int, playSound: Boolean = true) {
        val clampedLp = lp.coerceIn(0, 99999)
        val currentLp = if (player == 1) _lifePoints.value else _lifePoints2.value
        if (currentLp != clampedLp) {
            if (player == 1) {
                _lifePoints.value = clampedLp
                viewModelScope.launch {
                    context.dataStore.edit { settings ->
                        settings[LIFE_POINTS_P1_DS_KEY] = clampedLp
                    }
                }
            } else {
                _lifePoints2.value = clampedLp
                viewModelScope.launch {
                    context.dataStore.edit { settings ->
                        settings[LIFE_POINTS_P2_DS_KEY] = clampedLp
                    }
                }
            }
            if (playSound && !_isMuted.value) {
                soundManager.play(R.raw.lifepoints_change)
            }

            // Cancel any existing timer for this player
            if (player == 1) {
                countDownTimerP1?.cancel()
            } else {
                countDownTimerP2?.cancel()
            }

            val timer = object : CountDownTimer(if (playSound) 2100 else 0, 50) {
                override fun onTick(millisUntilFinished: Long) {
                    val tick = Random.nextInt(1000, 10000)
                    if (player == 1) {
                        _displayedLifePoints.value = tick
                    } else {
                        _displayedLifePoints2.value = tick
                    }
                }

                override fun onFinish() {
                    if (player == 1) {
                        _displayedLifePoints.value = _lifePoints.value
                    } else {
                        _displayedLifePoints2.value = _lifePoints2.value
                    }
                }
            }

            if (player == 1) {
                countDownTimerP1 = timer
            } else {
                countDownTimerP2 = timer
            }
            timer.start()
        }
        if (player == 1) {
            _displayedLifePoints.value = _lifePoints.value
        } else {
            _displayedLifePoints2.value = _lifePoints2.value
        }
    }

    override fun onCleared() {
        super.onCleared()
        countDownTimerP1?.cancel()
        countDownTimerP2?.cancel()
    }

    class Factory(
        private val context: Context,
        private val soundManager: SoundManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(context.applicationContext, soundManager) as T
        }
    }
}

