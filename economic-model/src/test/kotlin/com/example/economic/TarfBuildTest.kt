package com.example.economic

import com.example.economic.base.datetime.BusinessDayAdjustments
import com.example.economic.base.datetime.BusinessDayConvention
import com.example.economic.base.datetime.DayCountFraction
import com.example.economic.base.datetime.DayOfMonth
import com.example.economic.base.datetime.DaysOffset
import com.example.economic.base.datetime.ExplicitDateSchedule
import com.example.economic.base.datetime.Frequency
import com.example.economic.base.datetime.PeriodUnit
import com.example.economic.base.datetime.RelativeDateRule
import com.example.economic.base.datetime.ScheduleFrequency
import com.example.economic.base.math.Constant
import com.example.economic.base.math.Currency
import com.example.economic.base.math.Dimensionless
import com.example.economic.base.math.Measure
import com.example.economic.base.math.MeasureSchedule
import com.example.economic.base.math.QuantitySchedule
import com.example.economic.base.math.ValueSchedule
import com.example.economic.base.party.CounterpartyRole
import com.example.economic.base.party.PayerReceiver
import com.example.economic.observable.CurrencyPair
import com.example.economic.observable.ForeignExchangeRateIndex
import com.example.economic.observable.FxSpotRateSource
import com.example.economic.observable.InformationProvider
import com.example.economic.product.EconomicTerms
import com.example.economic.product.payout.InterestRatePayout
import com.example.economic.product.payout.PayoutId
import com.example.economic.product.rate.AdjustedReferenceDate
import com.example.economic.product.rate.DigitalRateFixing
import com.example.economic.product.rate.PeriodBoundary
import com.example.economic.product.rate.RateRegion
import com.example.economic.product.rate.ThresholdRateSpecification
import com.example.economic.product.schedule.CalculationPeriodDates
import com.example.economic.product.schedule.NoneStub
import com.example.economic.product.schedule.PayRelativeTo
import com.example.economic.product.schedule.PaymentDates
import com.example.economic.product.termination.AmountTarget
import com.example.economic.product.termination.ExactAdjustmentMethod
import com.example.economic.product.termination.FxDifferenceContribution
import com.example.economic.product.termination.FxDifferencePart
import com.example.economic.product.termination.KnockOutProvision
import com.example.economic.product.termination.RelativeTerminationDate
import com.example.economic.product.termination.TargetCondition
import com.example.economic.product.termination.TargetSettlementStyle
import com.example.economic.product.termination.TargetSettlementTreatment
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class TarfBuildTest {
    @Test
    fun `We can build TARF`() {
        val noAdjustment = BusinessDayAdjustments.create(
            convention = BusinessDayConvention.NONE,
            centers = emptySet(),
        )

        val fxIndex = ForeignExchangeRateIndex(
            currencyPair = CurrencyPair(baseCurrency = Currency.USD, quoteCurrency = Currency.JPY),
            fixingSource = FxSpotRateSource(InformationProvider.REFINITIV)
        )

        val couponId = PayoutId("coupon")

        val coupon = InterestRatePayout(
            payoutId = couponId,
            payerReceiver = PayerReceiver(payer = CounterpartyRole.PARTY1, receiver = CounterpartyRole.PARTY2),
            notional = QuantitySchedule(
                measure = MeasureSchedule(
                    valueSchedule = Constant(value=BigDecimal("100000000")),
                    unit = Currency.JPY
                ),
                multiplier = Measure(BigDecimal.ONE, Dimensionless),
            ),
            calculationPeriodDates = CalculationPeriodDates(
                unadjustedEffectiveDate = LocalDate.of(2027, 1, 1),
                unadjustedTerminationDate = LocalDate.of(2028, 1, 1),
                scheduleFrequency = ScheduleFrequency(
                    frequency = Frequency(3, PeriodUnit.MONTH),
                    rollConvention = DayOfMonth(1),
                ),
                calculationPeriodDatesAdjustments = noAdjustment,
                stub= NoneStub
            ),
            paymentDates = PaymentDates(
                paymentFrequency = Frequency(3, PeriodUnit.MONTH),
                payRelativeTo = PayRelativeTo.CALCULATION_PERIOD_END_DATE,
                paymentDateOffset = null,
                paymentDatesAdjustments = noAdjustment,
            ),
            dayCountFraction = DayCountFraction.ACT_360,
            rateSpecification = ThresholdRateSpecification(
                index = fxIndex,
                fixing = DigitalRateFixing(
                    reference = PeriodBoundary.CALCULATION_PERIOD_END_DATE,
                    fixingDate = AdjustedReferenceDate(noAdjustment)
                ),
                regions = listOf(
                    RateRegion(upperExclusive = BigDecimal("145"), rate = Constant(BigDecimal("0.01"))),
                    RateRegion(upperExclusive = BigDecimal("155"), rate = Constant(BigDecimal("0.03"))),
                ),
                otherwiseRate = Constant(BigDecimal("0.05"))
            )
        )

        val target = AmountTarget(
            observationSchedule = ExplicitDateSchedule(
                unadjustedDates = listOf(
                    LocalDate.of(2027, 4, 1),
                    LocalDate.of(2027, 7, 1),
                    LocalDate.of(2027, 10, 1),
                    LocalDate.of(2028, 1, 1),
                ),
                dateAdjustments = noAdjustment,
            ),
            contributionRule = FxDifferenceContribution(
                index = fxIndex,
                strike = Constant(BigDecimal("153")),
                notional = QuantitySchedule(
                    measure = MeasureSchedule(
                        valueSchedule = Constant(BigDecimal("1000000")),
                        unit = Currency.USD
                    ),
                    multiplier = Measure(BigDecimal.ONE, Dimensionless),
                ),
                fxDifferencePart = FxDifferencePart.POSITIVE_PART_OF_INDEX_MINUS_STRIKE
            ),
            targetAmount = Measure(value = BigDecimal("10000000"), unit = Currency.JPY)
        )

        val knockOut = KnockOutProvision(
            condition = TargetCondition(target),
            terminationDateRule = RelativeTerminationDate(
                relativeTo = RelativeTerminationDate.TerminationDateRelativeTo.TRIGGER_DATE,
                dateRule = RelativeDateRule(
                    offset = null,
                    adjustments = noAdjustment
                )
            ),
            effects = setOf(
                TargetSettlementTreatment(
                    style = TargetSettlementStyle.EXACT,
                    adjustmentMethod = ExactAdjustmentMethod.VARIED_NOTIONAL
                )
            )
        )

        EconomicTerms(
            payouts = listOf(coupon),
            knockOutProvision = knockOut,
        )
    }
}