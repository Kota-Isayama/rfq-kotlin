package com.example.economic.observable

sealed interface ValueExpression


data class ObservableValue(
    val observable: Observable
): ValueExpression


enum class ComparisonOperator {
    LESS,
    LESS_OR_EQUAL,
    EQUAL,
    GREATER_OR_EQUAL,
    GREATER
}