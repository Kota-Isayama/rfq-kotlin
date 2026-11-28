package com.example.economic.base.math

import com.example.economic.observable.price.PriceSchedule

data class PriceQuantity<
        U: UnitType,
        V: UnitType,
        W: UnitType
>(
    val price: PriceSchedule<U, V>,
    val quantity: QuantitySchedule<W, V>,
)
