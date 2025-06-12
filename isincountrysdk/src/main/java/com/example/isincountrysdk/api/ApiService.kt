package com.example.isincountrysdk.api

import com.example.isincountrysdk.models.CreateCountryRequest
import com.example.isincountrysdk.models.LocationCheckRequest
import com.example.isincountrysdk.models.LocationCheckResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("countries/check-location")
    fun checkLocation(@Body request: LocationCheckRequest): Call<LocationCheckResponse>

    @POST("countries/create")
    fun createCountry(@Body request: CreateCountryRequest): Call<Void>
    companion object {
        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://is-in-country-api.onrender.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}