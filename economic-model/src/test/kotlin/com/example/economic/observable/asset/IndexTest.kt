package com.example.economic.observable.asset

import com.example.economic.base.math.Currency
import com.example.economic.observable.CurrencyPair
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class IndexTest {
    @Test
    fun `CurrencyPair reject same currencies pair`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            CurrencyPair(
                baseCurrency = Currency.JPY,
                quoteCurrency = Currency.JPY,
            )
        }
    }
}