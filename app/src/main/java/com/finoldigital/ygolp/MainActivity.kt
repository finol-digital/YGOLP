package com.finoldigital.ygolp

import android.media.MediaPlayer
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.view.KeyEvent
import android.widget.TextView

class MainActivity : WearableActivity() {

    private var lpTextView: TextView? = null

    private var lpStart : MediaPlayer? = null
    private var lpChange : MediaPlayer? = null
    private var timeToDuel : MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Enables Always-on
        setAmbientEnabled()

        lpTextView = findViewById(R.id.lpTextView)

        restart()
    }

    private fun restart() {
        if (lpStart == null) {
            lpStart = MediaPlayer.create(this, R.raw.lp_start)
            lpStart?.setOnCompletionListener {
                stopLpStart()
                changeLp(8000)
            }
        }
        lpStart?.start()
    }

    // TODO: PRESS TO SWITCH TO CALCULATOR ACTIVITY
    private fun changeLp(lp: Int) {
        lpTextView?.text = lp.toString()
        // TODO: ANIMATE THE TEXT CHANGE
        if (lpChange == null) {
            lpChange = MediaPlayer.create(this, R.raw.lp_change)
            lpChange?.setOnCompletionListener {
                stopLpChange()
            }
        }
        lpChange?.start()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (event.repeatCount == 0) {
            when (keyCode) {
                KeyEvent.KEYCODE_STEM_1 -> {
                    startTimeToDuel()
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

    private fun startTimeToDuel() {
        if (timeToDuel == null) {
            timeToDuel = MediaPlayer.create(this, R.raw.its_time_to_duel)
            timeToDuel?.setOnCompletionListener {
                stopTimeToDuel()
            }
        }
        timeToDuel?.start()
    }

    override fun onStop() {
        stopLpStart()
        stopLpChange()
        stopTimeToDuel()
        super.onStop()
    }

    private fun stopLpStart() {
        if (lpStart != null) {
            lpStart?.release()
            lpStart = null
        }
    }

    private fun stopLpChange() {
        if (lpChange != null) {
            lpChange?.release()
            lpChange = null
        }
    }

    private fun stopTimeToDuel() {
        if (timeToDuel != null) {
            timeToDuel?.release()
            timeToDuel = null
        }
    }

}
