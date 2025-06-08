

package com.example.isincountrysdk.models

data class CreateCountryRequest(
    val name: String,
    val code: String,
    val geoJson: GeoJson
)


