package com.example.economic.base.datetime


enum class DayType {
    BUSINESS,
    CALENDAR;
}


data class DaysOffset(
    val days: Int,
    val dayType: DayType,
) {
    init {
        require (days != 0) {
            "Zero days offset is not allowed."
        }
    }
}
