package com.example.economic

import java.math.BigDecimal

sealed interface Payout


data class InterestRatePayout(
    val payerReceiver: PayerReceiver,
    val notional: QuantitySchedule<Currency>,
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
        when (notional.measure.valueSchedule) {
            is Constant<BigDecimal> -> {
                return notional.measure.valueSchedule.value > BigDecimal.ZERO
            }
            is DateBasedSchedule<BigDecimal> -> {
                var value = notional.measure.valueSchedule.initialValue
                if (value <= BigDecimal.ZERO) {
                    return false
                }

                for (step in notional.measure.valueSchedule.steps) {
                    value += step.value
                    if (value <= BigDecimal.ZERO) {
                        return false
                    }
                }

                return true
            }
            is PeriodBasedSchedule<BigDecimal> -> {
                var value = notional.measure.valueSchedule.initialValue
                if (value <= BigDecimal.ZERO) {
                    return false
                }

                for (step in notional.measure.valueSchedule.steps) {
                    value += step.value
                    if (value <= BigDecimal.ZERO) {
                        return false
                    }
                }

                return true
            }
        }
    }
}
