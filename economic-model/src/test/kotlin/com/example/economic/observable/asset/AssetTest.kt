package com.example.economic.observable.asset

import com.example.economic.observable.AssetIdentifier
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class AssetTest {
    @Test
    fun `asset identifier must not be blank`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            AssetIdentifier(
                value = "",
                type = AssetIdentifier.AssetIdentifierType.INTERNAL
            )
        }
    }
}