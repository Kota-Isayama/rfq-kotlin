package com.example.economic

import java.math.BigDecimal

data class Notional(val amount: BigDecimal) {
    init {
        if (amount <= BigDecimal.ZERO) {
            throw IllegalArgumentException("Notional amount must be greater than zero.")
        }
    }
}
