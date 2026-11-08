package com.example.economic

import java.time.LocalDate

enum class StubLength {
    SHORT,
    LONG;
}


sealed interface StubSpecification


object NoneStub : StubSpecification

data class InitialStub(val length: StubLength) : StubSpecification

data class FinalStub(val length: StubLength) : StubSpecification


data class BothStub(
    val unadjustedFirstRegularPeriodStartDate: LocalDate,
    val unadjustedLastRegularPeriodEndDate: LocalDate,
): StubSpecification {
    init {
        require(
            unadjustedFirstRegularPeriodStartDate.isBefore(unadjustedLastRegularPeriodEndDate)
        ) {
            "First Regular Period Start Date must be before Last Regular Period End Date."
        }
    }
}

