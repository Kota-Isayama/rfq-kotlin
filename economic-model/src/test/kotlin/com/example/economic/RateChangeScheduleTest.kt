package com.example.economic

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.DayOfWeek

class RateChangeScheduleTest {
    @Test
    fun `One week frequency is supported`() {
        RateChangeSchedule(
            scheduleFrequency = ScheduleFrequency(
                frequency = Frequency(periodMultiplier = 1, periodUnit = PeriodUnit.WEEK),
                rollConvention = DayOfWeekRoll(DayOfWeek.MONDAY),
            ),
            adjustments = BusinessDayAdjustments.create(
                convention = BusinessDayConvention.NONE,
                centers = setOf(),
            )
        )
    }

    @Test
    fun `Frequency other than 1 week`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            RateChangeSchedule(
                scheduleFrequency = ScheduleFrequency(
                    frequency = Frequency(periodMultiplier = 2, periodUnit = PeriodUnit.WEEK),
                    rollConvention = DayOfWeekRoll(DayOfWeek.MONDAY),
                ),
                adjustments = BusinessDayAdjustments.create(
                    convention = BusinessDayConvention.NONE,
                    centers = setOf(),
                )
            )
        }
    }
}