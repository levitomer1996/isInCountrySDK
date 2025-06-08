package com.example.isincountrydemoapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.isincountrydemoapp.adminpanel.PolygonAdminActivity
import com.example.isincountrydemoapp.ui.theme.IsInCountryDemoAppTheme

class MainActivity : ComponentActivity() {
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private val sdk = IsInCountry()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            Log.d("Permission", "Granted? $isGranted")
            if (!isGranted) {
                Log.e("Permission", "Location permission denied")
            }
        }

        checkLocationPermission()

        enableEdgeToEdge()
        setContent {
            IsInCountryDemoAppTheme {
                LocationCheckScreen(sdk = sdk)
            }
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}

@Composable
fun LocationCheckScreen(sdk: IsInCountry) {
    val context = LocalContext.current
    var result by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    var latInput by remember { mutableStateOf(TextFieldValue("")) }
    var lngInput by remember { mutableStateOf(TextFieldValue("")) }
    var countryCodeInput by remember { mutableStateOf(TextFieldValue("")) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            OutlinedTextField(
                value = latInput,
                onValueChange = { latInput = it },
                label = { Text("Latitude") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = lngInput,
                onValueChange = { lngInput = it },
                label = { Text("Longitude") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = countryCodeInput,
                onValueChange = { countryCodeInput = it },
                label = { Text("Country Code (e.g., IL)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                val lat = latInput.text.toDoubleOrNull()
                val lng = lngInput.text.toDoubleOrNull()
                val countryCode = countryCodeInput.text.trim().uppercase()

                if (lat == null || lng == null) {
                    result = "Please enter valid coordinates"
                    return@Button
                }

                if (countryCode.isEmpty()) {
                    result = "Please enter a valid country code"
                    return@Button
                }

                isLoading = true
                result = null

                sdk.checkManualLocation(context, lat, lng, countryCode) { inside ->
                    Log.d("UI", "✅ Manual callback: $inside")
                    result = if (inside) "✅ Inside $countryCode" else "❌ Outside $countryCode"
                    isLoading = false
                }
            }) {
                Text("Check Manual Location")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                Log.d("UI", "📌 Auto check button pressed")
                isLoading = true
                result = null

                sdk.isInCountry(context = context, targetCountry = "IL") { isInIsrael ->
                    Log.d("UI", "✅ Auto callback: $isInIsrael")
                    result = if (isInIsrael) "✅ You are in Israel" else "❌ You are not in Israel"
                    isLoading = false
                }
            }) {
                Text("Check Current Location")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                context.startActivity(Intent(context, PolygonAdminActivity::class.java))
            }) {
                Text("Open Polygon Admin Panel")
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                CircularProgressIndicator()
            }

            result?.let {
                Text(it, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
