package com.example.economic.base.math

import com.example.economic.base.datetime.BusinessDayAdjustments
import com.example.economic.base.datetime.BusinessDayConvention
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class QuantityTest {
    @Test
    fun `Quantity multiplier rejects 0`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            Quantity(
                measure = Measure(BigDecimal.ZERO, Currency.EUR),
                multiplier = Measure(BigDecimal.ZERO, Currency.EUR),
            )
        }
    }

    @Test
    fun `Quantity multiplier rejects negative value`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            Quantity(
                measure = Measure(BigDecimal.ZERO, Currency.EUR),
                multiplier = Measure(BigDecimal(-1), Currency.EUR),
            )
        }
    }

    @Test
    fun `QuantitySchedule multiplier rejects 0`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            QuantitySchedule(
                measure = MeasureSchedule(
                    valueSchedule = DateBasedSchedule(
                        initialValue = BigDecimal.ZERO,
                        steps = listOf(
                            DateBasedSchedule.DatedStep(
                                date = LocalDate.of(2019, 1, 1),
                                value = BigDecimal.ONE,
                            )
                        ),
                        stepDateAdjustments = BusinessDayAdjustments.create(
                            convention = BusinessDayConvention.NONE,
                            centers = setOf(),
                        )
                    ),
                    unit = Currency.EUR,
                ),
                multiplier = Measure(BigDecimal.ZERO, Currency.EUR),
            )
        }
    }

    @Test
    fun `QuantitySchedule multiplier rejects negative value`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            QuantitySchedule(
                measure = MeasureSchedule(
                    valueSchedule = DateBasedSchedule(
                        initialValue = BigDecimal.ZERO,
                        steps = listOf(
                            DateBasedSchedule.DatedStep(
                                date = LocalDate.of(2019, 1, 1),
                                value = BigDecimal.ONE,
                            )
                        ),
                        stepDateAdjustments = BusinessDayAdjustments.create(
                            convention = BusinessDayConvention.NONE,
                            centers = setOf(),
                        )
                    ),
                    unit = Currency.EUR,
                ),
                multiplier = Measure(BigDecimal(-1), Currency.EUR),
            )
        }
    }
}