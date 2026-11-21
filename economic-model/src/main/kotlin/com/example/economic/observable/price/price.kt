package com.example.economic.observable.price

import com.example.economic.base.math.Measure
import com.example.economic.base.math.MeasureSchedule
import com.example.economic.base.math.UnitType

data class Price<out U: UnitType, out V: UnitType>(
    val measure: Measure<U>,
    val perUnitOf: V
)


data class PriceSchedule<out U: UnitType, out V: UnitType>(
    val measure: MeasureSchedule<U>,
    val perUnitOf: V,
)
