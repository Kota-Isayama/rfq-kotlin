package com.example.economic

import java.math.BigDecimal

sealed interface Payout


data class InterestRatePayout(
    val payerReceiver: PayerReceiver,
    val notional: QuantitySchedule<Currency, Dimensionless>,
    val calculationPeriodDates: CalculationPeriodDates,
    val paymentDates: PaymentDates,
    val dayCountFraction: DayCountFraction,
    val rateSpecification: RateSpecification,
): Payout {
    init {
        require(is_notional_always_positive()) {
            "Notional must be always positive in the terms."
        }
    }

    private fun is_notional_always_positive(): Boolean {
        return when (notional.measure.valueSchedule) {
            is Constant<BigDecimal> -> {
                notional.measure.valueSchedule.value > BigDecimal.ZERO
            }
            is DateBasedSchedule<BigDecimal> -> {
                notional.measure.valueSchedule.initialValue > BigDecimal.ZERO
                        && notional.measure.valueSchedule.steps.all { it.value > BigDecimal.ZERO }
            }
            is PeriodBasedSchedule<BigDecimal> -> {
                notional.measure.valueSchedule.initialValue > BigDecimal.ZERO
                        && notional.measure.valueSchedule.steps.all { it.value > BigDecimal.ZERO }
            }
        }
    }
}
