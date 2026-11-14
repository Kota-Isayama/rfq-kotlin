package com.example.economic

import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class OisBuildTest {
    @Test
    fun `JPY OIS can be represented`() {
        val payer = CounterpartyRole.PARTY1
        val receiver = CounterpartyRole.PARTY2
        val notional = Notional(amount = BigDecimal("1000000000"))
        val calculationPeriodDates = CalculationPeriodDates(
            unadjustedEffectiveDate = LocalDate.parse("2020-01-01"),
            unadjustedTerminationDate = LocalDate.parse("2025-01-01"),
            scheduleFrequency = ScheduleFrequency(
                frequency = Frequency(periodMultiplier = 1, periodUnit = PeriodUnit.YEAR),
                rollConvention = DayOfMonth(day = 1),
            ),
            calculationPeriodDatesAdjustments = BusinessDayAdjustments.create(
                convention = BusinessDayConvention.MODFOLLOWING,
                centers = setOf(BusinessCenter("JPTO")),
            ),
            stub= NoneStub,
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

        val floatingRatePayout = InterestRatePayout(
            payerReceiver = PayerReceiver(payer=receiver, receiver = payer),
            currency = Currency.JPY,
            notional = notional,
            calculationPeriodDates = calculationPeriodDates,
            paymentDates = paymentDates,
            dayCountFraction = DayCountFraction.ACT_365_FIXED,
            rateSpecification = FloatingRateSpecification(
                index = RateIndex.JPY_TONA,
                spread = BigDecimal("0"),
                floatingRateDetermination = Compounding(
                    observationRule = NoShift,
                    businessCenters = setOf(BusinessCenter("JPTO")),
                )
            )
        )

        val fixedRatePayout = InterestRatePayout(
            payerReceiver = PayerReceiver(payer=payer, receiver = receiver),
            currency = Currency.JPY,
            notional = notional,
            calculationPeriodDates = calculationPeriodDates,
            paymentDates = paymentDates,
            dayCountFraction = DayCountFraction.ACT_365_FIXED,
            rateSpecification = FixedRateSpecification(
                rate= BigDecimal("0.01"),
            )
        )

        EconomicTerms(
            payouts = listOf(floatingRatePayout, fixedRatePayout),
        )
    }
}
