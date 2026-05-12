package com.finoldigital.ygolp.presentation.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.finoldigital.ygolp.presentation.enums.CalculatorMode
import com.finoldigital.ygolp.presentation.enums.Player
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CalculatorScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysCurrentLifePoints() {
        composeTestRule.setContent {
            CalculatorScreen(
                player = Player.ONE,
                lifePoints = 8000,
                initialCalculatorMode = CalculatorMode.SET,
            )
        }
        composeTestRule.onNodeWithText("8000").assertIsDisplayed()
    }

    @Test
    fun digitEntryUpdatesOperand() {
        composeTestRule.setContent {
            CalculatorScreen(
                player = Player.ONE,
                lifePoints = 8000,
                initialCalculatorMode = CalculatorMode.SET,
            )
        }
        composeTestRule.onNodeWithContentDescription("5").performClick()
        composeTestRule.onNodeWithText("5").assertIsDisplayed()
    }

    @Test
    fun multipleDigitEntry() {
        composeTestRule.setContent {
            CalculatorScreen(
                player = Player.ONE,
                lifePoints = 8000,
                initialCalculatorMode = CalculatorMode.SET,
            )
        }
        composeTestRule.onNodeWithContentDescription("5").performClick()
        composeTestRule.onNodeWithContentDescription("0").performClick()
        composeTestRule.onNodeWithContentDescription("0").performClick()
        composeTestRule.onNodeWithContentDescription("0").performClick()
        composeTestRule.onNodeWithText("5000").assertIsDisplayed()
    }

    @Test
    fun submitTriggersCallbackWithResult() {
        var submittedValue = -1
        composeTestRule.setContent {
            CalculatorScreen(
                player = Player.ONE,
                lifePoints = 8000,
                initialCalculatorMode = CalculatorMode.SET,
                onSubmit = { submittedValue = it },
            )
        }
        composeTestRule.onNodeWithContentDescription("5").performClick()
        composeTestRule.onNodeWithContentDescription("Triple zero").performClick()
        composeTestRule.onNodeWithContentDescription("Submit").performClick()
        assertEquals(5000, submittedValue)
    }

    @Test
    fun modeCyclingChangesOperator() {
        composeTestRule.setContent {
            CalculatorScreen(
                player = Player.ONE,
                lifePoints = 8000,
                initialCalculatorMode = CalculatorMode.SET,
            )
        }
        // Initially in SET mode, operator shows "=>"
        composeTestRule.onNodeWithContentDescription("Set").assertIsDisplayed()
        // Tap operator to cycle to SUBTRACT
        composeTestRule.onNodeWithContentDescription("Set").performClick()
        composeTestRule.onNodeWithContentDescription("Subtract").assertIsDisplayed()
    }

    @Test
    fun addModeCalculatesCorrectly() {
        var submittedValue = -1
        composeTestRule.setContent {
            CalculatorScreen(
                player = Player.ONE,
                lifePoints = 8000,
                initialCalculatorMode = CalculatorMode.ADD,
                onSubmit = { submittedValue = it },
            )
        }
        composeTestRule.onNodeWithContentDescription("1").performClick()
        composeTestRule.onNodeWithContentDescription("Double zero").performClick()
        composeTestRule.onNodeWithContentDescription("Submit").performClick()
        assertEquals(8100, submittedValue)
    }

    @Test
    fun subtractModeCalculatesCorrectly() {
        var submittedValue = -1
        composeTestRule.setContent {
            CalculatorScreen(
                player = Player.ONE,
                lifePoints = 8000,
                initialCalculatorMode = CalculatorMode.SUBTRACT,
                onSubmit = { submittedValue = it },
            )
        }
        composeTestRule.onNodeWithContentDescription("2").performClick()
        composeTestRule.onNodeWithContentDescription("Triple zero").performClick()
        composeTestRule.onNodeWithContentDescription("Submit").performClick()
        assertEquals(6000, submittedValue)
    }
}

