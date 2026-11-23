package com.example.economic.product.rate

import java.time.LocalDate

data class DigitalRateFixing(
    val reference: PeriodBoundary,
    val fixingDate: LocalDate,
)

