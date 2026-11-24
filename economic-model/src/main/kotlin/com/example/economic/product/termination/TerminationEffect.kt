package com.example.economic.product.termination

import com.example.economic.base.datetime.BusinessDayAdjustments
import com.example.economic.product.payout.PayoutId
import com.example.economic.base.datetime.DayType
import com.example.economic.base.datetime.DaysOffset
import com.example.economic.base.datetime.RelativeDateRule


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
    val dateRule: RelativeDateRule,
): AccruedInterestPaymentDateRule {
    enum class AccruedInterestPaymentDateRelativeTo {
        TRIGGER_DATE,
        TERMINATION_DATE;
    }
}


sealed interface TerminationDateRule


data class RelativeTerminationDate(
    val relativeTo: TerminationDateRelativeTo,
    val dateRule: RelativeDateRule,
): TerminationDateRule {
    enum class TerminationDateRelativeTo {
        TRIGGER_DATE;
    }
}


enum class TargetSettlementStyle {
    EXCLUSIVE,
    INCLUSIVE,
    EXACT;
}

enum class ExactAdjustmentMethod {
    VARIED_NOTIONAL,
    VARIED_STRIKE;
}


data class TargetSettlementTreatment(
    val style: TargetSettlementStyle,
    val adjustmentMethod: ExactAdjustmentMethod?
): TerminationEffect {
    init {
        require((style == TargetSettlementStyle.EXCLUSIVE) == (adjustmentMethod != null)) {
            "Adjustment method must be specified if and only if style is EXACT."
        }
    }
}
