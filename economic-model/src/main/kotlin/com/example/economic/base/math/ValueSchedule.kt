package com.example.economic.base.math

import com.example.economic.base.datetime.BusinessDayAdjustments
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


data class OrdinalBasedSchedule<out T: Any>(
    val initialValue: T,
    val steps: List<OrdinalStep<T>>
): ValueSchedule<T> {
    data class OrdinalStep<out T: Any>(
        val fromOrdinal: Int,
        val value: T,
    ) {
        init {
            require(fromOrdinal > 0) {
                "FromOrdinal must be positive"
            }

            require(fromOrdinal != 1) {
                "First ordinal value must be specified as initialValue."
            }
        }
    }

    init {
        require(steps.isNotEmpty()) {
            "Steps must not be empty"
        }

        require(
            steps.zipWithNext().all { (a, b) ->
                a.fromOrdinal < b.fromOrdinal
            }
        ) {
            "FromOrdinal must be strictly increasing."
        }
    }
}
