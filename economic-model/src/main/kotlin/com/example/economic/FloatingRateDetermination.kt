package com.example.economic

import java.time.LocalDate

sealed interface FloatingRateDetermination


enum class PeriodBoundary {
    CALCULATION_PERIOD_START_DATE,
    CALCULATION_PERIOD_END_DATE;
}


data class SingleFixing(
    val reference: PeriodBoundary,
    val fixingDate: LocalDate,
): FloatingRateDetermination


data class Compounding(
    val observationRule: ObservationRule,
    val businessCenters: Set<BusinessCenter>,
) : FloatingRateDetermination
