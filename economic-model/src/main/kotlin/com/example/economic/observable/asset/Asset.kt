package com.example.economic.observable.asset

sealed interface Asset


data class AssetIdentifier(
    val value: String,
    val type: AssetIdentifierType,
) {
    enum class AssetIdentifierType {
        ISIN,
        INTERNAL;
    }
}
