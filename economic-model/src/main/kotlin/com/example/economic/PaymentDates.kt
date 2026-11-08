package com.example.economic

enum class PayRelativeTo {
    CALCULATION_PERIOD_END_DATE;
}


data class PaymentDates(
    val paymentFrequency: Frequency,
    val payRelativeTo: PayRelativeTo,
    val paymentDateOffset: PaymentDaysOffset?,
    val paymentDatesAdjustments: BusinessDayAdjustments
) {
    init {
        if (paymentDateOffset?.dayType == DayType.BUSINESS) {
            require(paymentDatesAdjustments.convention != BusinessDayConvention.NONE) {
                "When offset day type is BUSINESS, day convention must not be NONE"
            }
        }
    }
}
