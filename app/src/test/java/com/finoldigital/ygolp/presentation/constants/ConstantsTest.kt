package com.finoldigital.ygolp.presentation.constants

import org.junit.Assert.assertEquals
import org.junit.Test

class ConstantsTest {

    @Test
    fun `MIN_LIFE_POINTS is 0`() {
        assertEquals(0, MIN_LIFE_POINTS)
    }

    @Test
    fun `MAX_LIFE_POINTS is 99999`() {
        assertEquals(99999, MAX_LIFE_POINTS)
    }

    @Test
    fun `STARTING_LIFE_POINTS is 8000`() {
        assertEquals(8000, STARTING_LIFE_POINTS)
    }
}


