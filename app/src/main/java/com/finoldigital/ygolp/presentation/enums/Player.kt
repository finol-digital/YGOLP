package com.finoldigital.ygolp.presentation.enums

enum class Player(val value: Int) {
    ONE(1),
    TWO(2);

    companion object {
        fun fromInt(value: Int) = entries.find { it.value == value } ?: ONE
    }
}