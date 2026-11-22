package com.example.economic.product

import com.example.economic.base.datetime.BusinessCenter
import com.example.economic.base.datetime.BusinessDayAdjustments
import com.example.economic.base.datetime.BusinessDayConvention
import com.example.economic.base.datetime.DayCountFraction
import com.example.economic.base.datetime.DayOfMonth
import com.example.economic.base.datetime.Frequency
import com.example.economic.base.datetime.PeriodUnit
import com.example.economic.base.datetime.ScheduleFrequency
import com.example.economic.base.math.Currency
import com.example.economic.base.math.Dimensionless
import com.example.economic.base.math.Measure
import com.example.economic.base.math.MeasureSchedule
import com.example.economic.base.math.PeriodBasedSchedule
import com.example.economic.base.math.QuantitySchedule
import com.example.economic.base.party.CounterpartyRole
import com.example.economic.base.party.PayerReceiver
import com.example.economic.product.payout.InterestRatePayout
import com.example.economic.product.payout.PayoutId
import com.example.economic.product.rate.FixedRateSpecification
import com.example.economic.product.schedule.CalculationPeriodDates
import com.example.economic.product.schedule.DayType
import com.example.economic.product.schedule.NoneStub
import com.example.economic.product.schedule.PayRelativeTo
import com.example.economic.product.schedule.PaymentDates
import com.example.economic.product.schedule.PaymentDaysOffset
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class EconomicTermsTest {
    @Test
    fun `Empty payout is rejected`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            EconomicTerms(
                payouts = emptyList(),
            )
        }
    }

    @Test
    fun `Payout IDs must be unique"`() {
        val payer = CounterpartyRole.PARTY1
        val receiver = CounterpartyRole.PARTY2
        val notional = QuantitySchedule(
            measure = MeasureSchedule(
                valueSchedule = PeriodBasedSchedule(
                    initialValue = BigDecimal("1000000"),
                    steps = listOf(
                        PeriodBasedSchedule.PeriodStep(
                            fromCalculationPeriodNumber = 2,
                            value = BigDecimal("2000000"),
                        )
                    ),
                ),
                unit = Currency.JPY,
            ),
            multiplier = Measure(
                value = BigDecimal.ONE,
                unit = Dimensionless,
            ),
        )
        val calculationPeriodDates = CalculationPeriodDates(
            unadjustedEffectiveDate = LocalDate.parse("2020-04-01"),
            unadjustedTerminationDate = LocalDate.parse("2025-04-01"),
            scheduleFrequency = ScheduleFrequency(
                frequency = Frequency(periodMultiplier = 1, periodUnit = PeriodUnit.YEAR),
                rollConvention = DayOfMonth(day = 1),
            ),
            calculationPeriodDatesAdjustments = BusinessDayAdjustments.create(
                convention = BusinessDayConvention.MODFOLLOWING,
                centers = setOf(BusinessCenter("JPTO")),
            ),
            stub = NoneStub,
        )
        val paymentDates = PaymentDates(
            paymentFrequency = Frequency(periodMultiplier = 1, periodUnit = PeriodUnit.YEAR),
            payRelativeTo = PayRelativeTo.CALCULATION_PERIOD_END_DATE,
            paymentDateOffset = PaymentDaysOffset(dayType = DayType.BUSINESS, days = 2),
            paymentDatesAdjustments = BusinessDayAdjustments.create(
                convention = BusinessDayConvention.MODFOLLOWING,
                centers = setOf(BusinessCenter("JPTO"))
            )
        )

        val fixedRatePayout = InterestRatePayout(
            payoutId = PayoutId("fixed-rate"),
            payerReceiver = PayerReceiver(payer = payer, receiver = receiver),
            notional = notional,
            calculationPeriodDates = calculationPeriodDates,
            paymentDates = paymentDates,
            dayCountFraction = DayCountFraction.ACT_365_FIXED,
            rateSpecification = FixedRateSpecification(
                rate = PeriodBasedSchedule(
                    initialValue = BigDecimal("0.01"),
                    steps = listOf(
                        PeriodBasedSchedule.PeriodStep(
                            fromCalculationPeriodNumber = 2,
                            value = BigDecimal("0.02"),
                        )
                    )
                ),
            )
        )

        Assertions.assertThrows(IllegalArgumentException::class.java) {
            EconomicTerms(
                payouts = listOf(fixedRatePayout, fixedRatePayout),
            )
        }
    }
}