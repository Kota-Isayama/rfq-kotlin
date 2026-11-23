package com.example.economic.product.termination

import com.example.economic.base.datetime.DateSchedule
import com.example.economic.base.math.Currency
import com.example.economic.base.math.Dimensionless
import com.example.economic.base.math.Measure
import com.example.economic.base.math.MeasureSchedule
import com.example.economic.base.math.QuantitySchedule
import com.example.economic.base.math.ValueSchedule
import com.example.economic.observable.ForeignExchangeRateIndex
import com.example.economic.observable.event.Trigger
import java.math.BigDecimal

sealed interface TargetAccumulationRule


//data class AmountTarget(
//    val observationSchedule: DateSchedule,
//    val contributionRule:
//)


data class CountTarget(
    val observationSchedule: DateSchedule,
    val qualifyingCondition: Trigger,
    val targetCount: Int,
): TargetAccumulationRule {
    init {
        require(targetCount > 0) { "targetCount must be greater than 0" }
    }
}


enum class FxDifferenceDirection {
    INDEX_MINUS_STRIKE,
    STRIKE_MINUS_INDEX;
}

data class FxDifferenceContribution(
    val index: ForeignExchangeRateIndex,
    val strike: ValueSchedule<BigDecimal>,
    val notional: QuantitySchedule<Currency, Dimensionless>,
    val direction: FxDifferenceDirection,
) {
    init {
        require(notional.measure.unit == index.currencyPair.baseCurrency) {
            "Notional currency must be base currency"
        }
    }
}


data class AmountTarget(
    val observationSchedule: DateSchedule,
    val contributionRule: FxDifferenceContribution,
    val targetAmount: Measure<Currency>
): TargetAccumulationRule {
    init {
        require(
            contributionRule.index.currencyPair.quoteCurrency == targetAmount.unit
        ) {
            "Target Amount currency must be quote currency."
        }

        require(targetAmount.value > BigDecimal.ZERO) {
            "Target Amount must be positive."
        }
    }
}
