package com.example.economic.observable.price

import com.example.economic.base.math.Constant
import com.example.economic.base.math.Currency
import com.example.economic.base.math.Dimensionless
import com.example.economic.base.math.FinancialUnit
import com.example.economic.base.math.Measure
import com.example.economic.base.math.MeasureSchedule
import com.example.economic.base.math.QuantitySchedule
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class PriceQuantityTest {
    @Test
    fun `accepts price quantity with dimensionless multiplier and matching units`() {
        PriceQuantity(
            price = PriceSchedule(
                measure = MeasureSchedule(
                    valueSchedule = Constant(BigDecimal(150)),
                    unit = Currency.JPY
                ),
                perUnitOf = Currency.USD
            ),
            quantity = QuantitySchedule(
                measure = MeasureSchedule(
                    valueSchedule = Constant(BigDecimal(1000000)),
                    unit = Currency.USD
                ),
                multiplier = Measure(
                    BigDecimal.ONE,
                    unit = Dimensionless
                )
            )
        )
    }

    @Test
    fun `accepts price quantity with unit conversion multiplier and matching units`() {
        PriceQuantity(
            price = PriceSchedule(
                measure = MeasureSchedule(
                    valueSchedule = Constant(BigDecimal(150)),
                    unit = Currency.JPY
                ),
                perUnitOf = FinancialUnit.SHARE
            ),
            quantity = QuantitySchedule(
                measure = MeasureSchedule(
                    valueSchedule = Constant(BigDecimal(10)),
                    unit = FinancialUnit.CONTRACT
                ),
                multiplier = Measure(
                    value = BigDecimal(100),
                    unit = FinancialUnit.SHARE
                )
            )
        )
    }

    @Test
    fun `rejects price quantity with price denominator differs from effective quantity unit`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            PriceQuantity(
                price = PriceSchedule(
                    measure = MeasureSchedule(
                        valueSchedule = Constant(BigDecimal(150)),
                        unit = Currency.JPY
                    ),
                    perUnitOf = Currency.EUR
                ),
                quantity = QuantitySchedule(
                    measure = MeasureSchedule(
                        valueSchedule = Constant(BigDecimal(100000000)),
                        unit = Currency.USD
                    ),
                    multiplier = Measure(
                        value = BigDecimal(1),
                        unit = Currency.USD
                    )
                )
            )
        }
    }
}