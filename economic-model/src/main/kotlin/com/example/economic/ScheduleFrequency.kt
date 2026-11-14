package com.example.economic

data class ScheduleFrequency(
    val frequency: Frequency,
    val rollConvention: RollConvention
) {
    init {
        when(frequency.periodUnit) {
            PeriodUnit.TERM -> require(rollConvention == NoRoll) {
                "When frequency is 1TERM, Roll does not exist."
            }

            PeriodUnit.WEEK -> {
                require(rollConvention is DayOfWeekRoll) {
                    "When frequency is WEEK, DayOfWeekRoll must be specified."
                }
            }

            PeriodUnit.MONTH -> {
                require(rollConvention != NoRoll) {
                    "When frequency is MONTH, Roll must exist."
                }

                require(!(rollConvention is DayOfWeekRoll)) {
                    "When frequency is MONTH, Roll cannot be DayOfWeek."
                }
            }

            PeriodUnit.YEAR -> {
                require(rollConvention != NoRoll) {
                    "When frequency is YEAR, Roll must exist."
                }

                require(!(rollConvention is DayOfWeekRoll)) {
                    "When frequency is YEAR, Roll cannot be DayOfWeek."
                }
            }
        }
    }
}
