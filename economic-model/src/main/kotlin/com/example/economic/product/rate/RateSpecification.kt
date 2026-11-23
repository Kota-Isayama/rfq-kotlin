package com.example.economic.product.rate

import com.example.economic.base.math.ValueSchedule
import com.example.economic.observable.Index
import com.example.economic.observable.RateIndex
import java.math.BigDecimal

sealed interface RateSpecification


data class FixedRateSpecification(val rate: ValueSchedule<BigDecimal>): RateSpecification


data class  FloatingRateSpecification(
    val index: RateIndex,

    val spread: ValueSchedule<BigDecimal>,
    val multiplier: ValueSchedule<BigDecimal>,
    val cap: ValueSchedule<BigDecimal>?,
    val floor: ValueSchedule<BigDecimal>?,
    val floatingRateDetermination: FloatingRateDetermination
): RateSpecification


data class RateRegion(
    val upperExclusive: BigDecimal,
    val rate: ValueSchedule<BigDecimal>,
)

data class ThresholdRateSpecification(
    val index: Index,

    val fixing: DigitalRateFixing,
    val regions: List<RateRegion>,
    val otherwiseRate: ValueSchedule<BigDecimal>,
): RateSpecification {
    init {
        require(regions.isNotEmpty()) {
            "Regions must not be empty"
        }

        require(
            regions.zipWithNext().all{it.first.upperExclusive < it.second.upperExclusive}
        ) {
            "Region upper bound must be strongly increasing."
        }
    }
}
