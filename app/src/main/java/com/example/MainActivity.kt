package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.CalculatorScreen
import com.example.ui.theme.DharsanCalculatorTheme
import com.example.ui.viewmodel.CalculatorViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: CalculatorViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsState()

            DharsanCalculatorTheme(themeMode = uiState.themeMode) {
                CalculatorScreen(viewModel = viewModel)
            }
        }
    }
}
