package com.example.economic.base.datetime

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class DaysOffsetTest {
    @Test
    fun `Zero is not valid days offset`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            DaysOffset(
                days = 0,
                dayType = DayType.BUSINESS
            )
        }
    }
}