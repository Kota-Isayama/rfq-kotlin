package com.example.economic

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CalculationPeriodFrequencyTest {
    @ParameterizedTest
    @MethodSource("normalRollConventionSource")
    fun `Term Frequency cannot roll`(rollConvention: RollConvention) {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            CalculationPeriodFrequency(
                Frequency(periodUnit = PeriodUnit.TERM, periodMultiplier = 1),
                rollConvention = rollConvention,
            )
        }
    }

    private fun normalRollConventionSource() = listOf(
        DayOfMonth(1),
        EndOfMonth,
    )

    @Test
    fun `Normal Frequency must have roll convention`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            CalculationPeriodFrequency(
                Frequency(periodUnit = PeriodUnit.YEAR, periodMultiplier = 1),
                rollConvention = NoRoll,
            )
        }
    }
}