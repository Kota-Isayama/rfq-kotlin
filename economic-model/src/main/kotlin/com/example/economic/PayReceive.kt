package com.example.economic

data class PayerReceiver(val payer: CounterpartyRole, val receiver: CounterpartyRole) {
    init {
        if (payer == receiver) {
            throw IllegalArgumentException("Payer and receiver cannot be the same")
        }
    }
}
