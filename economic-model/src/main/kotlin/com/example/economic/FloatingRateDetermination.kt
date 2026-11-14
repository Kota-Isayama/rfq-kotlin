package com.example.economic

sealed interface FloatingRateDetermination


data class Compounding(
    val observationRule: ObservationRule,
    val businessCenters: Set<BusinessCenter>,
) : FloatingRateDetermination
