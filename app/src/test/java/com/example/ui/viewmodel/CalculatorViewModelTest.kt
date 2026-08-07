package com.example.ui.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class CalculatorViewModelTest {

    private lateinit var viewModel: CalculatorViewModel

    @Before
    fun setup() {
        viewModel = CalculatorViewModel()
    }

    @Test
    fun testBasicAddition() {
        viewModel.onDigit("1")
        viewModel.onDigit("2")
        viewModel.onOperator("+")
        viewModel.onDigit("8")
        viewModel.onEquals()

        val state = viewModel.uiState.value
        assertEquals("20", state.result)
    }

    @Test
    fun testDivisionByZero() {
        viewModel.onDigit("9")
        viewModel.onOperator("÷")
        viewModel.onDigit("0")
        viewModel.onEquals()

        val state = viewModel.uiState.value
        assertEquals("Cannot divide by zero", state.errorMessage)
        assertEquals("Error", state.result)
    }

    @Test
    fun testMemoryOperations() {
        viewModel.onDigit("5")
        viewModel.onDigit("0")
        viewModel.onMemoryAdd() // Memory becomes 50

        var state = viewModel.uiState.value
        assertEquals(50.0, state.memoryValue, 0.001)

        viewModel.onClear()
        viewModel.onMemoryRecall() // Recalls 50

        state = viewModel.uiState.value
        assertEquals("50", state.expression)
    }
}
