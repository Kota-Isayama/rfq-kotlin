package com.example.economic.observable

import com.example.economic.base.math.Currency

sealed interface Index: Observable


enum class RateIndex: Index {
    JPY_TONA;
}


data class CurrencyPair(
    val baseCurrency: Currency,
    val quoteCurrency: Currency
) {
    init {
        require(baseCurrency != quoteCurrency) {
            "Base currency is not equal to quote currency $baseCurrency"
        }
    }
}


enum class InformationProvider{
    REFINITIV,
    BLOOMBERG;
}


data class FxSpotRateSource(
    val sourceProvider: InformationProvider
)

data class ForeignExchangeRateIndex(
    val currencyPair: CurrencyPair,
    val fixingSource: FxSpotRateSource,
): Index
