package com.example.data.local

import com.example.data.model.CalculationHistory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HistoryManager {
    private val _history = MutableStateFlow<List<CalculationHistory>>(emptyList())
    val history: StateFlow<List<CalculationHistory>> = _history.asStateFlow()

    fun addEntry(expression: String, result: String) {
        if (expression.isBlank() || result.isBlank() || result.contains("Cannot divide")) return
        val newEntry = CalculationHistory(expression = expression, result = result)
        _history.value = listOf(newEntry) + _history.value
    }

    fun clearHistory() {
        _history.value = emptyList()
    }

    fun deleteEntry(id: String) {
        _history.value = _history.value.filterNot { it.id == id }
    }
}
