package com.example.economic

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class RollConventionTest {
    @Test
    fun `Zero day cannot be day of roll`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            DayOfMonth(0)
        }
    }

    @Test
    fun `31 cannot be day of roll`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            DayOfMonth(31)
        }
    }
}