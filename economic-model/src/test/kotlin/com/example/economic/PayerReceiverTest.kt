package com.example.economic

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class PayerReceiverTest {
    @ParameterizedTest
    @EnumSource(CounterpartyRole::class)
    fun `Payer and Receiver cannot be the same`(role: CounterpartyRole) {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            PayerReceiver(
                payer = role,
                receiver = role
            )
        }
    }
}