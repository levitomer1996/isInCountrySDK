package com.example.isincountrydemoapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.isincountrysdk.api.ApiService
import com.example.isincountrysdk.models.LocationCheckRequest
import com.example.isincountrysdk.models.LocationCheckResponse
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class IsInCountry {
    private val api = ApiService.create()
    private val tag = "IsInCountry"

    fun isInCountry(context: Context, targetCountry: String, callback: (Boolean) -> Unit) {
        Log.d(tag, "📍 Starting location check for country: $targetCountry")


        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e(tag, "🚫 Location permission not granted")
            callback(false)
            return
        }

        val locationClient = LocationServices.getFusedLocationProviderClient(context)

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            1000L
        ).apply {
            setMinUpdateIntervalMillis(500L)
            setMaxUpdates(1)
            setWaitForAccurateLocation(true)
        }.build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation
                if (location != null) {
                    val lat = location.latitude
                    val lng = location.longitude

                    Log.d(tag, "✅ Got location: lat=$lat, lng=$lng")

                    val request = LocationCheckRequest(
                        lat = lat,
                        lng = lng,
                        code = targetCountry
                    )

                    Log.d(tag, "🌍 Sending request to server:")
                    Log.d(tag, "➡️ lat: ${request.lat}")
                    Log.d(tag, "➡️ lng: ${request.lng}")
                    Log.d(tag, "➡️ code: ${request.code}")

                    api.checkLocation(request).enqueue(object : Callback<LocationCheckResponse> {
                        override fun onResponse(
                            call: Call<LocationCheckResponse>,
                            response: Response<LocationCheckResponse>
                        ) {
                            val body = response.body()
                            Log.d(tag, "📨 Raw response: $response")
                            Log.d(tag, "📨 Response body: $body")

                            if (response.isSuccessful && body != null) {
                                Log.d(tag, "✅ Server response: inside=${body.inside}")
                                callback(body.inside)
                            } else {
                                Log.e(tag, "❌ Server error: code=${response.code()}, message=${response.message()}")
                                callback(false)
                            }
                        }

                        override fun onFailure(call: Call<LocationCheckResponse>, t: Throwable) {
                            Log.e(tag, "❌ API call failed: ${t.message}", t)
                            callback(false)
                        }
                    })
                } else {
                    Log.e(tag, "🚫 Location is null")
                    callback(false)
                }

                locationClient.removeLocationUpdates(this)
            }
        }

        Log.d(tag, "📡 Requesting location update...")
        locationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }
    fun checkManualLocation(
        context: Context,
        lat: Double,
        lng: Double,
        code: String,
        callback: (Boolean) -> Unit
    ) {
        Log.d(tag, "📍 Manual check with coordinates: lat=$lat, lng=$lng, code=$code")

        val request = LocationCheckRequest(
            lat = lat,
            lng = lng,
            code = code
        )

        api.checkLocation(request).enqueue(object : Callback<LocationCheckResponse> {
            override fun onResponse(
                call: Call<LocationCheckResponse>,
                response: Response<LocationCheckResponse>
            ) {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    Log.d(tag, "✅ Server response: inside=${body.inside}")
                    callback(body.inside)
                } else {
                    Log.e(tag, "❌ Server error: ${response.code()}, ${response.message()}")
                    callback(false)
                }
            }

            override fun onFailure(call: Call<LocationCheckResponse>, t: Throwable) {
                Log.e(tag, "❌ API call failed: ${t.message}", t)
                callback(false)
            }
        })
    }

}
