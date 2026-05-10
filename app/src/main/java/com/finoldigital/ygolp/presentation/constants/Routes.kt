package com.finoldigital.ygolp.presentation.constants

import com.finoldigital.ygolp.presentation.enums.CalculatorMode
import com.finoldigital.ygolp.presentation.enums.Player

sealed class Routes(val route: String) {

    object LifePoints : Routes("lifepoints")

    object Calculator : Routes("calculator/{$PLAYER_ARG}/{$CALCULATOR_MODE_ARG}") {
        fun createRoute(player: Player, calculatorMode: CalculatorMode) = "calculator/${player.value}/${calculatorMode.value}"
    }

    companion object {
        const val PLAYER_ARG = "player"
        const val CALCULATOR_MODE_ARG = "calculatorMode"
    }
}