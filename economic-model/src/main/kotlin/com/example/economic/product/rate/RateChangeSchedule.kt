package com.example.economic.product.rate

import com.example.economic.base.datetime.ScheduleFrequency
import com.example.economic.base.datetime.BusinessDayAdjustments
import com.example.economic.base.datetime.PeriodUnit

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