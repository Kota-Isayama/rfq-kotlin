package com.example.economic


enum class DayType {
    BUSINESS,
    CALENDAR;
}


data class PaymentDaysOffset(
    val days: Int,
    val dayType: DayType,
) {
    init {
        require (days != 0) {
            "Zero days offset is not allowed."
        }
    }
}
