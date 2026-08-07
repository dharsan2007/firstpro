package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.components.ButtonType
import com.example.ui.components.CalculatorButton
import com.example.ui.components.CalculatorDisplay
import com.example.ui.components.HistoryBottomSheet
import com.example.ui.viewmodel.CalculatorViewModel

@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("calculator_screen"),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Display Section
            CalculatorDisplay(
                expression = uiState.expression,
                result = uiState.result,
                previousExpression = uiState.previousExpression,
                errorMessage = uiState.errorMessage,
                isMemoryStored = uiState.isMemoryStored,
                themeMode = uiState.themeMode,
                historyCount = uiState.historyList.size,
                onToggleHistory = { viewModel.toggleHistory() },
                onThemeModeChange = { viewModel.setThemeMode(it) },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            )

            // Keypad Container with Geometric Balance Rounded Top Corners
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 20.dp)
                ) {
                    // Memory Controls Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CalculatorButton(
                            text = "MC",
                            onClick = { viewModel.onMemoryClear() },
                            buttonType = ButtonType.MEMORY,
                            modifier = Modifier.weight(1f),
                            testTag = "btn_mc"
                        )
                        CalculatorButton(
                            text = "MR",
                            onClick = { viewModel.onMemoryRecall() },
                            buttonType = ButtonType.MEMORY,
                            modifier = Modifier.weight(1f),
                            testTag = "btn_mr"
                        )
                        CalculatorButton(
                            text = "M-",
                            onClick = { viewModel.onMemorySubtract() },
                            buttonType = ButtonType.MEMORY,
                            modifier = Modifier.weight(1f),
                            testTag = "btn_m_minus"
                        )
                        CalculatorButton(
                            text = "M+",
                            onClick = { viewModel.onMemoryAdd() },
                            buttonType = ButtonType.MEMORY,
                            modifier = Modifier.weight(1f),
                            testTag = "btn_m_plus"
                        )
                    }

                    // Main Keypad Matrix (5 Rows)
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Row 1: C, +/-, %, ÷
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CalculatorButton(
                                text = "C",
                                onClick = { viewModel.onClear() },
                                buttonType = ButtonType.ACTION,
                                modifier = Modifier.weight(1f),
                                testTag = "btn_clear"
                            )
                            CalculatorButton(
                                text = "%",
                                onClick = { viewModel.onPercent() },
                                buttonType = ButtonType.ACTION,
                                modifier = Modifier.weight(1f),
                                testTag = "btn_percent"
                            )
                            CalculatorButton(
                                text = "⌫",
                                onClick = { viewModel.onDelete() },
                                buttonType = ButtonType.ACTION,
                                icon = Icons.AutoMirrored.Filled.Backspace,
                                modifier = Modifier.weight(1f),
                                testTag = "btn_delete"
                            )
                            CalculatorButton(
                                text = "÷",
                                onClick = { viewModel.onOperator("÷") },
                                buttonType = ButtonType.OPERATOR,
                                modifier = Modifier.weight(1f),
                                testTag = "btn_divide"
                            )
                        }

                        // Row 2: 7, 8, 9, ×
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CalculatorButton(
                                text = "7",
                                onClick = { viewModel.onDigit("7") },
                                buttonType = ButtonType.NUMBER,
                                modifier = Modifier.weight(1f),
                                testTag = "btn_7"
                            )
                            CalculatorButton(
                                text = "8",
                                onClick = { viewModel.onDigit("8") },
                                buttonType = ButtonType.NUMBER,
                                modifier = Modifier.weight(1f),
                                testTag = "btn_8"
                            )
                            CalculatorButton(
                                text = "9",
                                onClick = { viewModel.onDigit("9") },
                                buttonType = ButtonType.NUMBER,
                                modifier = Modifier.weight(1f),
                                testTag = "btn_9"
                            )
                            CalculatorButton(
                                text = "×",
                                onClick = { viewModel.onOperator("×") },
                                buttonType = ButtonType.OPERATOR,
                                modifier = Modifier.weight(1f),
                                testTag = "btn_multiply"
                            )
                        }

                        // Row 3: 4, 5, 6, −
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CalculatorButton(
                                text = "4",
                                onClick = { viewModel.onDigit("4") },
                                buttonType = ButtonType.NUMBER,
                                modifier = Modifier.weight(1f),
                                testTag = "btn_4"
                            )
                            CalculatorButton(
                                text = "5",
                                onClick = { viewModel.onDigit("5") },
                                buttonType = ButtonType.NUMBER,
                                modifier = Modifier.weight(1f),
                                testTag = "btn_5"
                            )
                            CalculatorButton(
                                text = "6",
                                onClick = { viewModel.onDigit("6") },
                                buttonType = ButtonType.NUMBER,
                                modifier = Modifier.weight(1f),
                                testTag = "btn_6"
                            )
                            CalculatorButton(
                                text = "−",
                                onClick = { viewModel.onOperator("−") },
                                buttonType = ButtonType.OPERATOR,
                                modifier = Modifier.weight(1f),
                                testTag = "btn_subtract"
                            )
                        }

                        // Row 4: 1, 2, 3, +
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CalculatorButton(
                                text = "1",
                                onClick = { viewModel.onDigit("1") },
                                buttonType = ButtonType.NUMBER,
                                modifier = Modifier.weight(1f),
                                testTag = "btn_1"
                            )
                            CalculatorButton(
                                text = "2",
                                onClick = { viewModel.onDigit("2") },
                                buttonType = ButtonType.NUMBER,
                                modifier = Modifier.weight(1f),
                                testTag = "btn_2"
                            )
                            CalculatorButton(
                                text = "3",
                                onClick = { viewModel.onDigit("3") },
                                buttonType = ButtonType.NUMBER,
                                modifier = Modifier.weight(1f),
                                testTag = "btn_3"
                            )
                            CalculatorButton(
                                text = "+",
                                onClick = { viewModel.onOperator("+") },
                                buttonType = ButtonType.OPERATOR,
                                modifier = Modifier.weight(1f),
                                testTag = "btn_add"
                            )
                        }

                        // Row 5: +/-, 0, ., =
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CalculatorButton(
                                text = "+/−",
                                onClick = { viewModel.onNegate() },
                                buttonType = ButtonType.NUMBER,
                                modifier = Modifier.weight(1f),
                                testTag = "btn_negate"
                            )
                            CalculatorButton(
                                text = "0",
                                onClick = { viewModel.onDigit("0") },
                                buttonType = ButtonType.NUMBER,
                                modifier = Modifier.weight(1f),
                                testTag = "btn_0"
                            )
                            CalculatorButton(
                                text = ".",
                                onClick = { viewModel.onDecimal() },
                                buttonType = ButtonType.NUMBER,
                                modifier = Modifier.weight(1f),
                                testTag = "btn_dot"
                            )
                            CalculatorButton(
                                text = "=",
                                onClick = { viewModel.onEquals() },
                                buttonType = ButtonType.EQUALS,
                                modifier = Modifier.weight(1f),
                                testTag = "btn_equals"
                            )
                        }
                    }
                }
            }
        }
    }

    // History Bottom Sheet
    if (uiState.isHistoryVisible) {
        HistoryBottomSheet(
            historyList = uiState.historyList,
            onDismissRequest = { viewModel.toggleHistory(false) },
            onReuseEntry = { expr, res -> viewModel.reuseHistoryEntry(expr, res) },
            onClearHistory = { viewModel.clearHistory() }
        )
    }
}
