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
import androidx.compose.ui.res.stringResource
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
import com.finoldigital.ygolp.R
import com.finoldigital.ygolp.presentation.components.PlayerIndicator
import com.finoldigital.ygolp.presentation.constants.MAX_LIFE_POINTS
import com.finoldigital.ygolp.presentation.constants.MIN_LIFE_POINTS
import com.finoldigital.ygolp.presentation.constants.STARTING_LIFE_POINTS
import com.finoldigital.ygolp.presentation.enums.CalculatorMode
import com.finoldigital.ygolp.presentation.enums.Player
import com.finoldigital.ygolp.presentation.theme.AppColors

private const val MAX_OPERAND_LENGTH = 5

@Composable
fun CalculatorScreen(
    player: Player = Player.ONE,
    lifePoints: Int = STARTING_LIFE_POINTS,
    initialCalculatorMode: CalculatorMode = CalculatorMode.SET,
    onDiscard: () -> Unit = {},
    onSubmit: (Int) -> Unit = {},
) {
    val operatorAdd = stringResource(R.string.calculator_operator_add)
    val operatorSubtract = stringResource(R.string.calculator_operator_subtract)
    val operatorSet = stringResource(R.string.calculator_operator_set)
    val labelAdd = stringResource(R.string.calculator_label_add)
    val labelSubtract = stringResource(R.string.calculator_label_subtract)
    val labelSet = stringResource(R.string.calculator_label_set)
    val textHalve = stringResource(R.string.calculator_text_halve)
    val labelHalve = stringResource(R.string.calculator_label_halve)
    val textClear = stringResource(R.string.calculator_text_clear)
    val labelClear = stringResource(R.string.calculator_label_clear)
    val textDiscard = stringResource(R.string.calculator_text_discard)
    val labelDiscard = stringResource(R.string.calculator_label_discard)
    val textSubmit = stringResource(R.string.calculator_text_submit)
    val labelSubmit = stringResource(R.string.calculator_label_submit)

    val digit0 = stringResource(R.string.calculator_digit_0)
    val digit1 = stringResource(R.string.calculator_digit_1)
    val digit2 = stringResource(R.string.calculator_digit_2)
    val digit3 = stringResource(R.string.calculator_digit_3)
    val digit4 = stringResource(R.string.calculator_digit_4)
    val digit5 = stringResource(R.string.calculator_digit_5)
    val digit6 = stringResource(R.string.calculator_digit_6)
    val digit7 = stringResource(R.string.calculator_digit_7)
    val digit8 = stringResource(R.string.calculator_digit_8)
    val digit9 = stringResource(R.string.calculator_digit_9)
    val digit00 = stringResource(R.string.calculator_digit_00)
    val digit000 = stringResource(R.string.calculator_digit_000)

    val label00 = stringResource(R.string.calculator_label_00)
    val label000 = stringResource(R.string.calculator_label_000)

    var calculatorMode by remember { mutableStateOf(initialCalculatorMode) }
    val operatorTextAndColor = remember(calculatorMode, operatorAdd, operatorSubtract, operatorSet) {
        when (calculatorMode) {
            CalculatorMode.ADD -> operatorAdd to Color.Green
            CalculatorMode.SUBTRACT -> operatorSubtract to Color.Red
            else -> operatorSet to Color.Yellow
        }
    }
    var operandText by remember { mutableStateOf(digit0) }
    val result = remember(lifePoints, calculatorMode, operandText) {
        val operand = operandText.toIntOrNull() ?: 0
        when (calculatorMode) {
            CalculatorMode.ADD -> lifePoints + operand
            CalculatorMode.SUBTRACT -> lifePoints - operand
            else -> operand
        }.coerceIn(MIN_LIFE_POINTS, MAX_LIFE_POINTS)
    }

    fun append(char: String) {
        var currentText = if (operandText == digit0) "" else operandText
        currentText += char
        // Limit to MAX_OPERAND_LENGTH characters
        if (currentText.length > MAX_OPERAND_LENGTH) {
            currentText = currentText.take(MAX_OPERAND_LENGTH)
        }
        operandText =
            if (currentText.isEmpty() || currentText.toLongOrNull() == 0L) digit0 else currentText
    }

    fun pop() {
        operandText =
            if (operandText.length > 1) operandText.dropLast(1) else digit0
    }

    fun nextMode() {
        val entries = CalculatorMode.entries
        calculatorMode = entries[(calculatorMode.ordinal + 1) % entries.size]
    }

    fun submit() {
        onSubmit(result)
    }

    val operatorDescription = remember(calculatorMode, labelAdd, labelSubtract, labelSet) {
        when (calculatorMode) {
            CalculatorMode.ADD -> labelAdd
            CalculatorMode.SUBTRACT -> labelSubtract
            else -> labelSet
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
                        text = textHalve,
                        accessibilityLabel = labelHalve,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colors.primary,
                        onClick = { onSubmit(lifePoints / 2) }
                    )
                    Spacer(modifier = Modifier.weight(0.5f))
                }

                // Calculator Buttons
                // Row 1
                FlowRow(horizontalArrangement = Arrangement.Center, maxItemsInEachRow = 4) {
                    CalculatorButton(digit7, accessibilityLabel = digit7) { append(digit7) }
                    CalculatorButton(digit8, accessibilityLabel = digit8) { append(digit8) }
                    CalculatorButton(digit9, accessibilityLabel = digit9) { append(digit9) }
                    CalculatorButton(textClear, accessibilityLabel = labelClear) { pop() }
                }
                // Row 2
                FlowRow(horizontalArrangement = Arrangement.Center, maxItemsInEachRow = 4) {
                    CalculatorButton(digit4, accessibilityLabel = digit4) { append(digit4) }
                    CalculatorButton(digit5, accessibilityLabel = digit5) { append(digit5) }
                    CalculatorButton(digit6, accessibilityLabel = digit6) { append(digit6) }
                    CalculatorButton(
                        textDiscard,
                        accessibilityLabel = labelDiscard,
                        color = MaterialTheme.colors.error
                    ) { onDiscard() }
                }
                // Row 3
                FlowRow(horizontalArrangement = Arrangement.Center, maxItemsInEachRow = 4) {
                    CalculatorButton(digit1, accessibilityLabel = digit1) { append(digit1) }
                    CalculatorButton(digit2, accessibilityLabel = digit2) { append(digit2) }
                    CalculatorButton(digit3, accessibilityLabel = digit3) { append(digit3) }
                    CalculatorButton(
                        textSubmit,
                        accessibilityLabel = labelSubmit,
                        color = MaterialTheme.colors.primary
                    ) { submit() }
                }
                // Row 4
                FlowRow(horizontalArrangement = Arrangement.Center, maxItemsInEachRow = 4) {
                    CalculatorButton(digit0, accessibilityLabel = digit0) { append(digit0) }
                    CalculatorButton(digit00, accessibilityLabel = label00) { append(digit00) }
                    CalculatorButton(digit000, accessibilityLabel = label000) { append(digit000) }
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