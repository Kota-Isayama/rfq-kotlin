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
import com.example.economic.base.datetime.DayType
import com.example.economic.product.schedule.NoneStub
import com.example.economic.product.schedule.PayRelativeTo
import com.example.economic.product.schedule.PaymentDates
import com.example.economic.base.datetime.DaysOffset
import com.example.economic.base.datetime.ExplicitDateSchedule
import com.example.economic.base.datetime.RelativeDateRule
import com.example.economic.base.math.Constant
import com.example.economic.observable.ComparisonOperator
import com.example.economic.observable.ObservableValue
import com.example.economic.observable.RateIndex
import com.example.economic.observable.event.Trigger
import com.example.economic.observable.event.TriggerEvent
import com.example.economic.product.termination.KnockOut
import com.example.economic.product.termination.OriginalPaymentDate
import com.example.economic.product.termination.PayAccruedInterest
import com.example.economic.product.termination.RelativeTerminationDate
import com.example.economic.product.termination.TerminationEffect
import com.example.economic.product.termination.WaiveAccruedInterest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.math.BigDecimal
import java.time.LocalDate

class EconomicTermsTest {
    @Test
    fun `Empty payout is rejected`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            EconomicTerms(
                payouts = emptyList(),
                knockOut = null
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
            paymentDateOffset = DaysOffset(dayType = DayType.BUSINESS, days = 2),
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

        val anotherPayout = fixedRatePayout.copy(
            dayCountFraction = DayCountFraction.ACT_360,
        )

        Assertions.assertThrows(IllegalArgumentException::class.java) {
            EconomicTerms(
                payouts = listOf(fixedRatePayout, anotherPayout),
                knockOut = null
            )
        }
    }
}


class KnockOutValidationTest {

    private val leg1Id = PayoutId("leg1")
    private val leg2Id = PayoutId("leg2")

    @Test
    fun `different accrued interest treatments per payout are allowed`() {
        assertDoesNotThrow {
            terms(
                setOf(
                    PayAccruedInterest(
                        payoutIds = setOf(leg1Id),
                        paymentDateRule = OriginalPaymentDate
                    ),
                    WaiveAccruedInterest(
                        payoutIds = setOf(leg2Id)
                    )
                )
            )
        }
    }

    @Test
    fun `missing accrued interest treatment is rejected`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            terms(
                setOf(
                    PayAccruedInterest(
                        payoutIds = setOf(leg1Id),
                        paymentDateRule = OriginalPaymentDate
                    )
                )
            )
        }
    }

    @Test
    fun `conflicting treatments for the same payout are rejected`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            terms(
                setOf(
                    PayAccruedInterest(
                        payoutIds = setOf(leg1Id, leg2Id),
                        paymentDateRule = OriginalPaymentDate
                    ),
                    WaiveAccruedInterest(
                        payoutIds = setOf(leg1Id)
                    )
                )
            )
        }
    }

    @Test
    fun `unknown payout reference is rejected`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            terms(
                setOf(
                    PayAccruedInterest(
                        payoutIds = setOf(leg1Id, leg2Id),
                        paymentDateRule = OriginalPaymentDate
                    ),
                    WaiveAccruedInterest(
                        payoutIds = setOf(PayoutId("unknown"))
                    )
                )
            )
        }
    }

    @Test
    fun `empty effects are rejected when interest rate payouts exist`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            terms(emptySet())
        }
    }

    private fun terms(effects: Set<TerminationEffect>): EconomicTerms {
        return EconomicTerms(
            payouts = listOf(
                payout(leg1Id),
                payout(leg2Id)
            ),
            knockOut = KnockOut(
                triggerEvent = TriggerEvent(
                    trigger = Trigger(
                        value = ObservableValue(RateIndex.JPY_TONA),
                        comparison = ComparisonOperator.GREATER_OR_EQUAL,
                        threshold = BigDecimal("0.05")
                    ),
                    observationSchedule = ExplicitDateSchedule(
                        unadjustedDates = listOf(
                            LocalDate.parse("2026-06-15")
                        ),
                        dateAdjustments = noAdjustment()
                    )
                ),
                terminationDateRule = RelativeTerminationDate(
                    relativeTo =
                        RelativeTerminationDate.TerminationDateRelativeTo.TRIGGER_DATE,
                    dateRule = RelativeDateRule(
                        offset = null,
                        adjustments = noAdjustment()
                    )
                ),
                effects = effects
            )
        )
    }

    private fun payout(id: PayoutId): InterestRatePayout {
        val adjustments = BusinessDayAdjustments.create(
            convention = BusinessDayConvention.MODFOLLOWING,
            centers = setOf(BusinessCenter("JPTO"))
        )

        return InterestRatePayout(
            payoutId = id,
            payerReceiver = PayerReceiver(
                payer = CounterpartyRole.PARTY1,
                receiver = CounterpartyRole.PARTY2
            ),
            notional = QuantitySchedule(
                measure = MeasureSchedule(
                    valueSchedule = Constant(BigDecimal("1000000")),
                    unit = Currency.JPY
                ),
                multiplier = Measure(
                    value = BigDecimal.ONE,
                    unit = Dimensionless
                )
            ),
            calculationPeriodDates = CalculationPeriodDates(
                unadjustedEffectiveDate = LocalDate.parse("2026-01-01"),
                unadjustedTerminationDate = LocalDate.parse("2027-01-01"),
                scheduleFrequency = ScheduleFrequency(
                    frequency = Frequency(1, PeriodUnit.YEAR),
                    rollConvention = DayOfMonth(1)
                ),
                calculationPeriodDatesAdjustments = adjustments,
                stub = NoneStub
            ),
            paymentDates = PaymentDates(
                paymentFrequency = Frequency(1, PeriodUnit.YEAR),
                payRelativeTo = PayRelativeTo.CALCULATION_PERIOD_END_DATE,
                paymentDateOffset = DaysOffset(2, DayType.BUSINESS),
                paymentDatesAdjustments = adjustments
            ),
            dayCountFraction = DayCountFraction.ACT_365_FIXED,
            rateSpecification = FixedRateSpecification(
                rate = Constant(BigDecimal("0.01"))
            )
        )
    }

    private fun noAdjustment() = BusinessDayAdjustments.create(
        convention = BusinessDayConvention.NONE,
        centers = emptySet()
    )
}