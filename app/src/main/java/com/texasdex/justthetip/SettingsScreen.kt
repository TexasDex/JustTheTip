package com.texasdex.justthetip

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: CalculatorViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val buildDate = remember { Date(BuildConfig.BUILD_TIME).toString() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings & About") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Options", style = MaterialTheme.typography.titleLarge)
            
            Text("Default Tip Percentage")
            var defaultPercentageText by remember { mutableStateOf(viewModel.defaultPercentage.toString()) }
            TextField(
                value = defaultPercentageText,
                onValueChange = { 
                    defaultPercentageText = it
                    it.toFloatOrNull()?.let { value ->
                        viewModel.updateDefaultPercentage(value)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("ATM-style Entry")
                    Text(
                        "Last two digits are always cents unless you type a period.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Switch(
                    checked = viewModel.isAtmModeEnabled,
                    onCheckedChange = { viewModel.updateAtmMode(it) }
                )
            }

            Text("Rounding Option")
            Column(Modifier.selectableGroup()) {
                RoundingOption.entries.forEach { option ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .selectable(
                                selected = (option == viewModel.roundingOption),
                                onClick = { viewModel.updateRoundingOption(option) },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (option == viewModel.roundingOption),
                            onClick = null // null recommended for accessibility with selectable modifier
                        )
                        Text(
                            text = when(option) {
                                RoundingOption.UP -> "Always round up"
                                RoundingOption.DOWN -> "Always round down"
                                RoundingOption.NEAREST -> "Round up or down"
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }

            HorizontalDivider()

            Text("About", style = MaterialTheme.typography.titleLarge)
            Text(
                "Apparently you can't download a tips app without watching full-screen video ads and sharing your data with every marketer on the entire Internet, so I just threw this together with Gemini.",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = "Author Website: https://texasdex.com",
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                    val intent = Intent(Intent.ACTION_VIEW, "https://texasdex.com".toUri())
                    context.startActivity(intent)
                }
            )

            Text("Build Date: $buildDate", style = MaterialTheme.typography.bodySmall)
            
            Text(
                "No ads. No privacy violations.  No in-app purchases. Just math.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
