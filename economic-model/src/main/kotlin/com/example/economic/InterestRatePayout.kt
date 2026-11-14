package com.example.economic

data class InterestRatePayout(
    val payerReceiver: PayerReceiver,
    val currency: Currency,
    val notional: Notional,
    val calculationPeriodDates: CalculationPeriodDates,
    val paymentDates: PaymentDates,
    val dayCountFraction: DayCountFraction,
    val rateSpecification: RateSpecification,
)
