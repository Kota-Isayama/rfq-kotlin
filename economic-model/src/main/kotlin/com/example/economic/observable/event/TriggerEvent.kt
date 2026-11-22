package com.example.economic.observable.event

import ComparisonOperator
import ValueExpression
import com.example.economic.base.datetime.DateSchedule
import java.math.BigDecimal


data class Trigger(
    val value: ValueExpression,
    val comparison: ComparisonOperator,
    val threshold: BigDecimal
)


data class TriggerEvent(
    val trigger: Trigger,
    val observationSchedule: DateSchedule
)