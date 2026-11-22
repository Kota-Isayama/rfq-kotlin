package com.example.economic.product.payout

import com.example.economic.product.rate.RateSpecification
import com.example.economic.base.datetime.DayCountFraction
import com.example.economic.base.math.Constant
import com.example.economic.base.math.Currency
import com.example.economic.base.math.DateBasedSchedule
import com.example.economic.base.math.Dimensionless
import com.example.economic.base.math.PeriodBasedSchedule
import com.example.economic.base.math.QuantitySchedule
import com.example.economic.base.party.PayerReceiver
import com.example.economic.product.schedule.CalculationPeriodDates
import com.example.economic.product.schedule.PaymentDates
import java.math.BigDecimal


@JvmInline
value class PayoutId(val value: String) {
    init {
        require(value.isNotEmpty()) {
            "PayoutId must not be empty"
        }
    }
}


sealed interface Payout {
    val payoutId: PayoutId
}


data class InterestRatePayout(
    override val payoutId: PayoutId,
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
