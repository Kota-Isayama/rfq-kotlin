package com.example.economic.base.math

import java.math.BigDecimal

data class Quantity<out U: UnitType, out V: UnitType>(
    val measure: Measure<U>,
    val multiplier: Measure<V>
) {
    init {
        require(multiplier.value > BigDecimal.ZERO) {
            "Multiplier must be positive"
        }
    }
}


data class QuantitySchedule<out U: UnitType, out V: UnitType>(
    val measure: MeasureSchedule<U>,
    val multiplier: Measure<V>
) {
    init {
        require(multiplier.value > BigDecimal.ZERO) {
            "Multiplier must be positive"
        }
    }
}