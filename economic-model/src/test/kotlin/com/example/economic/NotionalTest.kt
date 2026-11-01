package com.example.economic

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class NotionalTest {
    // Normal system
    @Test
    fun `Positive number can be notional amount`() {
        Notional(BigDecimal.valueOf(100000))
    }

    // Abnormal system
    @Test
    fun `Zero cannot be notional amount`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            Notional(BigDecimal.ZERO)
        }
    }

    @Test
    fun `Negative number cannot be notional amount`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            Notional(BigDecimal.valueOf(-100000))
        }
    }

    @Test
    fun `Cannot be copied with negative number`() {
        val notional = Notional(BigDecimal.valueOf(100000))
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            val invalidNotional = notional.copy(amount = BigDecimal.valueOf(-100000))
        }
    }
}