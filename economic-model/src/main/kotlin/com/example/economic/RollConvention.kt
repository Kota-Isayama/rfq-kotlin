package com.example.economic

sealed interface RollConvention


data class DayOfMonth(val day: Int): RollConvention {
    init {
        if (day <= 0 || 30 < day) {
            throw IllegalArgumentException("day must be greater than 0 and smaller than or equal to 30")
        }
    }
}


object EndOfMonth: RollConvention