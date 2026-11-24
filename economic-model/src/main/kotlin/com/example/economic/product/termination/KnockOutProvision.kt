package com.example.economic.product.termination

import com.example.economic.observable.event.TriggerEvent

data class KnockOutProvision(
    val condition: KnockOutCondition,
    val terminationDateRule: TerminationDateRule,
    val effects: Set<TerminationEffect>
) {
    init {
        when (condition) {
            is BarrierTriggerCondition -> {
                require(effects.all { !(it is TargetSettlementTreatment)}) {
                    "When BarrierTriggerCondition, TargetSettlement Treatment must not be specified."
                }
            }
            is TargetCondition -> {
                require(
                    effects.count { it is TargetSettlementTreatment } == 1
                ) {
                    "When TargetSettlementTreatment, exactly one TargetSettlement must be specified."
                }
            }
        }
    }
}


sealed interface KnockOutCondition


data class BarrierTriggerCondition(
    val triggerEvent: TriggerEvent,
): KnockOutCondition


data class TargetCondition(
    val target: TargetAccumulationRule
): KnockOutCondition