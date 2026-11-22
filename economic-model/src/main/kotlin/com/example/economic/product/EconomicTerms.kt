package com.example.economic.product

import com.example.economic.product.payout.Payout

data class EconomicTerms(
    val payouts: List<Payout>,
) {
    init {
        require(payouts.isNotEmpty()) {
            "payouts must not be empty"
        }

        require(
            payouts.map {it.payoutId}.toSet().size == payouts.size
        ) {
            "Payout IDs must be unique."
        }
    }
}