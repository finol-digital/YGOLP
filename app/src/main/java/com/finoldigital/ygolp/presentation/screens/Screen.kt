package com.finoldigital.ygolp.presentation.screens

sealed class Screen(val route: String) {
    object LifePoints : Screen("lifepoints/{player}") {
        fun createRoute(player: Int) = "lifepoints/$player"
    }

    object Calculator : Screen("calculator/{player}/{initialCalculatorMode}") {
        fun createRoute(player: Int, mode: CalculatorMode) = "calculator/$player/${mode.value}"
    }
}