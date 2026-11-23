package com.example.economic.product.termination

import com.example.economic.base.datetime.DateSchedule
import com.example.economic.observable.event.Trigger

sealed interface TargetAccumulationRule


//data class AmountTarget(
//    val observationSchedule: DateSchedule,
//    val contributionRule:
//)


data class CountTarget(
    val observationSchedule: DateSchedule,
    val qualifyingCondition: Trigger,
    val targetCount: Int,
): TargetAccumulationRule {
    init {
        require(targetCount > 0) { "targetCount must be greater than 0" }
    }
}