package com.example.economic

data class BusinessDayAdjustments(
    val convention: BusinessDayConvention,
    val centers: Set<BusinessCenter>
) {
    val businessCenters = centers.toSet()
        get() = field.toSet()

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
}
