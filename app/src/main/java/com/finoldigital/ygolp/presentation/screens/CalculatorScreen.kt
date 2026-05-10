package com.finoldigital.ygolp.presentation.screens

import android.view.KeyEvent
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.finoldigital.ygolp.presentation.components.PlayerIndicator
import com.finoldigital.ygolp.presentation.enums.CalculatorMode
import com.finoldigital.ygolp.presentation.enums.Player
import com.finoldigital.ygolp.presentation.theme.AppColors

const val MIN_LIFE_POINTS = 0
const val MAX_LIFE_POINTS = 99999
private const val MAX_OPERAND_LENGTH = 5

@Composable
fun CalculatorScreen(
    player: Player = Player.ONE,
    lifePoints: Int = STARTING_LIFE_POINTS,
    initialCalculatorMode: CalculatorMode = CalculatorMode.SET,
    onDiscard: () -> Unit = {},
    onSubmit: (Int) -> Unit = {},
) {
    var calculatorMode by remember { mutableStateOf(initialCalculatorMode) }
    val operatorTextAndColor = remember(calculatorMode) {
        when (calculatorMode) {
            CalculatorMode.ADD -> "+" to Color.Green
            CalculatorMode.SUBTRACT -> "-" to Color.Red
            else -> "=>" to Color.Yellow
        }
    }
    var operandText by remember { mutableStateOf("0") }
    val result = remember(lifePoints, calculatorMode, operandText) {
        val operand = operandText.toIntOrNull() ?: 0
        when (calculatorMode) {
            CalculatorMode.ADD -> lifePoints + operand
            CalculatorMode.SUBTRACT -> lifePoints - operand
            else -> operand
        }.coerceIn(MIN_LIFE_POINTS, MAX_LIFE_POINTS)
    }

    fun append(char: String) {
        var currentText = operandText.trimStart('0')
        currentText += char
        // Limit to MAX_OPERAND_LENGTH characters
        if (currentText.length > MAX_OPERAND_LENGTH) {
            currentText = currentText.take(MAX_OPERAND_LENGTH)
        }
        operandText =
            if (currentText.toIntOrNull() == 0 || currentText.isEmpty()) "0" else currentText
    }

    fun pop() {
        operandText =
            if (operandText.length > 1) operandText.dropLast(1) else "0"
    }

    fun nextMode() {
        val entries = CalculatorMode.entries
        calculatorMode = entries[(calculatorMode.ordinal + 1) % entries.size]
    }

    fun submit() {
        onSubmit(result)
    }

    val operatorDescription = remember(calculatorMode) {
        when (calculatorMode) {
            CalculatorMode.ADD -> "Add"
            CalculatorMode.SUBTRACT -> "Subtract"
            else -> "Set"
        }
    }

    val focusRequester = remember { FocusRequester() }
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .focusRequester(focusRequester)
                .onKeyEvent { keyEvent ->
                    if (keyEvent.nativeKeyEvent.repeatCount == 0 && keyEvent.nativeKeyEvent.action == KeyEvent.ACTION_DOWN) {
                        when (keyEvent.nativeKeyEvent.keyCode) {
                            KeyEvent.KEYCODE_STEM_1 -> {
                                submit()
                                true
                            }

                            KeyEvent.KEYCODE_STEM_2 -> {
                                onDiscard()
                                true
                            }

                            else -> false
                        }
                    } else {
                        false
                    }
                }
                .focusable()
        ) {
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
                        accessibilityLabel = operatorDescription,
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
                        accessibilityLabel = "Halve",
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colors.primary,
                        onClick = { onSubmit(lifePoints / 2) }
                    )
                    Spacer(modifier = Modifier.weight(0.5f))
                }

                // Calculator Buttons
                // Row 1
                FlowRow(horizontalArrangement = Arrangement.Center, maxItemsInEachRow = 4) {
                    CalculatorButton("7", accessibilityLabel = "7") { append("7") }
                    CalculatorButton("8", accessibilityLabel = "8") { append("8") }
                    CalculatorButton("9", accessibilityLabel = "9") { append("9") }
                    CalculatorButton("C", accessibilityLabel = "Clear") { pop() }
                }
                // Row 2
                FlowRow(horizontalArrangement = Arrangement.Center, maxItemsInEachRow = 4) {
                    CalculatorButton("4", accessibilityLabel = "4") { append("4") }
                    CalculatorButton("5", accessibilityLabel = "5") { append("5") }
                    CalculatorButton("6", accessibilityLabel = "6") { append("6") }
                    CalculatorButton(
                        "X",
                        accessibilityLabel = "Discard",
                        color = MaterialTheme.colors.error
                    ) { onDiscard() }
                }
                // Row 3
                FlowRow(horizontalArrangement = Arrangement.Center, maxItemsInEachRow = 4) {
                    CalculatorButton("1", accessibilityLabel = "1") { append("1") }
                    CalculatorButton("2", accessibilityLabel = "2") { append("2") }
                    CalculatorButton("3", accessibilityLabel = "3") { append("3") }
                    CalculatorButton(
                        "=",
                        accessibilityLabel = "Submit",
                        color = MaterialTheme.colors.primary
                    ) { submit() }
                }
                // Row 4
                FlowRow(horizontalArrangement = Arrangement.Center, maxItemsInEachRow = 4) {
                    CalculatorButton("0", accessibilityLabel = "0") { append("0") }
                    CalculatorButton("00", accessibilityLabel = "Double zero") { append("00") }
                    CalculatorButton("000", accessibilityLabel = "Triple zero") { append("000") }
                }
            }
            PlayerIndicator(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
                player = player
            )
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        }
    }
}

@Composable
fun OperatorButton(
    text: String,
    modifier: Modifier = Modifier,
    accessibilityLabel: String = text,
    color: Color = MaterialTheme.colors.surface,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .aspectRatio(1.5f)
            .semantics { contentDescription = accessibilityLabel },
        colors = ButtonDefaults.buttonColors(backgroundColor = AppColors.CalculatorButtonDark)
    ) {
        Text(text, color = color)
    }
}

@Composable
fun CalculatorButton(
    text: String,
    modifier: Modifier = Modifier,
    accessibilityLabel: String = text,
    color: Color = MaterialTheme.colors.surface,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(ButtonDefaults.DefaultButtonSize * 0.75f)
            .aspectRatio(1.5f)
            .semantics { contentDescription = accessibilityLabel }
            .then(modifier),
        colors = ButtonDefaults.buttonColors(backgroundColor = color)
    ) {
        Text(text)
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun CalculatorScreenPreview() {
    CalculatorScreen()
}