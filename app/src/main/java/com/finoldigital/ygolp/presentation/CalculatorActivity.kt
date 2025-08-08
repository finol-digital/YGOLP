package com.finoldigital.ygolp.presentation

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
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales

const val EXTRA_CALCULATOR_MODE = "com.finoldigital.ygolp.EXTRA_CALCULATOR_MODE"

class CalculatorActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val initialLifePoints = intent.getIntExtra(EXTRA_LIFE_POINTS, STARTING_LIFE_POINTS)
        val initialCalculatorMode = intent.getIntExtra(EXTRA_CALCULATOR_MODE, 0)

        setContent {
            CalculatorScreen(
                initialLifePoints = initialLifePoints,
                initialCalculatorMode = initialCalculatorMode,
                onFinish = {},
                onCancel = {}
            )
        }
    }
}

@Composable
fun CalculatorScreen(
    initialLifePoints: Int,
    initialCalculatorMode: Int,
    onFinish: (Int) -> Unit,
    onCancel: () -> Unit
) {
    val lifePoints = initialLifePoints
    var calculatorMode by remember { mutableIntStateOf(initialCalculatorMode) } // 0:=> 1:- 2:+
    val operatorTextAndColor = remember(calculatorMode) {
        when (calculatorMode) {
            2 -> "+" to Color.Green
            1 -> "-" to Color.Red
            else -> "=>" to Color.Yellow // 0
        }
    }
    var operandText by remember { mutableStateOf("0") }
    val result = remember(lifePoints, calculatorMode, operandText) {
        val operand = operandText.toIntOrNull() ?: 0
        when (calculatorMode) {
            2 -> lifePoints + operand
            1 -> lifePoints - operand
            else -> operand // 0
        }
    }

    fun append(char: String) {
        var currentText = operandText.trimStart('0')
        currentText += char
        operandText =
            if (currentText.toIntOrNull() == 0 || currentText.isEmpty()) "0" else currentText
    }

    fun pop() {
        operandText =
            if (operandText.length > 1) operandText.substring(0, operandText.length - 1) else "0"
    }

    fun nextMode() {
        calculatorMode = (calculatorMode + 1) % 3
    }

    fun submit() {
        onFinish(result)
    }

    val focusRequester = remember { FocusRequester() }
    MaterialTheme {
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
                                onCancel()
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
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {

                // LifePoints Display
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = lifePoints.toString(),
                        fontSize = 20.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Operator and Operand Display
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    OperatorButton(
                        text = operatorTextAndColor.first,
                        modifier = Modifier.weight(1f),
                        color = operatorTextAndColor.second,
                        onClick = {nextMode()})
                    Text(
                        text = operandText,
                        fontSize = 20.sp,
                        color = operatorTextAndColor.second,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(3f)
                    )
                }

                // Calculator Buttons
                val buttonModifier = Modifier.weight(1f)
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    CalculatorButton("7", buttonModifier) { append("7") }
                    CalculatorButton("8", buttonModifier) { append("8") }
                    CalculatorButton("9", buttonModifier) { append("9") }
                    CalculatorButton("C", buttonModifier, color = Color.DarkGray) { pop() }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    CalculatorButton("4", buttonModifier) { append("4") }
                    CalculatorButton("5", buttonModifier) { append("5") }
                    CalculatorButton("6", buttonModifier) { append("6") }
                    CalculatorButton(
                        "X",
                        buttonModifier,
                        color = MaterialTheme.colors.error
                    ) { onCancel() }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    CalculatorButton("1", buttonModifier) { append("1") }
                    CalculatorButton("2", buttonModifier) { append("2") }
                    CalculatorButton("3", buttonModifier) { append("3") }
                    CalculatorButton(
                        "=",
                        buttonModifier,
                        color = MaterialTheme.colors.primary
                    ) { submit() }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    Spacer(modifier = Modifier.weight(0.5f))
                    CalculatorButton("0", buttonModifier) { append("0") }
                    CalculatorButton("00", buttonModifier) { append("00") }
                    CalculatorButton("000", buttonModifier) { append("000") }
                    Spacer(modifier = Modifier.weight(0.5f))
                }
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
            }
        }
    }
}

@Composable
fun OperatorButton(
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

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun CalculatorScreenPreview() {
    CalculatorScreen(STARTING_LIFE_POINTS, 1, onFinish = {}, onCancel = {})
}
