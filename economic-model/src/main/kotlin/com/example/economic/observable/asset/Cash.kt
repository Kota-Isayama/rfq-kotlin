package com.example.economic.observable.asset

import com.example.economic.base.math.Currency

data class Cash(
    val currency: Currency,
): Asset
