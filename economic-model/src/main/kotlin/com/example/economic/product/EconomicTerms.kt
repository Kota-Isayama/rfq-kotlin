package com.example.economic.product

import com.example.economic.product.payout.Payout

data class EconomicTerms(
    val payouts: List<Payout>,
) {
    init {
        require(payouts.isNotEmpty()) {
            "payouts must not be empty"
        }
    }
}