package com.finoldigital.ygolp.presentation

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box // Added import
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales

const val EXTRA_CALCULATOR_MODE = "com.finoldigital.ygolp.EXTRA_CALCULATOR_MODE"

class CalculatorActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val initialLifePoints = intent.getIntExtra(EXTRA_LIFE_POINTS, STARTING_LIFE_POINTS)
        val initialCalculatorMode = intent.getIntExtra(EXTRA_CALCULATOR_MODE, 0)
        // Assuming playerId might also come from intent or be fixed for this activity instance
        val playerId = intent.getIntExtra("PLAYER_ID_KEY", 1) // Example, adjust as needed

        setContent {
            CalculatorScreen(
                initialLifePoints = initialLifePoints,
                initialCalculatorMode = initialCalculatorMode,
                onFinish = { /* do nothing */ },
                onCancel = { /* do nothing */ },
                playerId = playerId
            )
        }
    }
}

@Composable
fun CalculatorScreen(
    initialLifePoints: Int,
    initialCalculatorMode: Int,
    onFinish: (Int) -> Unit,
    onCancel: () -> Unit,
    playerId: Int = 1
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
            Box(modifier = Modifier.fillMaxSize()) { // Wrap content in a Box
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(1.dp)
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Spacer(modifier = Modifier.weight(0.5f))
                        OperatorButton(
                            text = operatorTextAndColor.first,
                            modifier = Modifier.weight(1f),
                            color = operatorTextAndColor.second,
                            onClick = { nextMode() }
                        )
                        Text(
                            text = operandText,
                            fontSize = 20.sp,
                            color = operatorTextAndColor.second,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1.5f)
                        )
                        OperatorButton(
                            text = "1/2",
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colors.primary,
                            onClick = { onFinish(lifePoints / 2) }
                        )
                        Spacer(modifier = Modifier.weight(0.5f))
                    }

                    // Calculator Buttons
                    // Row 1
                    FlowRow(horizontalArrangement = Arrangement.Center, maxItemsInEachRow = 4) {
                        CalculatorButton("7") { append("7") }
                        CalculatorButton("8") { append("8") }
                        CalculatorButton("9") { append("9") }
                        CalculatorButton("C", color = Color.DarkGray) { pop() }
                    }
                    // Row 2
                    FlowRow(horizontalArrangement = Arrangement.Center, maxItemsInEachRow = 4) {
                        CalculatorButton("4") { append("4") }
                        CalculatorButton("5") { append("5") }
                        CalculatorButton("6") { append("6") }
                        CalculatorButton(
                            "X",
                            color = MaterialTheme.colors.error
                        ) { onCancel() }
                    }
                    // Row 3
                    FlowRow(horizontalArrangement = Arrangement.Center, maxItemsInEachRow = 4) {
                        CalculatorButton("1") { append("1") }
                        CalculatorButton("2") { append("2") }
                        CalculatorButton("3") { append("3") }
                        CalculatorButton(
                            "=",
                            color = MaterialTheme.colors.primary
                        ) { submit() }
                    }
                    // Row 4
                    FlowRow(horizontalArrangement = Arrangement.Center, maxItemsInEachRow = 4) {
                        CalculatorButton("0") { append("0") }
                        CalculatorButton("00") { append("00") }
                        CalculatorButton("000") { append("000") }
                    }
                    // Spacer to push PlayerIndicator down if Column doesn't fill height
                    // This might not be needed if Column is .fillMaxSize() and PlayerIndicator is outside it.
                    // Spacer(Modifier.weight(1f))
                }
                PlayerIndicator(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp), // Added padding for spacing from the edge
                    playerId = playerId
                )
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
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray)
    ) {
        Text(text, color = color)
    }
}

@Composable
fun CalculatorButton(
    text: String,
    modifier: Modifier = Modifier, // For additional styling from the caller
    color: Color = MaterialTheme.colors.surface,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier // Start with a new Modifier chain for the Button's core style
            .width(ButtonDefaults.DefaultButtonSize * 0.75f)
            .aspectRatio(1.5f) // Calculate height from this fixed width
            .then(modifier), // Then apply any modifier passed to CalculatorButton externally
        colors = ButtonDefaults.buttonColors(backgroundColor = color)
    ) {
        Text(text)
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun CalculatorScreenPreview() {
    CalculatorScreen(STARTING_LIFE_POINTS, 0, onFinish = {}, onCancel = {}, playerId = 1)
}
