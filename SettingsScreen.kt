package com.savingstracker.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.savingstracker.app.data.ThemeMode
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun SettingsScreen(
    themeMode: ThemeMode,
    deadline: LocalDate,
    showResetDialog: Boolean,
    onThemeChange: (ThemeMode) -> Unit,
    onRequestReset: () -> Unit,
    onConfirmReset: () -> Unit,
    onCancelReset: () -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Text("SETTINGS", style = MaterialTheme.typography.headlineMedium) }

        item {
            SettingsCard(title = "Theme") {
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    val options = listOf(ThemeMode.LIGHT to "Light", ThemeMode.DARK to "Dark", ThemeMode.SYSTEM to "System")
                    options.forEachIndexed { index, (mode, label) ->
                        SegmentedButton(
                            selected = themeMode == mode,
                            onClick = { onThemeChange(mode) },
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size)
                        ) {
                            Text(label)
                        }
                    }
                }
            }
        }

        item {
            SettingsCard(title = "Challenge Deadline") {
                Text(deadline.format(formatter), style = MaterialTheme.typography.bodyLarge)
            }
        }

        item {
            SettingsCard(title = "Currency") {
                Text("KSh (Kenyan Shilling)", style = MaterialTheme.typography.bodyLarge)
            }
        }

        item {
            SettingsCard(title = "Reset Challenge") {
                Column {
                    Text(
                        "This clears all saved progress and starts the KSh 10,000 challenge over.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    OutlinedButton(
                        onClick = onRequestReset,
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Reset Challenge")
                    }
                }
            }
        }

        item {
            SettingsCard(title = "About") {
                Text(
                    "Savings Tracker helps you save exactly KSh 10,000 by checking off individual denomination boxes as you save in real life. All data stays on your phone — fully offline, no account required.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = onCancelReset,
            title = { Text("Reset Challenge?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to reset your savings challenge? All saved progress will be deleted.") },
            confirmButton = {
                TextButton(onClick = onConfirmReset) {
                    Text("Reset", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = onCancelReset) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun SettingsCard(title: String, content: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                title.uppercase(Locale.ENGLISH),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Column(modifier = Modifier.padding(top = 8.dp)) { content() }
        }
    }
}
