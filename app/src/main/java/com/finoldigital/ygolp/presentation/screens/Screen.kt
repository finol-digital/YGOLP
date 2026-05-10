package com.finoldigital.ygolp.presentation.screens

import com.finoldigital.ygolp.presentation.enums.CalculatorMode
import com.finoldigital.ygolp.presentation.enums.Player

sealed class Screen(val route: String) {
    object LifePoints : Screen("lifepoints")

    object Calculator : Screen("calculator/{player}/{calculatorMode}") {
        fun createRoute(player: Player, calculatorMode: CalculatorMode) = "calculator/${player.value}/${calculatorMode.value}"
    }
}