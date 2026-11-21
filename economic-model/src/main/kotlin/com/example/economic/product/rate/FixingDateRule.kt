package com.example.economic.product.rate

import com.example.economic.base.datetime.BusinessCenter
import com.example.economic.base.datetime.BusinessDayAdjustments

sealed interface FixingDateRule


data class BusinessDayFixingOffset(
    val offset: Int,
    val businessCenters: Set<BusinessCenter>,
): FixingDateRule {
    init {
        require(offset != 0) {
            "Offset must be non-zero."
        }
    }
}


data class AdjustedReferenceDate(
    val adjustments: BusinessDayAdjustments
): FixingDateRule