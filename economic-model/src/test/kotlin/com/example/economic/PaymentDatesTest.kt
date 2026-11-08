package com.example.economic

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class PaymentDatesTest {
    @Test
    fun `BusinessDayConvention must be None when Offset date type is not Business`() {
        PaymentDates(
            paymentFrequency = Frequency(periodMultiplier = 6, periodUnit = PeriodUnit.MONTH),
            payRelativeTo = PayRelativeTo.CALCULATION_PERIOD_END_DATE,
            paymentDatesAdjustments = BusinessDayAdjustments.create(
                convention = BusinessDayConvention.NONE,
                centers = setOf(),
            ),
            paymentDateOffset = PaymentDaysOffset(
                days = 2,
                dayType = DayType.CALENDAR
            ),
        )
    }

    @Test
    fun `BusinessDayConvention cannot be other than None when Offset date type is not Business`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            PaymentDates(
                paymentFrequency = Frequency(periodMultiplier = 6, periodUnit = PeriodUnit.MONTH),
                payRelativeTo = PayRelativeTo.CALCULATION_PERIOD_END_DATE,
                paymentDatesAdjustments = BusinessDayAdjustments.create(
                    convention = BusinessDayConvention.NONE,
                    centers = emptySet(),
                ),
                paymentDateOffset = PaymentDaysOffset(
                    days = 2,
                    dayType = DayType.BUSINESS
                ),
            )
        }
    }
}