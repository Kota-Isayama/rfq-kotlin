package com.example.economic.product.termination

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TerminationEffectTest {

    @Test
    fun `WaiveAccruedInterest rejects empty payout IDs`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            WaiveAccruedInterest(
                payoutIds = emptySet()
            )
        }
    }

    @Test
    fun `PayAccruedInterest rejects empty payout IDs`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            PayAccruedInterest(
                payoutIds = emptySet(),
                paymentDateRule = OriginalPaymentDate
            )
        }
    }


}