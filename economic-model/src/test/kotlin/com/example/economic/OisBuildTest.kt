package com.example.economic

import com.example.economic.base.datetime.BusinessCenter
import com.example.economic.base.datetime.BusinessDayAdjustments
import com.example.economic.base.datetime.BusinessDayConvention
import com.example.economic.base.datetime.DayCountFraction
import com.example.economic.base.datetime.DayOfMonth
import com.example.economic.base.datetime.Frequency
import com.example.economic.base.datetime.PeriodUnit
import com.example.economic.base.datetime.ScheduleFrequency
import com.example.economic.base.math.Constant
import com.example.economic.base.math.Currency
import com.example.economic.base.math.DateBasedSchedule
import com.example.economic.base.math.Dimensionless
import com.example.economic.base.math.Measure
import com.example.economic.base.math.MeasureSchedule
import com.example.economic.base.math.PeriodBasedSchedule
import com.example.economic.base.math.QuantitySchedule
import com.example.economic.base.party.CounterpartyRole
import com.example.economic.base.party.PayerReceiver
import com.example.economic.observable.index.RateIndex
import com.example.economic.product.EconomicTerms
import com.example.economic.product.payout.InterestRatePayout
import com.example.economic.product.rate.Compounding
import com.example.economic.product.rate.FixedRateSpecification
import com.example.economic.product.rate.FloatingRateSpecification
import com.example.economic.product.rate.NoShift
import com.example.economic.product.schedule.CalculationPeriodDates
import com.example.economic.product.schedule.DayType
import com.example.economic.product.schedule.NoneStub
import com.example.economic.product.schedule.PayRelativeTo
import com.example.economic.product.schedule.PaymentDates
import com.example.economic.product.schedule.PaymentDaysOffset
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class OisBuildTest {
    @Test
    fun `JPY OIS can be represented`() {
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

        val floatingRatePayout = InterestRatePayout(
            payerReceiver = PayerReceiver(payer = receiver, receiver = payer),
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

        EconomicTerms(
            payouts = listOf(floatingRatePayout, fixedRatePayout),
        )
    }
}
