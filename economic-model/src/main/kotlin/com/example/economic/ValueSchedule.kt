package com.example.economic

import java.time.LocalDate

sealed interface ValueSchedule<out T>


data class Constant<out T: Any>(val value: T) : ValueSchedule<T>


data class DateBasedSchedule<out T>(
    val initialValue: T,
    val steps: List<DatedStep<T>>,
    val stepDateAdjustments: BusinessDayAdjustments,
): ValueSchedule<T> {
    data class DatedStep<out T>(
        val date: LocalDate,
        val value: T,
    )

    init {
        require(steps.isNotEmpty()) {
            "Steps must not be empty"
        }

        require(
            steps.zipWithNext().all { (a, b) ->
                a.date < b.date
            }
        ) {
            "Step date must be monomorphic."
        }
    }
}


data class PeriodBasedSchedule<out T>(
    val initialValue: T,
    val steps: List<PeriodStep<T>>
): ValueSchedule<T> {
    data class PeriodStep<out T>(
        val fromCalculationPeriodNumber: Int,
        val value: T,
    ) {
        init {
            require(fromCalculationPeriodNumber != 1) {
                "First calculation period value must be specified as initialValue."
            }
        }
    }

    init {
        require(steps.isNotEmpty()) {
            "Steps must not be empty"
        }

        require(
            steps.zipWithNext().all { (a, b) ->
                a.fromCalculationPeriodNumber < b.fromCalculationPeriodNumber
            }
        ) {
            "FromCaluculationPeriodNumber must be monomorphic."
        }
    }
}
