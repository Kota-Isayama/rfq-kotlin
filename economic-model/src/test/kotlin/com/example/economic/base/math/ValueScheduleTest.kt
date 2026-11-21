package com.example.economic.base.math

import com.example.economic.base.datetime.BusinessDayAdjustments
import com.example.economic.base.datetime.BusinessDayConvention
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.math.BigDecimal
import java.time.LocalDate

class ValueScheduleTest {
    private val initialValue = BigDecimal("0.01")
    private val noAdjustment = BusinessDayAdjustments.create(
        convention = BusinessDayConvention.NONE,
        centers = emptySet(),
    )

    private fun dateSchedule(vararg dates: String): DateBasedSchedule<BigDecimal> =
        DateBasedSchedule(
            initialValue = initialValue,
            steps = dates.mapIndexed { index, date ->
                DateBasedSchedule.DatedStep(
                    date = LocalDate.parse(date),
                    value = BigDecimal.valueOf(index.toLong() + 2, 2),
                )
            },
            stepDateAdjustments = noAdjustment,
        )

    private fun periodSchedule(vararg periods: Int): PeriodBasedSchedule<BigDecimal> =
        PeriodBasedSchedule(
            initialValue = initialValue,
            steps = periods.mapIndexed { index, period ->
                PeriodBasedSchedule.PeriodStep(
                    fromCalculationPeriodNumber = period,
                    value = BigDecimal.valueOf(index.toLong() + 2, 2),
                )
            },
        )

    @Test
    fun `Date based schedule accepts a single step`() {
        dateSchedule("2027-06-01")
    }

    @Test
    fun `Date based schedule accepts strictly increasing dates`() {
        dateSchedule("2027-06-01", "2027-09-01", "2027-12-01")
    }

    @Test
    fun `Date based schedule rejects empty steps`() {
        assertThrows<IllegalArgumentException> {
            dateSchedule()
        }
    }

    @Test
    fun `Date based schedule rejects duplicate dates`() {
        assertThrows<IllegalArgumentException> {
            dateSchedule("2027-06-01", "2027-06-01")
        }
    }

    @Test
    fun `Date based schedule rejects dates out of order`() {
        assertThrows<IllegalArgumentException> {
            dateSchedule("2027-06-01", "2027-12-01", "2027-09-01")
        }
    }

    @Test
    fun `Period based schedule accepts a single step from period two`() {
        periodSchedule(2)
    }

    @Test
    fun `Period based schedule accepts strictly increasing period numbers with gaps`() {
        periodSchedule(2, 4, 7)
    }

    @Test
    fun `Period based schedule rejects empty steps`() {
        assertThrows<IllegalArgumentException> {
            periodSchedule()
        }
    }

    @Test
    fun `Period based schedule rejects duplicate period numbers`() {
        assertThrows<IllegalArgumentException> {
            periodSchedule(2, 2)
        }
    }

    @Test
    fun `Period based schedule rejects period numbers out of order`() {
        assertThrows<IllegalArgumentException> {
            periodSchedule(2, 7, 4)
        }
    }

    @ParameterizedTest
    @ValueSource(ints = [-1, 0, 1])
    fun `Period step rejects period numbers below two`(period: Int) {
        assertThrows<IllegalArgumentException> {
            PeriodBasedSchedule.PeriodStep(
                fromCalculationPeriodNumber = period,
                value = BigDecimal("0.02"),
            )
        }
    }
}