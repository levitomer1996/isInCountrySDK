package com.example.isincountrysdk.models

data class LocationCheckRequest(
    val lat: Double,
    val lng: Double,
    val code: String
)