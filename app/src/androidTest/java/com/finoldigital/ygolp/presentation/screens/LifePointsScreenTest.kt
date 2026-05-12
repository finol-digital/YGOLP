package com.finoldigital.ygolp.presentation.screens

import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class LifePointsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysDefaultLifePoints() {
        composeTestRule.setContent {
            val pagerState = rememberPagerState(pageCount = { 2 })
            LifePointsScreen(
                pagerState = pagerState,
                displayedLifePoints1 = 8000,
                displayedLifePoints2 = 8000,
            )
        }
        composeTestRule.onNodeWithText("8000").assertIsDisplayed()
    }

    @Test
    fun displaysCustomLifePoints() {
        composeTestRule.setContent {
            val pagerState = rememberPagerState(pageCount = { 2 })
            LifePointsScreen(
                pagerState = pagerState,
                displayedLifePoints1 = 5000,
                displayedLifePoints2 = 3000,
            )
        }
        composeTestRule.onNodeWithText("5000").assertIsDisplayed()
    }

    @Test
    fun showsMuteIconWhenNotMuted() {
        composeTestRule.setContent {
            val pagerState = rememberPagerState(pageCount = { 2 })
            LifePointsScreen(
                pagerState = pagerState,
                isMuted = false,
            )
        }
        composeTestRule.onNodeWithContentDescription("Mute").assertIsDisplayed()
    }

    @Test
    fun showsUnmuteIconWhenMuted() {
        composeTestRule.setContent {
            val pagerState = rememberPagerState(pageCount = { 2 })
            LifePointsScreen(
                pagerState = pagerState,
                isMuted = true,
            )
        }
        composeTestRule.onNodeWithContentDescription("Unmute").assertIsDisplayed()
    }
}

