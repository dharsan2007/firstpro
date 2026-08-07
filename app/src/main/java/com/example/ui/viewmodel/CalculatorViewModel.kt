package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.HistoryManager
import com.example.ui.theme.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.abs

class CalculatorViewModel(
    private val historyManager: HistoryManager = HistoryManager()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            historyManager.history.collect { list ->
                _uiState.update { it.copy(historyList = list) }
            }
        }
    }

    fun onDigit(digit: String) {
        _uiState.update { state ->
            val newExpr = if (state.errorMessage != null) digit else state.expression + digit
            val liveRes = calculateLiveResult(newExpr)
            state.copy(
                expression = newExpr,
                errorMessage = if (liveRes.first) null else state.errorMessage,
                result = if (liveRes.first) liveRes.second else state.result
            )
        }
    }

    fun onDecimal() {
        _uiState.update { state ->
            val expr = state.expression
            val lastNumberToken = getLastNumberToken(expr)
            if (lastNumberToken.contains(".")) {
                return@update state
            }
            val newExpr = if (expr.isEmpty() || isOperator(expr.last())) "${expr}0." else "$expr."
            val liveRes = calculateLiveResult(newExpr)
            state.copy(
                expression = newExpr,
                result = if (liveRes.first) liveRes.second else state.result
            )
        }
    }

    fun onOperator(op: String) {
        _uiState.update { state ->
            var expr = state.expression
            if (expr.isEmpty()) {
                // If expression is empty, use current result as base if valid
                if (state.result.isNotEmpty() && state.result != "0" && state.errorMessage == null) {
                    expr = state.result.replace(",", "")
                } else if (op == "−" || op == "-") {
                    // Allow starting with negative
                    val newExpr = "−"
                    return@update state.copy(expression = newExpr, errorMessage = null)
                } else {
                    return@update state
                }
            }

            val lastChar = expr.last()
            val newExpr = if (isOperator(lastChar)) {
                // Replace last operator with new operator
                expr.dropLast(1) + op
            } else {
                "$expr $op "
            }

            state.copy(
                expression = newExpr,
                errorMessage = null
            )
        }
    }

    fun onPercent() {
        _uiState.update { state ->
            val expr = state.expression
            if (expr.isEmpty()) return@update state

            val lastToken = getLastNumberToken(expr)
            if (lastToken.isEmpty()) return@update state

            val numVal = lastToken.toDoubleOrNull() ?: return@update state
            val percentVal = numVal / 100.0
            val formattedPercent = formatResult(percentVal)

            val newExpr = expr.substring(0, expr.length - lastToken.length) + formattedPercent
            val liveRes = calculateLiveResult(newExpr)

            state.copy(
                expression = newExpr,
                result = if (liveRes.first) liveRes.second else state.result,
                errorMessage = null
            )
        }
    }

    fun onNegate() {
        _uiState.update { state ->
            val expr = state.expression
            if (expr.isEmpty()) {
                val currentNum = state.result.replace(",", "").toDoubleOrNull() ?: 0.0
                val negated = -currentNum
                val formatted = formatResult(negated)
                return@update state.copy(result = formatted)
            }

            val lastToken = getLastNumberToken(expr)
            if (lastToken.isEmpty()) return@update state

            val prefix = expr.substring(0, expr.length - lastToken.length)
            val newLastToken = if (lastToken.startsWith("−") || lastToken.startsWith("-")) {
                lastToken.substring(1)
            } else {
                "−$lastToken"
            }

            val newExpr = prefix + newLastToken
            val liveRes = calculateLiveResult(newExpr)

            state.copy(
                expression = newExpr,
                result = if (liveRes.first) liveRes.second else state.result,
                errorMessage = null
            )
        }
    }

    fun onClear() {
        _uiState.update { state ->
            if (state.expression.isNotEmpty()) {
                state.copy(expression = "", errorMessage = null)
            } else {
                state.copy(expression = "", result = "0", errorMessage = null)
            }
        }
    }

    fun onDelete() {
        _uiState.update { state ->
            if (state.expression.isEmpty()) return@update state
            val trimmed = state.expression.trimEnd()
            val newExpr = if (trimmed.endsWith(" ")) {
                trimmed.dropLast(1).trimEnd()
            } else {
                trimmed.dropLast(1)
            }

            val liveRes = calculateLiveResult(newExpr)
            state.copy(
                expression = newExpr,
                result = if (newExpr.isEmpty()) "0" else (if (liveRes.first) liveRes.second else state.result),
                errorMessage = null
            )
        }
    }

    fun onEquals() {
        val state = _uiState.value
        val expr = state.expression.ifEmpty { state.result }
        if (expr.isEmpty()) return

        try {
            val evalResult = evaluateExpression(expr)
            val formatted = formatResult(evalResult)

            historyManager.addEntry(expr, formatted)

            _uiState.update {
                it.copy(
                    previousExpression = expr,
                    expression = "",
                    result = formatted,
                    errorMessage = null
                )
            }
        } catch (e: ArithmeticException) {
            _uiState.update {
                it.copy(
                    errorMessage = e.message ?: "Cannot divide by zero",
                    result = "Error"
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    errorMessage = "Invalid Expression",
                    result = "Error"
                )
            }
        }
    }

    // Memory operations
    fun onMemoryClear() {
        _uiState.update { it.copy(memoryValue = 0.0) }
    }

    fun onMemoryRecall() {
        val state = _uiState.value
        if (state.memoryValue == 0.0) return
        val memFormatted = formatResult(state.memoryValue)
        onDigit(memFormatted)
    }

    fun onMemoryAdd() {
        val currentVal = getCurrentValForMemory()
        _uiState.update { it.copy(memoryValue = it.memoryValue + currentVal) }
    }

    fun onMemorySubtract() {
        val currentVal = getCurrentValForMemory()
        _uiState.update { it.copy(memoryValue = it.memoryValue - currentVal) }
    }

    private fun getCurrentValForMemory(): Double {
        val state = _uiState.value
        return if (state.expression.isNotEmpty()) {
            try { evaluateExpression(state.expression) } catch (e: Exception) { 0.0 }
        } else {
            state.result.replace(",", "").toDoubleOrNull() ?: 0.0
        }
    }

    // History BottomSheet
    fun toggleHistory(show: Boolean? = null) {
        _uiState.update {
            it.copy(isHistoryVisible = show ?: !it.isHistoryVisible)
        }
    }

    fun clearHistory() {
        historyManager.clearHistory()
    }

    fun reuseHistoryEntry(expression: String, result: String) {
        _uiState.update {
            it.copy(
                expression = expression,
                result = result,
                isHistoryVisible = false,
                errorMessage = null
            )
        }
    }

    // Theme Switch
    fun setThemeMode(mode: ThemeMode) {
        _uiState.update { it.copy(themeMode = mode) }
    }

    // Expression Evaluator Engine
    private fun calculateLiveResult(expr: String): Pair<Boolean, String> {
        if (expr.isBlank()) return Pair(true, "0")
        return try {
            val res = evaluateExpression(expr)
            Pair(true, formatResult(res))
        } catch (e: Exception) {
            Pair(false, "")
        }
    }

    private fun evaluateExpression(exprStr: String): Double {
        val cleaned = exprStr
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .trim()

        if (cleaned.isEmpty()) return 0.0

        // Parse tokens
        val tokens = mutableListOf<String>()
        var currentToken = StringBuilder()

        var i = 0
        while (i < cleaned.length) {
            val c = cleaned[i]
            if (c == ' ') {
                if (currentToken.isNotEmpty()) {
                    tokens.add(currentToken.toString())
                    currentToken = StringBuilder()
                }
            } else if (c == '+' || c == '*' || c == '/' || c == '%') {
                if (currentToken.isNotEmpty()) {
                    tokens.add(currentToken.toString())
                    currentToken = StringBuilder()
                }
                tokens.add(c.toString())
            } else if (c == '-') {
                // Check if '-' is binary operator or unary minus
                if (currentToken.isEmpty() && (tokens.isEmpty() || isOperatorToken(tokens.last()))) {
                    currentToken.append(c)
                } else {
                    if (currentToken.isNotEmpty()) {
                        tokens.add(currentToken.toString())
                        currentToken = StringBuilder()
                    }
                    tokens.add("-")
                }
            } else {
                currentToken.append(c)
            }
            i++
        }
        if (currentToken.isNotEmpty()) {
            tokens.add(currentToken.toString())
        }

        if (tokens.isEmpty()) return 0.0

        // Process Multiplication, Division, Percentage first
        val values = mutableListOf<Double>()
        val ops = mutableListOf<String>()

        var idx = 0
        while (idx < tokens.size) {
            val token = tokens[idx]
            if (isOperatorToken(token)) {
                if (token == "*") {
                    if (idx + 1 < tokens.size && values.isNotEmpty()) {
                        val nextVal = tokens[idx + 1].toDouble()
                        val prevVal = values.removeAt(values.size - 1)
                        values.add(prevVal * nextVal)
                        idx++
                    }
                } else if (token == "/") {
                    if (idx + 1 < tokens.size && values.isNotEmpty()) {
                        val nextVal = tokens[idx + 1].toDouble()
                        if (nextVal == 0.0) throw ArithmeticException("Cannot divide by zero")
                        val prevVal = values.removeAt(values.size - 1)
                        values.add(prevVal / nextVal)
                        idx++
                    }
                } else if (token == "%") {
                    if (values.isNotEmpty()) {
                        val prevVal = values.removeAt(values.size - 1)
                        values.add(prevVal / 100.0)
                    }
                } else {
                    ops.add(token)
                }
            } else {
                token.toDoubleOrNull()?.let { values.add(it) }
            }
            idx++
        }

        if (values.isEmpty()) return 0.0

        var result = values[0]
        for (j in 0 until ops.size) {
            if (j + 1 < values.size) {
                val op = ops[j]
                val nextVal = values[j + 1]
                if (op == "+") {
                    result += nextVal
                } else if (op == "-") {
                    result -= nextVal
                }
            }
        }

        return result
    }

    private fun isOperator(c: Char): Boolean {
        return c == '+' || c == '−' || c == '-' || c == '×' || c == '*' || c == '÷' || c == '/'
    }

    private fun isOperatorToken(token: String): Boolean {
        return token == "+" || token == "-" || token == "*" || token == "/" || token == "%"
    }

    private fun getLastNumberToken(expr: String): String {
        val parts = expr.split(" ")
        return parts.lastOrNull { it.isNotEmpty() && !isOperatorToken(it) } ?: ""
    }

    private fun formatResult(value: Double): String {
        if (value.isNaN() || value.isInfinite()) return "Error"
        if (abs(value - value.toLong()) < 1e-9) {
            val df = DecimalFormat("#,###", DecimalFormatSymbols(Locale.US))
            return df.format(value.toLong())
        }
        val df = DecimalFormat("#,##0.########", DecimalFormatSymbols(Locale.US))
        return df.format(value)
    }
}
