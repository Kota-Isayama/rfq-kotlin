package com.example.economic.base.datetime;

import com.example.economic.product.termination.RelativePaymentDate
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test;

public class RelativeDateTest {
    @Test
    fun `RelativeDateRule rejects business day offset without business centers`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            RelativeDateRule(
                offset = DaysOffset(
                    days = 2,
                    dayType = DayType.BUSINESS
                ),
                adjustments = noAdjustment()
            )
        }
    }

    @Test
    fun `RelativeDateRule accepts no offset`() {
        RelativeDateRule(
            offset = null,
            adjustments = noAdjustment()
        )
    }

    @Test
    fun `RelativeDateRule accepts calendar day offset without business centers`() {
        RelativeDateRule(
            offset = DaysOffset(
                    days = 2,
                    dayType = DayType.CALENDAR
            ),
            adjustments = noAdjustment()
        )
    }

    @Test
    fun `RelativeDateRule accepts business day offset with business centers`() {
        RelativeDateRule(
            offset = DaysOffset(
                    days = 2,
                    dayType = DayType.BUSINESS
            ),
            adjustments = BusinessDayAdjustments.create(
                    convention = BusinessDayConvention.FOLLOWING,
                    centers = setOf(BusinessCenter("JPTO"))
            )
        )
    }

    private fun noAdjustment() =
            BusinessDayAdjustments.create(
                convention = BusinessDayConvention.NONE,
                centers = emptySet()
        )
}
