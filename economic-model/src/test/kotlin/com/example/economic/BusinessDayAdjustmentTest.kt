package com.example.economic

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class BusinessDayAdjustmentTest {
    @Test
    fun `BusinessDayAdjustment with specific business day convention accepts business centers`() {
        BusinessDayAdjustment(
            BusinessDayConvention.MODFOLLOWING,
            setOf(BusinessCenter("JPTO"))
        )
    }

    @Test
    fun `BusinessDayAdjustment with specific convention rejects business centers`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            BusinessDayAdjustment(
                BusinessDayConvention.FOLLOWING,
                setOf()
            )
        }
    }

    @Test
    fun `BusinessDayAdjustment with none convention accepts empty business centers`() {
        BusinessDayAdjustment(
            BusinessDayConvention.NONE,
            emptySet()
        )
    }

    @Test
    fun `BusinessDayAdjustment with none convention rejects business centers`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            BusinessDayAdjustment(
                BusinessDayConvention.NONE,
                setOf(BusinessCenter("JPTO"))
            )
        }
    }
}
