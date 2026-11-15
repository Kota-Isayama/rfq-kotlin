package com.example.economic

sealed interface Payout


data class InterestRatePayout(
    val payerReceiver: PayerReceiver,
    val currency: Currency,
    val notional: ValueSchedule<Notional>,
    val calculationPeriodDates: CalculationPeriodDates,
    val paymentDates: PaymentDates,
    val dayCountFraction: DayCountFraction,
    val rateSpecification: RateSpecification,
): Payout
