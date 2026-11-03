package com.example.economic

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class BusinessCenterTest {
    @ParameterizedTest
    @ValueSource(strings = ["", " ", "\n", "\t", " \n\t"])
    fun `Empty String is rejects`(value: String) {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            BusinessCenter(value)
        }
    }
}