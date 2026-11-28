package com.example.economic.observable.price

import com.example.economic.base.math.Dimensionless
import com.example.economic.base.math.QuantitySchedule
import com.example.economic.base.math.UnitType

data class PriceQuantity<
        U: UnitType,
        V: UnitType,
        W: UnitType,
        X: UnitType,
>(
    val price: PriceSchedule<U, V>,
    val quantity: QuantitySchedule<W, X>,
) {
    init {
        require(price.perUnitOf == quantity.effectiveUnit()) {
            "Price unit must match effective quantity unit"
        }
    }
}
