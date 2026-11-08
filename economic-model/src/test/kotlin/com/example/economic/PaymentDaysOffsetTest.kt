package com.example.economic

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


class PaymentDaysOffsetTest {
    @Test
    fun `Zero is not valid days offset`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            PaymentDaysOffset(
                days = 0,
                dayType = DayType.BUSINESS
            )
        }
    }
}