package com.example.economic

sealed interface RollConvention


data class DayOfMonth(val day: Int): RollConvention {
    init {
        require (0 < day && day < 31) {
            "day must be greater than 0 and smaller than or equal to 30"
        }
    }
}


data object EndOfMonth: RollConvention