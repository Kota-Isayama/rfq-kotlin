package com.example.economic.product.termination

import com.example.economic.observable.event.TriggerEvent

data class KnockOutProvision(
    val condition: KnockOutCondition,
    val terminationDateRule: TerminationDateRule,
    val effects: Set<TerminationEffect>
)


sealed interface KnockOutCondition


data class BarrierTriggerCondition(
    val triggerEvent: TriggerEvent,
): KnockOutCondition


data class TargetCondition(
    val target: TargetAccumulationRule
): KnockOutCondition