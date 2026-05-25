package com.texasdex.justthetip

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: CalculatorViewModel,
    onNavigateToSettings: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Just the Tip") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                value = TextFieldValue(
                    text = viewModel.billAmountInput,
                    selection = TextRange(viewModel.billAmountInput.length)
                ),
                onValueChange = { viewModel.updateBillAmount(it.text) },
                label = { Text("Bill Amount") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = viewModel.tipPercentageInput,
                    onValueChange = { viewModel.updateTipPercentage(it) },
                    label = { Text("Tip %") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )

                Slider(
                    value = viewModel.tipPercentage.toFloat().coerceIn(0f, 100f),
                    onValueChange = { viewModel.setTipPercentage(it) },
                    valueRange = 0f..100f,
                    modifier = Modifier.weight(2f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(10f, 15f, 20f, 25f).forEach { percentage ->
                    val isSelected = kotlin.math.abs(viewModel.tipPercentage - percentage) < 0.001
                    Button(
                        onClick = { viewModel.setTipPercentage(percentage) },
                        colors = if (isSelected) {
                            ButtonDefaults.buttonColors()
                        } else {
                            ButtonDefaults.filledTonalButtonColors()
                        },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Text("${percentage.toInt()}%", fontSize = 12.sp)
                    }
                }
            }

            HorizontalDivider()

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val currencyFormat = NumberFormat.getCurrencyInstance()
                Text("Tip: ${currencyFormat.format(viewModel.tipAmount)}", style = MaterialTheme.typography.headlineSmall)
                Text("Total: ${currencyFormat.format(viewModel.totalAmount)}", style = MaterialTheme.typography.headlineMedium)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.roundTip() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Round Tip", fontSize = 12.sp)
                }
                Button(
                    onClick = { viewModel.roundTotal() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Round Total", fontSize = 12.sp)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.adjustTip(-1.0) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("-$1")
                }
                Button(
                    onClick = { viewModel.adjustTip(1.0) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("+$1")
                }
                Button(
                    onClick = { viewModel.reset() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reset")
                }
            }
        }
    }
}
