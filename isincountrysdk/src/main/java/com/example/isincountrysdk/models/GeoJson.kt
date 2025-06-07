package com.example.isincountrysdk.models

data class GeoJson(
    val type: String = "MultiPolygon",
    val coordinates: List<List<List<List<Double>>>>
)