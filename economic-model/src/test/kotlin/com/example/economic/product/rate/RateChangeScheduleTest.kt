package com.example.economic.product.rate

import com.example.economic.base.datetime.BusinessDayAdjustments
import com.example.economic.base.datetime.BusinessDayConvention
import com.example.economic.base.datetime.DayOfWeekRoll
import com.example.economic.base.datetime.Frequency
import com.example.economic.base.datetime.PeriodUnit
import com.example.economic.base.datetime.ScheduleFrequency
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