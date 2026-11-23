package com.example.economic.base.datetime

data class RelativeDateRule(
    val offset: DaysOffset?,
    val adjustments: BusinessDayAdjustments
) {
    init {
        if (offset?.dayType == DayType.BUSINESS) {
            require(adjustments.businessCenters.isNotEmpty()) { "Business Centers must not be empty when the offset is business days." }
        }
    }
}
