package com.example.economic.observable.event

import com.example.economic.base.datetime.DateSchedule
import com.example.economic.observable.ComparisonOperator
import com.example.economic.observable.ValueExpression
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