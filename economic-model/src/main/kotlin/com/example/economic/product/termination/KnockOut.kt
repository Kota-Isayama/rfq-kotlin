package com.example.economic.product.termination

import com.example.economic.observable.event.TriggerEvent

data class KnockOut(
    val triggerEvent: TriggerEvent,
    val terminationDateRule: TerminationDateRule,
    val effects: Set<TerminationEffect>
)

