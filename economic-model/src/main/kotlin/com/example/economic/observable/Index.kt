package com.example.economic.observable

sealed interface Index: Observable


enum class RateIndex: Index {
    JPY_TONA;
}