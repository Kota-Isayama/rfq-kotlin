package com.example.economic

data class EconomicTerms(
    val payouts: List<Payout>,
) {
    init {
        require(payouts.isNotEmpty()) {
            "payouts must not be empty"
        }
    }
}
