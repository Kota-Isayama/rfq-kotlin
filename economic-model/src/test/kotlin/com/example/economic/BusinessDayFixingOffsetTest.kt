package com.example.economic

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class BusinessDayFixingOffsetTest {
    @Test
    fun `Zero day offset is forbidden`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            BusinessDayFixingOffset(
                offset = 0,
                businessCenters = setOf(),
            )
        }
    }
}