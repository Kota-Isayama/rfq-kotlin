package com.example.economic

import java.util.Collections

@ConsistentCopyVisibility
data class BusinessDayAdjustments private constructor (
    val convention: BusinessDayConvention,
    val businessCenters: Set<BusinessCenter>
) {
    init {
        when (convention) {
            BusinessDayConvention.MODFOLLOWING,
            BusinessDayConvention.FOLLOWING,
            BusinessDayConvention.MODPRECEDING,
            BusinessDayConvention.PRECEDING -> {
                require(!businessCenters.isEmpty()) {
                    "When business day convention is specified, business center have to be specified."
                }
            }

            BusinessDayConvention.NONE -> {
                require(businessCenters.isEmpty()) {
                    "When business day convention is not specified, business center must not be specified."
                }
            }
        }
    }

    companion object {
        fun create(convention: BusinessDayConvention, centers: Set<BusinessCenter>): BusinessDayAdjustments {
            return BusinessDayAdjustments(
                convention,
                Collections.unmodifiableSet(centers.toMutableSet())
            )
        }
    }
}
