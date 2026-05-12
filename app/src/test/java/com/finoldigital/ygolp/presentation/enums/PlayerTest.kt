package com.finoldigital.ygolp.presentation.enums

import org.junit.Assert.assertEquals
import org.junit.Test

class PlayerTest {

    @Test
    fun `fromInt 1 returns ONE`() {
        assertEquals(Player.ONE, Player.fromInt(1))
    }

    @Test
    fun `fromInt 2 returns TWO`() {
        assertEquals(Player.TWO, Player.fromInt(2))
    }

    @Test
    fun `fromInt unknown value defaults to ONE`() {
        assertEquals(Player.ONE, Player.fromInt(99))
    }

    @Test
    fun `fromInt negative value defaults to ONE`() {
        assertEquals(Player.ONE, Player.fromInt(-1))
    }
}


