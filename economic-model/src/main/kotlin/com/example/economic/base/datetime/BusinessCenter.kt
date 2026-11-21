package com.example.economic.base.datetime

data class BusinessCenter(val center: String) {
    init {
        require(center.trim() != "") {
            "Business Center cannot be empty"
        }
    }
}