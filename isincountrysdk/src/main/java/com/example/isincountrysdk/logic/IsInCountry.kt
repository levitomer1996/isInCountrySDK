package com.example.isincountrysdk.logic


import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

import android.content.Context
import androidx.annotation.RequiresPermission
import com.example.isincountrysdk.api.ApiService
import com.example.isincountrysdk.models.LocationCheckRequest
import com.example.isincountrysdk.models.LocationCheckResponse
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class IsInCountry {
    private val api = ApiService.create()

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun isInCountry(context: Context, targetCountry: String, callback: (Boolean) -> Unit) {
        val locationClient = LocationServices.getFusedLocationProviderClient(context)

        val permissionGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!permissionGranted) {
            callback(false)
            return
        }

        locationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val request = LocationCheckRequest(
                    lat = location.latitude,
                    lng = location.longitude,
                    code = targetCountry
                )
                api.checkLocation(request).enqueue(object : Callback<LocationCheckResponse> {
                    override fun onResponse(
                        call: Call<LocationCheckResponse>,
                        response: Response<LocationCheckResponse>
                    ) {
                        callback(response.body()?.inside == true)
                    }

                    override fun onFailure(call: Call<LocationCheckResponse>, t: Throwable) {
                        callback(false)
                    }
                })
            } else {
                callback(false)
            }
        }
    }
}
