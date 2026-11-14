package com.example.economic

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class EconomicTermsTest {
    @Test
    fun `Empty payout is rejected`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            EconomicTerms(
                payouts = emptyList(),
            )
        }
    }
}