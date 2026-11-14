package com.example.economic

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ScheduleFrequencyTest {
    @Test
    fun `Accepts term frequency without roll convention`() {
        ScheduleFrequency(
            Frequency(periodMultiplier = 1, periodUnit = PeriodUnit.TERM),
            rollConvention = NoRoll
        )
    }

    @ParameterizedTest
    @MethodSource("normalRollConventionSource")
    fun `Term Frequency cannot roll`(rollConvention: RollConvention) {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            ScheduleFrequency(
                Frequency(periodUnit = PeriodUnit.TERM, periodMultiplier = 1),
                rollConvention = rollConvention,
            )
        }
    }

    private fun normalRollConventionSource() = listOf(
        DayOfMonth(1),
        EndOfMonth,
    )

    @Test
    fun `Accepts weekly frequency with week of day roll`() {
        ScheduleFrequency(
            Frequency(periodMultiplier = 1, periodUnit = PeriodUnit.WEEK),
            rollConvention = DayOfWeekRoll(DayOfWeek.MONDAY),
        )
    }

    @Test
    fun `Rejects weekly frequency with of day of month roll`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            ScheduleFrequency(
                Frequency(periodMultiplier = 1, periodUnit = PeriodUnit.WEEK),
                rollConvention = DayOfMonth(15),
            )
        }
    }

    @Test
    fun `Rejects weekly frequency end of month roll`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            ScheduleFrequency(
                Frequency(periodMultiplier = 1, periodUnit = PeriodUnit.WEEK),
                rollConvention = EndOfMonth,
            )
        }
    }

    @Test
    fun `Weekly Frequency must have roll convention`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            ScheduleFrequency(
                Frequency(periodMultiplier = 1, periodUnit = PeriodUnit.WEEK),
                rollConvention = NoRoll,
            )
        }
    }

    @Test
    fun `Accepts monthly frequency with day of month roll`() {
        ScheduleFrequency(
            Frequency(periodMultiplier = 1, periodUnit = PeriodUnit.MONTH),
            rollConvention = DayOfMonth(15)
        )
    }

    @Test
    fun `Accepts monthly frequency with end of month roll`() {
        ScheduleFrequency(
            Frequency(periodMultiplier = 1, periodUnit = PeriodUnit.MONTH),
            rollConvention = EndOfMonth
        )
    }

    @Test
    fun `accepts yearly frequency with end of month roll`() {
        ScheduleFrequency(
            Frequency(1, PeriodUnit.YEAR),
            EndOfMonth,
        )
    }

    @Test
    fun `Normal Frequency must have roll convention`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            ScheduleFrequency(
                Frequency(periodUnit = PeriodUnit.YEAR, periodMultiplier = 1),
                rollConvention = NoRoll,
            )
        }
    }

    @Test
    fun `Monthly Frequency rejects week of day roll`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            ScheduleFrequency(
                frequency = Frequency(periodMultiplier = 1, periodUnit = PeriodUnit.MONTH),
                rollConvention = DayOfWeekRoll(DayOfWeek.MONDAY),
            )
        }
    }

    @Test
    fun `Yearly frequency rejects week of day roll`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            ScheduleFrequency(
                frequency = Frequency(periodMultiplier = 1, periodUnit = PeriodUnit.YEAR),
                rollConvention = DayOfWeekRoll(DayOfWeek.MONDAY),
            )
        }
    }
}