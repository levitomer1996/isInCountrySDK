package com.example.isincountrysdk.models

data class Country(
    val _id: String? = null,
    val name: String,
    val code: String,
    val geoJson: GeoJson
)