
package com.example.economic.product.rate

import com.example.economic.base.datetime.BusinessCenter
import com.example.economic.base.math.Constant
import com.example.economic.base.math.Currency
import com.example.economic.observable.CurrencyPair
import com.example.economic.observable.ForeignExchangeRateIndex
import com.example.economic.observable.FxSpotRateSource
import com.example.economic.observable.InformationProvider
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ThresholdRateSpecificationTest {

    @Test
    fun `multiple rate regions are allowed`() {
        Assertions.assertDoesNotThrow {
            specification("145", "155")
        }
    }

    @Test
    fun `empty rate regions are rejected`() {
        Assertions.assertThrows(
            IllegalArgumentException::class.java
        ) {
            specification()
        }
    }

    @Test
    fun `descending thresholds are rejected`() {
        Assertions.assertThrows(
            IllegalArgumentException::class.java
        ) {
            specification("155", "145")
        }
    }

    @Test
    fun `duplicate thresholds are rejected`() {
        Assertions.assertThrows(
            IllegalArgumentException::class.java
        ) {
            specification("145", "145")
        }
    }

    private fun specification(
        vararg thresholds: String
    ): ThresholdRateSpecification {
        return ThresholdRateSpecification(
            index = ForeignExchangeRateIndex(
                currencyPair = CurrencyPair(
                    baseCurrency = Currency.USD,
                    quoteCurrency = Currency.JPY
                ),
                fixingSource = FxSpotRateSource(
                    sourceProvider = InformationProvider.REFINITIV
                )
            ),
            fixing = DigitalRateFixing(
                reference = PeriodBoundary.CALCULATION_PERIOD_END_DATE,
                fixingDate = BusinessDayFixingOffset(
                    offset = -2,
                    businessCenters = setOf(BusinessCenter("JPTO"))
                )
            ),
            regions = thresholds.map {
                RateRegion(
                    upperExclusive = BigDecimal(it),
                    rate = Constant(BigDecimal("0.01"))
                )
            },
            otherwiseRate = Constant(BigDecimal("0.05"))
        )
    }
}
