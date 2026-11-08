package com.example.economic

import java.time.LocalDate

enum class StubLength {
    SHORT,
    LONG;
}


interface StubSpecification


object NoneStub : StubSpecification

data class InitialStub(val length: StubLength) : StubSpecification

data class FinalStub(val length: StubLength) : StubSpecification


data class BothStub(
    val unadjustedFirstRegularPeriodStartDate: LocalDate,
    val unadjustedLastRegularPeriodEndDate: LocalDate,
) {
    init {
        require(
            unadjustedFirstRegularPeriodStartDate.isBefore(unadjustedLastRegularPeriodEndDate)
        ) {
            "Fist Regular Period Start Date must be before Last Regular Period End Date."
        }
    }
}

