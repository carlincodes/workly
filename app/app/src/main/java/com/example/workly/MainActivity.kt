package com.example.workly

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.workly.navigation.AppNavigation
import com.example.workly.ui.theme.WorklyTheme

class MainActivity : ComponentActivity() {

    companion object {
        private const val REQUEST_LOCATION_PERMISSIONS = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Solicitar permissões de localização em tempo de execução se necessário
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val missing = permissions.any { perm ->
            ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED
        }

        if (missing) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_LOCATION_PERMISSIONS)
        }

        setContent {
            WorklyTheme {
                AppNavigation()
            }
        }
    }
}