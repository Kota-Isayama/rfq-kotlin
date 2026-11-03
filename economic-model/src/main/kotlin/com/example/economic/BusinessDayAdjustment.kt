package com.example.economic

data class BusinessDayAdjustment(
    val convention: BusinessDayConvention,
    val businessCenter: Set<BusinessCenter>
) {
    init {
        when (convention) {
            BusinessDayConvention.MODFOLLOWING -> {
                require(!businessCenter.isEmpty()) {
                    "When business day convention is specified, business center have to be specified."
                }
            }

            BusinessDayConvention.FOLLOWING -> {
                require(!businessCenter.isEmpty()) {
                    "When business day convention is specified, business center have to be specified."
                }
            }

            BusinessDayConvention.MODPRECEDING -> {
                require(!businessCenter.isEmpty()) {
                    "When business day convention is specified, business center have to be specified."
                }
            }

            BusinessDayConvention.PRECEDING -> {
                require(!businessCenter.isEmpty()) {
                    "When business day convention is specified, business center have to be specified."
                }
            }

            BusinessDayConvention.NONE -> {
                require(businessCenter.isEmpty()) {
                    "When business day convention is not specified, business center must not be specified."
                }
            }
        }
    }
}
