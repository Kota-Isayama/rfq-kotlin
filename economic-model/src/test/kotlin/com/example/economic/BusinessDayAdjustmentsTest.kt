package com.example.economic

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class BusinessDayAdjustmentsTest {
    @Test
    fun `BusinessDayAdjustments with specific business day convention accepts business centers`() {
        BusinessDayAdjustments(
            BusinessDayConvention.MODFOLLOWING,
            setOf(BusinessCenter("JPTO"))
        )
    }

    @Test
    fun `BusinessDayAdjustments with specific convention rejects business centers`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            BusinessDayAdjustments(
                BusinessDayConvention.FOLLOWING,
                setOf()
            )
        }
    }

    @Test
    fun `BusinessDayAdjustments with none convention accepts empty business centers`() {
        BusinessDayAdjustments(
            BusinessDayConvention.NONE,
            emptySet()
        )
    }

    @Test
    fun `BusinessDayAdjustments with none convention rejects business centers`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            BusinessDayAdjustments(
                BusinessDayConvention.NONE,
                setOf(BusinessCenter("JPTO"))
            )
        }
    }
}
