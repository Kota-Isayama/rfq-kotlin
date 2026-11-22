package com.example.economic.base.datetime

import java.time.LocalDate


sealed interface DateSchedule


data class ExplicitDateSchedule(
    val unadjustedDates: List<LocalDate>,
    val dateAdjustments: BusinessDayAdjustments,
): DateSchedule {
    init {
        require(unadjustedDates.isNotEmpty()) {
            "Unadjusted dates must not be empty"
        }

        require(unadjustedDates.zipWithNext().all {
            it.first.isBefore(it.second)
        }) {
            "Unadjusted dates must be strictly increasing."
        }
    }
}


data class PeriodicDateSchedule(
    val firstUnadjustedDate: LocalDate,
    val lastUnadjustedDate: LocalDate,
    val scheduleFrequency: ScheduleFrequency,
    val dateAdjustments: BusinessDayAdjustments
): DateSchedule {
    init {
        require(firstUnadjustedDate.isBefore(lastUnadjustedDate)) {
            "First unadjusted date must be before last date"
        }

        require(scheduleFrequency.frequency.periodUnit != PeriodUnit.TERM) {
            "Schedule frequency must not be equal to TERM"
        }
    }
}