package com.example.economic.product.schedule

import com.example.economic.base.datetime.BusinessDayAdjustments
import com.example.economic.base.datetime.BusinessDayConvention
import com.example.economic.base.datetime.DayType
import com.example.economic.base.datetime.DaysOffset
import com.example.economic.base.datetime.Frequency

enum class PayRelativeTo {
    CALCULATION_PERIOD_END_DATE;
}


data class PaymentDates(
    val paymentFrequency: Frequency,
    val payRelativeTo: PayRelativeTo,
    val paymentDateOffset: DaysOffset?,
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
