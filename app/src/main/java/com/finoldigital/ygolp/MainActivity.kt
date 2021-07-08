package com.finoldigital.ygolp

import android.media.MediaPlayer
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.view.KeyEvent
import android.widget.TextView

class MainActivity : WearableActivity() {

    private val lifePointsKey: String = "lifePointsKey"

    private val defaultLifePoints = 8000

    private var lifePoints: Int = 0

    private lateinit var textView: TextView

    private var duelStartMP: MediaPlayer? = null
    private var lifePointsChangeMP: MediaPlayer? = null
    private var itsTimeToDuelMP: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Enables Always-on
        setAmbientEnabled()

        // This is the earliest we can initialize the text view
        textView = findViewById(R.id.textView)

        // Restore state now instead of onRestoreInstanceState
        if (savedInstanceState == null)
            restart()
        else
            changeLifePoints(savedInstanceState.getInt(lifePointsKey))
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.run {
            putInt(lifePointsKey, lifePoints)
        }
        if (outState != null)
            super.onSaveInstanceState(outState)
    }

    private fun restart() {
        lifePoints = 0
        if (duelStartMP == null) {
            duelStartMP = MediaPlayer.create(this, R.raw.duel_start)
            duelStartMP?.setOnCompletionListener {
                stopDuelStart()
                changeLifePoints(defaultLifePoints)
            }
        }
        duelStartMP?.start()
    }

    // TODO: PRESS TO SWITCH TO CALCULATOR ACTIVITY
    private fun changeLifePoints(lp: Int) {
        if (lifePoints == lp)
            return

        lifePoints = lp
        textView.text = lifePoints.toString()
        // TODO: ANIMATE THE TEXT CHANGE
        if (lifePointsChangeMP == null) {
            lifePointsChangeMP = MediaPlayer.create(this, R.raw.lifepoints_change)
            lifePointsChangeMP?.setOnCompletionListener {
                stopLifePointsChange()
            }
        }
        lifePointsChangeMP?.start()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (event.repeatCount == 0) {
            when (keyCode) {
                KeyEvent.KEYCODE_STEM_1 -> {
                    startItsTimeToDuel()
                    true
                }
                KeyEvent.KEYCODE_STEM_2 -> {
                    restart()
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

}
