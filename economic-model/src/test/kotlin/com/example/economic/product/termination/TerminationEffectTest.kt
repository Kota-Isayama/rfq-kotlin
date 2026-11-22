package com.example.economic.product.termination

import com.example.economic.base.datetime.BusinessCenter
import com.example.economic.base.datetime.BusinessDayAdjustments
import com.example.economic.base.datetime.BusinessDayConvention
import com.example.economic.product.payout.PayoutId
import com.example.economic.product.schedule.DayType
import com.example.economic.product.schedule.PaymentDaysOffset
import com.example.economic.product.termination.RelativePaymentDate.AccruedInterestPaymentDateRelativeTo
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TerminationEffectTest {

    @Test
    fun `WaiveAccruedInterest rejects empty payout IDs`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            WaiveAccruedInterest(
                payoutIds = emptySet()
            )
        }
    }

    @Test
    fun `PayAccruedInterest rejects empty payout IDs`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            PayAccruedInterest(
                payoutIds = emptySet(),
                paymentDateRule = OriginalPaymentDate
            )
        }
    }

    @Test
    fun `RelativePaymentDate rejects business day offset without business centers`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            RelativePaymentDate(
                relativeTo = AccruedInterestPaymentDateRelativeTo.TERMINATION_DATE,
                paymentDateOffset = PaymentDaysOffset(
                    days = 2,
                    dayType = DayType.BUSINESS
                ),
                paymentDateAdjustments = noAdjustment()
            )
        }
    }

    @Test
    fun `RelativePaymentDate accepts no offset`() {
        RelativePaymentDate(
            relativeTo = AccruedInterestPaymentDateRelativeTo.TRIGGER_DATE,
            paymentDateOffset = null,
            paymentDateAdjustments = noAdjustment()
        )
    }

    @Test
    fun `RelativePaymentDate accepts calendar day offset without business centers`() {
        RelativePaymentDate(
            relativeTo = AccruedInterestPaymentDateRelativeTo.TERMINATION_DATE,
            paymentDateOffset = PaymentDaysOffset(
                days = 2,
                dayType = DayType.CALENDAR
            ),
            paymentDateAdjustments = noAdjustment()
        )
    }

    @Test
    fun `RelativePaymentDate accepts business day offset with business centers`() {
        RelativePaymentDate(
            relativeTo = AccruedInterestPaymentDateRelativeTo.TERMINATION_DATE,
            paymentDateOffset = PaymentDaysOffset(
                days = 2,
                dayType = DayType.BUSINESS
            ),
            paymentDateAdjustments = BusinessDayAdjustments.create(
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