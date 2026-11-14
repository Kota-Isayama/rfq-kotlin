package com.example.economic

data class RateChangeSchedule(
    val scheduleFrequency: ScheduleFrequency,
    val adjustments: BusinessDayAdjustments
) {
    init {
        require(scheduleFrequency.frequency.periodUnit == PeriodUnit.WEEK && scheduleFrequency.frequency.periodMultiplier == 1) {
            "As for now, only 1 week is supported."
        }
    }
}
