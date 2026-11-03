package com.example.economic

enum class PeriodUnit {
    DAY, WEEK, MONTH, YEAR, TERM;
}


data class Frequency(val periodMultiplier: Int, val periodUnit: PeriodUnit) {
    init {
        require(periodMultiplier > 0) { "Period must be positive" }
        require(periodUnit != PeriodUnit.TERM || periodMultiplier == 1) { "When Unit is TERM, multiplier must be 1" }
    }
}
