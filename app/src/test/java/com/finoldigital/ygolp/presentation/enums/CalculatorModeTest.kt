package com.finoldigital.ygolp.presentation.enums

import org.junit.Assert.assertEquals
import org.junit.Test

class CalculatorModeTest {

    @Test
    fun `fromInt 0 returns SET`() {
        assertEquals(CalculatorMode.SET, CalculatorMode.fromInt(0))
    }

    @Test
    fun `fromInt 1 returns SUBTRACT`() {
        assertEquals(CalculatorMode.SUBTRACT, CalculatorMode.fromInt(1))
    }

    @Test
    fun `fromInt 2 returns ADD`() {
        assertEquals(CalculatorMode.ADD, CalculatorMode.fromInt(2))
    }

    @Test
    fun `fromInt unknown value defaults to SET`() {
        assertEquals(CalculatorMode.SET, CalculatorMode.fromInt(99))
    }

    @Test
    fun `fromInt negative value defaults to SET`() {
        assertEquals(CalculatorMode.SET, CalculatorMode.fromInt(-1))
    }
}


