//package com.example.economic.product.termination
//
//import org.junit.jupiter.api.Assertions
//import org.junit.jupiter.api.Test
//
//class TargetAccumulationRuleTest {
//    @Test
//    fun `target count must be positive`() {
//        Assertions.assertThrows(IllegalArgumentException::class.java) {
//            CountTarget(
//                observationSchedule = schedule,
//                qualifyingCondition = trigger,
//                targetCount = 0
//            )
//        }
//    }
//}