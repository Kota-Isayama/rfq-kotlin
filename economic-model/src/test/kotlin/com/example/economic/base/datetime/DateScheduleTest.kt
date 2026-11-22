package com.example.economic.base.datetime

import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.time.LocalDate

class DateScheduleTest {

    private val dateAdjustments = BusinessDayAdjustments.create(
        BusinessDayConvention.NONE,
        emptySet(),
    )

    private val firstDate = LocalDate.of(2027, 1, 15)
    private val secondDate = LocalDate.of(2027, 2, 15)
    private val lastDate = LocalDate.of(2027, 3, 15)

    private val monthlyFrequency = ScheduleFrequency(
        frequency = Frequency(periodMultiplier = 1, periodUnit = PeriodUnit.MONTH),
        rollConvention = DayOfMonth(15),
    )

    @Test
    fun `Explicit schedule accepts a single observation date`() {
        ExplicitDateSchedule(
            unadjustedDates = listOf(firstDate),
            dateAdjustments = dateAdjustments,
        )
    }

    @Test
    fun `Explicit schedule accepts strictly increasing dates`() {
        ExplicitDateSchedule(
            unadjustedDates = listOf(firstDate, secondDate, lastDate),
            dateAdjustments = dateAdjustments,
        )
    }

    @Test
    fun `Explicit schedule rejects empty dates`() {
        assertThrows(IllegalArgumentException::class.java) {
            ExplicitDateSchedule(
                unadjustedDates = emptyList(),
                dateAdjustments = dateAdjustments,
            )
        }
    }

    @Test
    fun `Explicit schedule rejects duplicate dates`() {
        assertThrows(IllegalArgumentException::class.java) {
            ExplicitDateSchedule(
                unadjustedDates = listOf(firstDate, secondDate, secondDate),
                dateAdjustments = dateAdjustments,
            )
        }
    }

    @Test
    fun `Explicit schedule rejects dates that are not strictly increasing`() {
        assertThrows(IllegalArgumentException::class.java) {
            ExplicitDateSchedule(
                unadjustedDates = listOf(firstDate, lastDate, secondDate),
                dateAdjustments = dateAdjustments,
            )
        }
    }

    @Test
    fun `Periodic schedule accepts increasing dates with monthly frequency`() {
        PeriodicDateSchedule(
            firstUnadjustedDate = firstDate,
            lastUnadjustedDate = lastDate,
            scheduleFrequency = monthlyFrequency,
            dateAdjustments = dateAdjustments,
        )
    }

    @Test
    fun `Periodic schedule rejects identical first and last dates`() {
        assertThrows(IllegalArgumentException::class.java) {
            PeriodicDateSchedule(
                firstUnadjustedDate = firstDate,
                lastUnadjustedDate = firstDate,
                scheduleFrequency = monthlyFrequency,
                dateAdjustments = dateAdjustments,
            )
        }
    }

    @Test
    fun `Periodic schedule rejects first date after last date`() {
        assertThrows(IllegalArgumentException::class.java) {
            PeriodicDateSchedule(
                firstUnadjustedDate = lastDate,
                lastUnadjustedDate = firstDate,
                scheduleFrequency = monthlyFrequency,
                dateAdjustments = dateAdjustments,
            )
        }
    }

    @Test
    fun `Periodic schedule rejects term frequency`() {
        val termFrequency = ScheduleFrequency(
            frequency = Frequency(periodMultiplier = 1, periodUnit = PeriodUnit.TERM),
            rollConvention = NoRoll,
        )

        assertThrows(IllegalArgumentException::class.java) {
            PeriodicDateSchedule(
                firstUnadjustedDate = firstDate,
                lastUnadjustedDate = lastDate,
                scheduleFrequency = termFrequency,
                dateAdjustments = dateAdjustments,
            )
        }
    }
}
