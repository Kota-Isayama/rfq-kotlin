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
            else -> require(rollConvention != NoRoll) {
                "When frequency is other than TERM, Roll must exist."
            }
        }
    }
}
