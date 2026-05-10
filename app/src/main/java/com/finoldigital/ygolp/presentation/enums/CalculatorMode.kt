package com.finoldigital.ygolp.presentation.enums

enum class CalculatorMode(val value: Int) {
    SET(0),
    SUBTRACT(1),
    ADD(2);

    companion object {
        fun fromInt(value: Int) = entries.find { it.value == value } ?: SET
    }
}