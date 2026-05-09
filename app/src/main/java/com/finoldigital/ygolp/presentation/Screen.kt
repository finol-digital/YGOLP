package com.finoldigital.ygolp.presentation

sealed class Screen(val route: String) {
    object LifePoints : Screen("lifepoints/{player}") {
        fun createRoute(player: Int) = "lifepoints/$player"
    }

    object Calculator : Screen("calculator/{player}/{initialCalculatorMode}") {
        fun createRoute(player: Int, mode: Int) = "calculator/$player/$mode"
    }
}
