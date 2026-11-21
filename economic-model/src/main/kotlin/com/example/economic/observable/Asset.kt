package com.example.economic.observable

import com.example.economic.base.math.Currency

sealed interface Asset: Observable


data class AssetIdentifier(
    val value: String,
    val type: AssetIdentifierType,
) {
    enum class AssetIdentifierType {
        ISIN,
        INTERNAL;
    }

    init {
        require(value.isNotBlank()) {
            "Value must not be blank"
        }
    }
}


data class Cash(
    val currency: Currency,
): Asset


data class Security(
    val identifier: AssetIdentifier,
    val securityType: SecurityType
): Asset {
    enum class SecurityType {
        DEBT,
        EQUITY,
        FUND;
    }
}