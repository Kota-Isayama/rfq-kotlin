package com.example.economic

sealed interface FloatingRateDetermination


enum class PeriodBoundary {
    CALCULATION_PERIOD_START_DATE,
    CALCULATION_PERIOD_END_DATE;
}


data class SingleFixing(
    val reference: PeriodBoundary,
    val fixingOffset: Int,
)


data class Compounding(
    val observationRule: ObservationRule,
    val businessCenters: Set<BusinessCenter>,
) : FloatingRateDetermination
