package com.example.economic

import java.time.LocalDate

data class CalculationPeriodDates(
    val unadjustedEffectiveDate: LocalDate,
    val unadjustedTerminationDate: LocalDate,
    val scheduleFrequency: ScheduleFrequency,
    val calculationPeriodDatesAdjustments: BusinessDayAdjustments,
    val stub: StubSpecification
) {
    init {
        require(unadjustedEffectiveDate.isBefore(unadjustedTerminationDate)) {
            "Effective date must be before Termination Date."
        }

        if (stub is BothStub) {
            require(unadjustedEffectiveDate.isBefore(stub.unadjustedFirstRegularPeriodStartDate)) {
                "Effective date must be before First Regular Period Start Date."
            }

            require(stub.unadjustedLastRegularPeriodEndDate.isBefore(unadjustedTerminationDate)) {
                "Termination date must be after Last Regular Period End Date."
            }
        }
    }
}
