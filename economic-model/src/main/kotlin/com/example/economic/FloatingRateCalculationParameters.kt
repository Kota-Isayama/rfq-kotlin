package com.example.economic


sealed interface ObservationRule


object NoShift: ObservationRule


data class LookBack(val businessDays: Int): ObservationRule {
    init {
        if (businessDays == 0) {
            throw IllegalArgumentException("LookBack with zero day offset is treated as NoShift.")
        }
        else if (businessDays < 0) {
            throw IllegalArgumentException("businessDays must not be negative.")
        }
    }
}


enum class CalculationBase {
    STANDARD,
    SET_IN_ADVANCE,
    FIXING_DATE;
}

data class ObservationPeriodShift(val businessDays: Int, val calculationBase: CalculationBase): ObservationRule {
    init {
        if (businessDays == 0) {
            throw IllegalArgumentException("ObservationPeriodShift with zero day offset is treated as NoShift.")
        }
        else if (businessDays < 0) {
            throw IllegalArgumentException("businessDays must not be negative.")
        }
    }
}

data class Lockout(val businessDays: Int): ObservationRule {
    init {
        if (businessDays == 0) {
            throw IllegalArgumentException("Lockout with zero day offset is treated as NoShift.")
        }
        else if (businessDays < 0) {
            throw IllegalArgumentException("businessDays must not be negative")
        }
    }
}

data class FloatingRateCalculationParameters(
    val method: CalculationMethod,
    val observationRule: ObservationRule
)
