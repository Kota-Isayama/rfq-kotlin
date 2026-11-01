package com.example.economic

import java.math.BigDecimal

sealed interface RateSpecification


data class FixedRateSpecification(val rate: BigDecimal): RateSpecification


data class  FloatingRateSpecification(
    val index: RateIndex,
    val spread: BigDecimal,
    val calculationMethod: CalculationMethod
): RateSpecification
