package com.example.economic

interface FloatingRateDetermination


data class Compounding(
    val observationRule: ObservationRule,
    val businessCenters: Set<BusinessCenter>,
) : FloatingRateDetermination
