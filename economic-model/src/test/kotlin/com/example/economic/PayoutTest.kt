package com.example.economic

import org.junit.jupiter.api.Assertions
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.test.Test

class PayoutTest {
    @Test
    fun `Notional rejects non-positive initial value`() {
        val payer = CounterpartyRole.PARTY1
        val receiver = CounterpartyRole.PARTY2
        val notional = QuantitySchedule(
            measure = MeasureSchedule(
                valueSchedule = PeriodBasedSchedule(
                    initialValue = BigDecimal("-1000000"),
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

        Assertions.assertThrows(IllegalArgumentException::class.java) {
             InterestRatePayout(
                payerReceiver = PayerReceiver(payer=receiver, receiver = payer),
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
        }
    }

    @Test
    fun `Notional rejects non-positive date-based step value`() {
        val payer = CounterpartyRole.PARTY1
        val receiver = CounterpartyRole.PARTY2
        val notional = QuantitySchedule(
            measure = MeasureSchedule(
                valueSchedule = DateBasedSchedule(
                    initialValue = BigDecimal("1000000"),
                    steps = listOf(
                        DateBasedSchedule.DatedStep(
                            date = LocalDate.parse("2021-04-01"),
                            value = BigDecimal("-1000000"),
                        )
                    ),
                    stepDateAdjustments = BusinessDayAdjustments.create(
                        convention = BusinessDayConvention.MODFOLLOWING,
                        centers = setOf(BusinessCenter("JPTO")),
                    )
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

        Assertions.assertThrows(IllegalArgumentException::class.java) {
            InterestRatePayout(
                payerReceiver = PayerReceiver(payer=receiver, receiver = payer),
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
        }
    }

    @Test
    fun `Notional rejects non-positive period-based step value`() {
        val payer = CounterpartyRole.PARTY1
        val receiver = CounterpartyRole.PARTY2
        val notional = QuantitySchedule(
            measure = MeasureSchedule(
                valueSchedule = PeriodBasedSchedule(
                    initialValue = BigDecimal("1000000"),
                    steps = listOf(
                        PeriodBasedSchedule.PeriodStep(
                            fromCalculationPeriodNumber = 2,
                            value = BigDecimal("-2000000"),
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

        Assertions.assertThrows(IllegalArgumentException::class.java) {
            InterestRatePayout(
                payerReceiver = PayerReceiver(payer=receiver, receiver = payer),
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
        }
    }
}