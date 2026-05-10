package com.finoldigital.ygolp.presentation.screens

enum class CalculatorMode(val value: Int) {
    SET(0),
    SUBTRACT(1),
    ADD(2);

    companion object {
        fun fromInt(value: Int) = entries.find { it.value == value } ?: SET
    }
}