package com.example.economic

import java.math.BigDecimal


sealed interface UnitType


object Dimensionless: UnitType


enum class Currency : UnitType {
    JPY,
    USD,
    AUD,
    EUR;
}


enum class FinancialUnit: UnitType {
    SHARE,                  // 株数          トヨタ株1,000株
    CONTRACT,               // 契約数        日経225先物10枚
    CONTRACTUAL_PRODUCT,    // 契約商品全体   オプション契約全体に対するプレミアム
    INDEX_UNIT;             // 指数ポイント   日経平均40,000ポイント
}


data class Measure<out U: UnitType>(
    val value: BigDecimal,
    val unit: U
)


data class MeasureSchedule<out U: UnitType>(
    val valueSchedule: ValueSchedule<BigDecimal>,
    val unit: U
)