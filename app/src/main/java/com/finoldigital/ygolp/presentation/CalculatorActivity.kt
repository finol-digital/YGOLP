package com.finoldigital.ygolp.presentation

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text

// Assume these constants are defined somewhere accessible, e.g., in a companion object or a constants file
const val EXTRA_CALC_MODE = "com.finoldigital.ygolp.EXTRA_CALC_MODE"

class CalculatorActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val initialYgolp = intent.getIntExtra(EXTRA_YGOLP, DEFAULT_LIFE_POINTS)
        val initialMode = intent.getIntExtra(EXTRA_CALC_MODE, 0)

        setContent {
            CalculatorScreen(
                initialYgolp = initialYgolp,
                initialMode = initialMode,
                onFinish = { resultIntent ->
                    setResult(RESULT_OK, resultIntent)
                    finish()
                },
                onCancel = {
                    setResult(RESULT_CANCELED) // Or RESULT_OK based on `buttonX`'''s original intent
                    finish()
                }
            )
        }
    }
}

@Composable
fun CalculatorScreen(
    initialYgolp: Int,
    initialMode: Int,
    onFinish: (Intent) -> Unit,
    onCancel: () -> Unit
) {
    var ygolp by remember { mutableIntStateOf(initialYgolp) }
    var mode by remember { mutableIntStateOf(initialMode) } // 0:=> 1:- 2:+
    var text by remember { mutableStateOf("0") }

    val focusRequester = remember { FocusRequester() }

    val operatorTextAndColor = remember(mode) {
        when (mode) {
            2 -> "+" to Color.Green
            1 -> "-" to Color.Red
            else -> "=>" to Color.Yellow // 0
        }
    }

    fun append(char: String) {
        var currentText = text.trimStart('0')
        currentText += char
        text = if (currentText.toIntOrNull() == 0 || currentText.isEmpty()) "0" else currentText
    }

    fun pop() {
        text = if (text.length > 1) text.substring(0, text.length - 1) else "0"
    }

    fun nextMode() {
        mode = (mode + 1) % 3
    }

    fun submit() {
        val input = text.toIntOrNull() ?: 0
        val resultIntent = Intent()
        val resultLp = when (mode) {
            0 -> input
            1 -> ygolp - input
            2 -> ygolp + input
            else -> ygolp
        }
        resultIntent.putExtra(EXTRA_YGOLP, resultLp)
        // Pass back the mode if needed, or other data
        // resultIntent.putExtra(EXTRA_CALC_MODE, mode)
        onFinish(resultIntent)
    }

    // Handle physical button presses using onKeyEvent if preferred for Compose-centric handling
    // The original onKeyDown logic from Activity is kept for now if specific Activity behavior is needed.
    // If you want full Compose handling:
    // LaunchedEffect(Unit) { focusRequester.requestFocus() }

    MaterialTheme { // Assuming you have a Wear Compose Theme
        Scaffold(
            modifier = Modifier
                .focusRequester(focusRequester)
                .onKeyEvent { keyEvent ->
                    if (keyEvent.nativeKeyEvent.repeatCount == 0 && keyEvent.nativeKeyEvent.action == KeyEvent.ACTION_DOWN) {
                        when (keyEvent.nativeKeyEvent.keyCode) {
                            KeyEvent.KEYCODE_STEM_1 -> {
                                submit()
                                true
                            }

                            KeyEvent.KEYCODE_STEM_2 -> {
                                onCancel() // Or a specific action for STEM_2 if it'''s not just cancel
                                true
                            }

                            else -> false
                        }
                    } else {
                        false
                    }
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Operand Display
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = { nextMode() },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = operatorTextAndColor.second.copy(
                                alpha = 0.3f
                            )
                        )
                    ) {
                        Text(text = operatorTextAndColor.first, color = operatorTextAndColor.second)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = text,
                        fontSize = 24.sp,
                        color = operatorTextAndColor.second,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Number Buttons
                val buttonModifier = Modifier.weight(1f)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    CalculatorButton("7", buttonModifier) { append("7") }
                    CalculatorButton("8", buttonModifier) { append("8") }
                    CalculatorButton("9", buttonModifier) { append("9") }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    CalculatorButton("4", buttonModifier) { append("4") }
                    CalculatorButton("5", buttonModifier) { append("5") }
                    CalculatorButton("6", buttonModifier) { append("6") }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    CalculatorButton("1", buttonModifier) { append("1") }
                    CalculatorButton("2", buttonModifier) { append("2") }
                    CalculatorButton("3", buttonModifier) { append("3") }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    CalculatorButton("0", buttonModifier) { append("0") }
                    CalculatorButton("00", buttonModifier) { append("00") }
                    CalculatorButton("000", buttonModifier) { append("000") }
                }

                // Action Buttons
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    CalculatorButton("C", buttonModifier, color = Color.DarkGray) { pop() }
                    CalculatorButton(
                        "X",
                        buttonModifier,
                        color = MaterialTheme.colors.error
                    ) { onCancel() }
                    CalculatorButton(
                        "=",
                        buttonModifier,
                        color = MaterialTheme.colors.primary
                    ) { submit() }
                }
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
            }
        }
    }
}

@Composable
fun CalculatorButton(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.surface,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.aspectRatio(1.5f), // Adjust aspect ratio as needed for Wear
        colors = ButtonDefaults.buttonColors(backgroundColor = color)
    ) {
        Text(text)
    }
}
