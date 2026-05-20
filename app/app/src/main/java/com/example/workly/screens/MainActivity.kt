package com.example.workly.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.workly.navigation.AppNavigation
import com.example.workly.ui.theme.WorklyTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WorklyTheme {
                AppNavigation()
            }
        }
    }
}