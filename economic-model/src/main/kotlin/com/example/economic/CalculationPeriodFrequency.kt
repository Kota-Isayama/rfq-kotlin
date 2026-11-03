package com.example.economic

data class CalculationPeriodFrequency(
    val frequency: Frequency,
    val rollConvention: RollConvention
) {
    init {
        when(frequency.periodUnit) {
            PeriodUnit.TERM -> require(rollConvention == NoRoll) {
                "When frequency is 1TERM, Roll does not exist."
            }
            PeriodUnit.MONTH -> require(rollConvention != NoRoll) {
                "When frequency is MONTH, Roll must exist."
            }

            PeriodUnit.YEAR -> require(rollConvention != NoRoll) {
                "When frequency is YEAR, Roll must exist."
            }
        }
    }
}
