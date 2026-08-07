package com.example.ui.viewmodel

import com.example.data.model.CalculationHistory
import com.example.ui.theme.ThemeMode

data class CalculatorUiState(
    val expression: String = "",
    val result: String = "0",
    val previousExpression: String = "",
    val memoryValue: Double = 0.0,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val isHistoryVisible: Boolean = false,
    val errorMessage: String? = null,
    val historyList: List<CalculationHistory> = emptyList()
) {
    val isMemoryStored: Boolean get() = memoryValue != 0.0
}
