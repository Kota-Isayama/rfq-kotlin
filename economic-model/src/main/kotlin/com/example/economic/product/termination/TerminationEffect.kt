package com.example.economic.product.termination

import com.example.economic.base.datetime.BusinessDayAdjustments
import com.example.economic.product.payout.PayoutId
import com.example.economic.base.datetime.DayType
import com.example.economic.base.datetime.DaysOffset


sealed interface TerminationEffect


sealed interface AccruedInterestTreatment: TerminationEffect {
    val payoutIds: Set<PayoutId>
}


data class WaiveAccruedInterest(
    override val payoutIds: Set<PayoutId>
) : AccruedInterestTreatment {
    init {
        require(payoutIds.isNotEmpty()) { "PayoutId must not be empty" }
    }
}


data class PayAccruedInterest(
    override val payoutIds: Set<PayoutId>,
    val paymentDateRule: AccruedInterestPaymentDateRule
): AccruedInterestTreatment {
    init {
        require(payoutIds.isNotEmpty()) {
            "PayoutId must not be empty"
        }
    }
}


sealed interface AccruedInterestPaymentDateRule

// 元々の契約内容の利払い日と同一
data object OriginalPaymentDate: AccruedInterestPaymentDateRule

// 短縮された計算期間に既存の支払規約を適用
data object RecalculatePaymentDate: AccruedInterestPaymentDateRule

// KO固有の設定
data class RelativePaymentDate(
    val relativeTo: AccruedInterestPaymentDateRelativeTo,
    val paymentDateOffset: DaysOffset?,
    val paymentDateAdjustments: BusinessDayAdjustments
): AccruedInterestPaymentDateRule {
    enum class AccruedInterestPaymentDateRelativeTo {
        TRIGGER_DATE,
        TERMINATION_DATE;
    }

    init {
        if (paymentDateOffset?.dayType == DayType.BUSINESS) {
            require(paymentDateAdjustments.businessCenters.isNotEmpty()) { "Business Centers must not be empty when the offset is business days." }
        }
    }
}

