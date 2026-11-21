package com.example.economic.observable.asset

interface Instrument : Asset


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
