package com.example.economic

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDate

class CalculationPeriodDatesTest {
    @Test
    fun `Effective Date cannot be after Termination date`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            CalculationPeriodDates(
                unadjustedEffectiveDate = LocalDate.parse("2020-01-02"),
                unadjustedTerminationDate = LocalDate.parse("2020-01-01"),
                scheduleFrequency = ScheduleFrequency(
                    frequency = Frequency(1, PeriodUnit.MONTH),
                    rollConvention = DayOfMonth(day=1)
                ),
                calculationPeriodDatesAdjustments = BusinessDayAdjustments.create(
                    convention=BusinessDayConvention.MODFOLLOWING,
                    centers = setOf(BusinessCenter("JPTO")),
                ),
                stub= NoneStub,
            )
        }
    }

    @Test
    fun `Effective Date cannot be after First Regular Period Start Date`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            CalculationPeriodDates(
                unadjustedEffectiveDate = LocalDate.parse("2020-01-01"),
                unadjustedTerminationDate = LocalDate.parse("2021-02-01"),
                scheduleFrequency = ScheduleFrequency(
                    frequency = Frequency(1, PeriodUnit.MONTH),
                    rollConvention = DayOfMonth(day=1)
                ),
                calculationPeriodDatesAdjustments = BusinessDayAdjustments.create(
                    convention=BusinessDayConvention.MODFOLLOWING,
                    centers = setOf(BusinessCenter("JPTO")),
                ),
                stub= BothStub(
                    unadjustedFirstRegularPeriodStartDate = LocalDate.parse("2019-12-01"),
                    unadjustedLastRegularPeriodEndDate = LocalDate.parse("2021-01-01"),
                ),
            )
        }
    }

    @Test
    fun `Termination Date cannot be before Last Regular Period End Date`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            CalculationPeriodDates(
                unadjustedEffectiveDate = LocalDate.parse("2020-01-01"),
                unadjustedTerminationDate = LocalDate.parse("2021-02-01"),
                scheduleFrequency = ScheduleFrequency(
                    frequency = Frequency(1, PeriodUnit.MONTH),
                    rollConvention = DayOfMonth(day=1)
                ),
                calculationPeriodDatesAdjustments = BusinessDayAdjustments.create(
                    convention=BusinessDayConvention.MODFOLLOWING,
                    centers = setOf(BusinessCenter("JPTO")),
                ),
                stub= BothStub(
                    unadjustedFirstRegularPeriodStartDate = LocalDate.parse("2020-02-01"),
                    unadjustedLastRegularPeriodEndDate = LocalDate.parse("2021-02-02"),
                ),
            )
        }
    }
}

