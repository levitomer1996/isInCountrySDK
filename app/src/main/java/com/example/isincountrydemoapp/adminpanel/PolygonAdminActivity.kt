package com.example.isincountrydemoapp.adminpanel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.isincountrydemoapp.ui.theme.IsInCountryDemoAppTheme

class PolygonAdminActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IsInCountryDemoAppTheme {
                PolygonAdminScreen()
            }
        }
    }
}