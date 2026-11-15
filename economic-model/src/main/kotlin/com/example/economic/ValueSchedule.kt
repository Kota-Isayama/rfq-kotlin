package com.example.economic

import java.time.LocalDate

sealed interface ValueSchedule<out T: Any>


data class Constant<out T: Any>(val value: T) : ValueSchedule<T>


data class DateBasedSchedule<out T: Any>(
    val initialValue: T,
    val steps: List<DatedStep<T>>,
    val stepDateAdjustments: BusinessDayAdjustments,
): ValueSchedule<T> {
    data class DatedStep<out T: Any>(
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
            "Step date must be strictly increasing."
        }
    }
}


data class PeriodBasedSchedule<out T: Any>(
    val initialValue: T,
    val steps: List<PeriodStep<T>>
): ValueSchedule<T> {
    data class PeriodStep<out T: Any>(
        val fromCalculationPeriodNumber: Int,
        val value: T,
    ) {
        init {
            require(fromCalculationPeriodNumber > 0) {
                "FromCalculationPeriodNumber must be positive"
            }

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
            "FromCaluculationPeriodNumber must be strictly increasing."
        }
    }
}
