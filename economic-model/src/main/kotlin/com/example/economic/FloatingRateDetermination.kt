package com.example.economic

sealed interface FloatingRateDetermination


enum class PeriodBoundary {
    CALCULATION_PERIOD_START_DATE,
    CALCULATION_PERIOD_END_DATE;
}


data class SingleFixing(
    val reference: PeriodBoundary,
    val fixingDateRule: FixingDateRule,
): FloatingRateDetermination


data class Compounding(
    val observationRule: ObservationRule,
    val businessCenters: Set<BusinessCenter>,
) : FloatingRateDetermination


data class Averaging(
    val rateChangeSchedule: RateChangeSchedule,
    val fixingDateRule: FixingDateRule,
    val weighting: AveragingWeighting
): FloatingRateDetermination {
    enum class AveragingWeighting {
        EQUAL,           // 各Fixing値を等しい重みで平均
        DAYS_IN_EFFECT;  // 各Fixing値が適用される日数で加重平均
    }
}