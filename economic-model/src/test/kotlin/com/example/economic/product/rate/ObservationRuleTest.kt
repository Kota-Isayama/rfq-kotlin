package com.example.economic.product.rate

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ObservationRuleTest {
    @Test
    fun `Zero day offset is forbidden for look back`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            LookBack(0)
        }
    }

    @Test
    fun `Negative day offset is forbidden for look back`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            LookBack(-1)
        }
    }

    @Test
    fun `Zero day offset is forbidden for observation period shift`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            ObservationPeriodShift(0, CalculationBase.STANDARD)
        }
    }

    @Test
    fun `Negative day offset is forbidden for observation period shift`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            ObservationPeriodShift(-1, CalculationBase.STANDARD)
        }
    }

    @Test
    fun `Zero day offset is forbidden for lockout`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            Lockout(0)
        }
    }

    @Test
    fun `Negative day offset is forbidden for lockout`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            Lockout(-1)
        }
    }
}