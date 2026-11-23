package com.example.economic.product

import com.example.economic.product.payout.InterestRatePayout
import com.example.economic.product.payout.Payout
import com.example.economic.product.termination.AccruedInterestTreatment
import com.example.economic.product.termination.KnockOutProvision

data class EconomicTerms(
    val payouts: List<Payout>,
    val knockOutProvision: KnockOutProvision?
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

        validateAccruedInterestTreatments()
    }

    private fun validateAccruedInterestTreatments() {
        if (knockOutProvision == null) {
            return  // There are no requirements when KO is not specified.
        }

        val requiredIds = payouts
            .filterIsInstance<InterestRatePayout>()
            .map { it.payoutId }
            .toSet()

        val counts = knockOutProvision.effects
            .filterIsInstance<AccruedInterestTreatment>()
            .flatMap { it.payoutIds }
            .groupingBy { it }
            .eachCount()

        require(counts.keys == requiredIds) {
            "Accrued interest treatments must cover exactly the interest rate payouts."
        }

        require(counts.values.all { it == 1}) {
            "Each interest rate payout must have exactly one accrued interest treatment."
        }
    }
}