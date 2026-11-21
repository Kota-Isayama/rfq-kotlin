package com.example.economic

import java.math.BigDecimal

data class Quantity<out U: UnitType>(
    val measure: Measure<U>,
    val multiplier: Measure<UnitType>
) {
    init {
        require(multiplier.value > BigDecimal.ZERO) {
            "Multiplier must be positive"
        }
    }
}


data class QuantitySchedule<out U: UnitType>(
    val measure: MeasureSchedule<U>,
    val multiplier: Measure<UnitType>
) {
    init {
        require(multiplier.value > BigDecimal.ZERO) {
            "Multiplier must be positive"
        }
    }
}