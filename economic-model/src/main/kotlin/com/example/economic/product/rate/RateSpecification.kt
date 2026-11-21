package com.example.economic.product.rate

import com.example.economic.base.math.ValueSchedule
import com.example.economic.observable.index.RateIndex
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
