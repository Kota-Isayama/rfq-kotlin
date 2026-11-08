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
        if (paymentDatesAdjustments.convention == BusinessDayConvention.NONE) {
            require(paymentDateOffset?.dayType == DayType.CALENDAR) {
                "Payment dates convention must be specified, when the payment date offset day type is BUSINESS"
            }
        }
    }
}
