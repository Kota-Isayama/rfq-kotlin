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
                spread = Constant(value = BigDecimal("0")),
                multiplier = PeriodBasedSchedule(
                    initialValue = BigDecimal("1.0"),
                    steps = listOf(
                        PeriodBasedSchedule.PeriodStep(
                            fromCalculationPeriodNumber = 2,
                            value = BigDecimal("2.0"),
                        )
                    )
                ),
                floor = null,
                cap = DateBasedSchedule(
                    initialValue = BigDecimal("4.0"),
                    steps = listOf(
                        DateBasedSchedule.DatedStep(
                            date = LocalDate.parse("2021-04-01"),
                            value = BigDecimal("8.0"),
                        )
                    ),
                    stepDateAdjustments = BusinessDayAdjustments.create(
                        convention = BusinessDayConvention.MODFOLLOWING,
                        centers = setOf(BusinessCenter("JPTO")),
                    )
                ),
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
                rate= PeriodBasedSchedule(
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

        EconomicTerms(
            payouts = listOf(floatingRatePayout, fixedRatePayout),
        )
    }
}
