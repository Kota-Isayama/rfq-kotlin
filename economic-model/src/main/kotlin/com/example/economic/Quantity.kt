package com.example.economic

data class Quantity<U: UnitType>(
    val measure: Measure<U>,
    val multiplier: Measure<UnitType>
)


data class QuantitySchedule<U: UnitType>(
    val measure: MeasureSchedule<U>,
    val multiplier: Measure<UnitType>
)