package com.example.isincountrydemoapp.adminpanel.api



import retrofit2.Response
import retrofit2.http.*
import com.example.isincountrysdk.models.Country

interface CountriesApi {
    @GET("countries")
    suspend fun getCountries(): List<Country>

    @POST("countries/create")
    suspend fun createCountry(@Body country: Country): Response<Unit>

    @DELETE("countries/{id}")
    suspend fun deleteCountry(@Path("id") id: String): Response<Unit>

    companion object {
        fun create(): CountriesApi {
            return retrofit2.Retrofit.Builder()
                .baseUrl("http://10.100.102.122:3000/") // או כתובת השרת שלך
                .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                .build()
                .create(CountriesApi::class.java)
        }
    }
}
