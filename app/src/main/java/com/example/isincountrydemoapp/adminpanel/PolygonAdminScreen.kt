package com.example.isincountrydemoapp.adminpanel

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.isincountrydemoapp.adminpanel.api.CountriesApi
import com.example.isincountrysdk.models.Country
import com.example.isincountrysdk.models.GeoJson
import kotlinx.coroutines.launch

@Composable
fun PolygonAdminScreen() {
    val api = remember { CountriesApi.create() }
    val scope = rememberCoroutineScope()

    var countries by remember { mutableStateOf<List<Country>>(emptyList()) }
    var name by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var coordinates by remember {
        mutableStateOf(
            mutableListOf(
                mutableStateOf(Pair("", "")) // default one point
            )
        )
    }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        countries = try {
            api.getCountries()
        } catch (e: Exception) {
            emptyList()
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Polygon Admin Panel", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Country Name") })
        OutlinedTextField(value = code, onValueChange = { code = it }, label = { Text("Country Code") })

        Spacer(Modifier.height(16.dp))

        Text("Polygon Coordinates", style = MaterialTheme.typography.titleMedium)
        coordinates.forEachIndexed { index, coordState ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = coordState.value.first,
                    onValueChange = { coordinates[index].value = it to coordState.value.second },
                    label = { Text("Longitude") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = coordState.value.second,
                    onValueChange = { coordinates[index].value = coordState.value.first to it },
                    label = { Text("Latitude") },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Button(onClick = {
            coordinates.add(mutableStateOf("" to ""))
        }) {
            Text("Add Coordinate Point")
        }

        Spacer(Modifier.height(16.dp))

        Button(onClick = {
            try {
                val coordList: List<List<Double>> = coordinates.mapIndexed { i, pairState ->
                    val lng = pairState.value.first.toDoubleOrNull()
                    val lat = pairState.value.second.toDoubleOrNull()

                    if (lng == null || lat == null) throw IllegalArgumentException("Invalid coordinate at row ${i + 1}")
                    listOf(lng, lat)
                }

                // Build the GeoJson
                val geoJson = GeoJson(
                    type = "MultiPolygon",
                    coordinates = listOf(
                        listOf(
                            coordList + listOf(coordList.first()) // Close polygon
                        )
                    )
                )

                val country = Country(null, name, code, geoJson)

                scope.launch {
                    api.createCountry(country)
                    countries = api.getCountries()
                    name = ""
                    code = ""
                    coordinates = mutableListOf(mutableStateOf("" to ""))
                    errorMessage = null
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            }
        }) {
            Text("Add Country")
        }

        errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(24.dp))
        Text("Existing Countries", style = MaterialTheme.typography.titleMedium)

        LazyColumn {
            items(countries) { country ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Column {
                        Text("${country.name} (${country.code})")
                        Text("Points: ${country.geoJson.coordinates.firstOrNull()?.firstOrNull()?.size ?: 0}")
                    }
                    IconButton(onClick = {
                        scope.launch {
                            country._id?.let { api.deleteCountry(it) }
                            countries = api.getCountries()
                        }
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }
        }
    }
}
