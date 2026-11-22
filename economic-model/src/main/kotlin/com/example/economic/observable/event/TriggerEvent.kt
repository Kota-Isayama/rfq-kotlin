package com.example.economic.observable.event

import com.example.economic.base.datetime.DateSchedule
import com.example.economic.observable.Observable
import java.math.BigDecimal


sealed interface ValueExpression


data class ObservableValue(
    val observable: Observable
): ValueExpression


enum class ComparisonOperator {
    LESS,
    LESS_OR_EQUAL,
    EQUAL,
    GREATER_OR_EQUAL,
    GREATER
}


data class Trigger(
    val value: ValueExpression,
    val comparison: ComparisonOperator,
    val threshold: BigDecimal
)


data class TriggerEvent(
    val trigger: Trigger,
    val observationSchedule: DateSchedule
)