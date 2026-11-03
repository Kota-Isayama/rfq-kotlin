package com.example.economic

import Frequency
import PeriodUnit
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource

class FrequencyTest {
    @ParameterizedTest
    @EnumSource(PeriodUnit::class)
    fun `Zero is invalid multiplier`(unit: PeriodUnit) {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            Frequency(periodMultiplier = 0, periodUnit = unit)
        }
    }

    @ParameterizedTest
    @EnumSource(PeriodUnit::class)
    fun `Negative is invalid multiplier`(unit: PeriodUnit) {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            Frequency(periodMultiplier = -1, periodUnit = unit)
        }
    }

    @Test
    fun `1 is valid multiplier for Term PeriodUnit`() {
        Frequency(periodMultiplier = 1, periodUnit = PeriodUnit.TERM)
    }

    @ParameterizedTest
    @ValueSource(ints = [2, 10, 100])
    fun `Integer other than 1 is invalid multiplier for Term PeriodUnit`(multiplier: Int) {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            Frequency(periodMultiplier = multiplier, periodUnit = PeriodUnit.TERM)
        }
    }
}
