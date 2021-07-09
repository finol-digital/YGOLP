package com.finoldigital.ygolp

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.support.wearable.activity.WearableActivity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.wear.widget.BoxInsetLayout
import java.util.*

const val EXTRA_YGOLP = "com.finoldigital.ygolp.EXTRA_YGOLP"

const val LIFE_POINTS_KEY: String = "LIFE_POINTS_KEY"

const val DEFAULT_LIFE_POINTS = 8000

class MainActivity : WearableActivity(), View.OnTouchListener {

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

        // Use this to detect touch
        val boxInsetLayout: BoxInsetLayout = findViewById(R.id.boxInsetLayout)
        boxInsetLayout.setOnTouchListener(this)

        // Restore state now instead of onRestoreInstanceState
        if (savedInstanceState == null)
            restart()
        else
            setLifePoints(savedInstanceState.getInt(LIFE_POINTS_KEY))
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.run {
            putInt(LIFE_POINTS_KEY, lifePoints)
        }
        if (outState != null)
            super.onSaveInstanceState(outState)
    }

    private fun restart() {
        lifePoints = 0
        textView.text = lifePoints.toString()
        if (duelStartMP == null) {
            duelStartMP = MediaPlayer.create(this, R.raw.duel_start)
            duelStartMP?.setOnCompletionListener {
                stopDuelStart()
                setLifePoints(DEFAULT_LIFE_POINTS)
            }
        }
        duelStartMP?.start()
    }

    private fun setLifePoints(lp: Int) {
        if (lifePoints != lp) {
            lifePoints = lp
            if (lifePointsChangeMP == null) {
                lifePointsChangeMP = MediaPlayer.create(this, R.raw.lifepoints_change)
                lifePointsChangeMP?.setOnCompletionListener {
                    stopLifePointsChange()
                }
            }
            lifePointsChangeMP?.start()
            object : CountDownTimer(2100, 50) {
                override fun onTick(millisUntilFinished: Long) {
                    val min = 1000
                    val max = 9999
                    textView.text = (Random().nextInt(max - min + 1) + min).toString()
                }

                override fun onFinish() {
                    textView.text = lifePoints.toString()
                }
            }.start()
        }
        textView.text = lifePoints.toString()
    }

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP) {
            view?.performClick()
            val height = resources.displayMetrics.heightPixels
            val intent = Intent(this, CalculatorActivity::class.java).apply {
                putExtra(EXTRA_YGOLP, lifePoints)
                var mode = 2
                val y = event.rawY
                if (y > height / 3)
                    mode = 0
                if (y > height / 3 * 2)
                    mode = 1
                putExtra(EXTRA_CALC_MODE, mode)
            }
            startActivityForResult(intent, CHANGE_LIFE_POINTS)
        }
        return true
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        when (requestCode) {
            CHANGE_LIFE_POINTS ->
                if (resultCode == RESULT_OK) {
                    intent?.getIntExtra(EXTRA_YGOLP, DEFAULT_LIFE_POINTS)?.let { setLifePoints(it) }
                }
        }
    }

    companion object {
        internal const val CHANGE_LIFE_POINTS = 0
    }

}
