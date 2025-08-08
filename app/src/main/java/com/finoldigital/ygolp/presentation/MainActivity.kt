package com.finoldigital.ygolp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.finoldigital.ygolp.R
import com.finoldigital.ygolp.presentation.theme.WearAppTheme
import com.google.android.horologist.compose.ambient.AmbientAware

const val EXTRA_YGOLP = "com.finoldigital.ygolp.EXTRA_YGOLP"

const val DEFAULT_LIFE_POINTS = 8000

class MainActivity : ComponentActivity() {
    /*
        private var lifePoints: Int = 0
        private var duelStartMP: MediaPlayer? = null
        private var lifePointsChangeMP: MediaPlayer? = null
        private var itsTimeToDuelMP: MediaPlayer? = null
    */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WearApp()
        }
    }

    @Composable
    fun WearApp() {
        val navController = rememberSwipeDismissableNavController()
        WearAppTheme {
            AmbientAware { ambientStateUpdate ->
                SwipeDismissableNavHost(
                    navController = navController,
                    startDestination = "life"
                ) {
                    composable("life") {
                        LifeScreen(
                            DEFAULT_LIFE_POINTS,
                            onShowCalculator = { navController.navigate("calculator") }
                        )
                    }
                    /* todo:
                    composable("calculator") {
                        CalculatorScreen()
                    }*/
                }
            }
        }
    }

    /* TODO:
    // Use this to detect touch
    val boxInsetLayout: BoxInsetLayout = findViewById(R.id.boxInsetLayout)
    boxInsetLayout.setOnTouchListener(this)

    // Restore state now instead of onRestoreInstanceState
    if (savedInstanceState == null)
        restart()
    else
        setLifePoints(savedInstanceState.getInt(LIFE_POINTS_KEY))*/
    @Composable
    fun LifeScreen(lifePoints: Int, onShowCalculator: () -> Unit) {
        Image(
            painterResource(R.drawable.lifepoints_background),
            contentDescription = "",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxSize()
                .clickable { onShowCalculator() }
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onShowCalculator() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onShowCalculator() },
                textAlign = TextAlign.Center,
                color = Color(0xFFFBFF0C.toInt()),
                fontFamily = FontFamily(Font(R.font.nationalyze_alp)),
                fontSize = 32.sp,
                text = lifePoints.toString()
            )
        }
    }

    @WearPreviewDevices
    @WearPreviewFontScales
    @Composable
    fun LifePointsScreenPreview() {
        LifeScreen(DEFAULT_LIFE_POINTS, onShowCalculator = {})
    }

    /* TODO: override fun onSaveInstanceState(outState: Bundle?) {
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
                    val tick = Random().nextInt(max - min + 1) + min
                    textView.text = tick.toString()
                }

                override fun onFinish() {
                    textView.text = lifePoints.toString()
                }
            }.start()
        }
        textView.text = lifePoints.toString()
    }*/

    /* TODO: override fun onTouch(view: View?, event: MotionEvent?): Boolean {
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

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CHANGE_LIFE_POINTS ->
                if (resultCode == RESULT_OK) {
                    data?.getIntExtra(EXTRA_YGOLP, DEFAULT_LIFE_POINTS)?.let { setLifePoints(it) }
                }
        }
    }

    companion object {
        internal const val CHANGE_LIFE_POINTS = 0
    }*/
}
