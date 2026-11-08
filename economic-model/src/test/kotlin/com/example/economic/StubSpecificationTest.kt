package com.example.economic

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDate

class StubSpecificationTest {
    @Test
    fun `First regular periods start date cannot be after last regular period end date`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            BothStub(
                unadjustedFirstRegularPeriodStartDate = LocalDate.parse("2020-01-01"),
                unadjustedLastRegularPeriodEndDate = LocalDate.parse("2020-01-01")
            )
        }
    }
}
