package com.savingstracker.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.savingstracker.app.data.SavingsBox
import com.savingstracker.app.data.SavingsSummary
import com.savingstracker.app.ui.components.CircularGoalProgress
import com.savingstracker.app.ui.components.StatCard
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    boxes: List<SavingsBox>,
    summary: SavingsSummary,
    todaysBoxes: List<SavingsBox>,
    smartPick: SavingsBox?,
    onSmartPickSave: (SavingsBox) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "SAVINGS TRACKER",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = "KSh 10,000 CHALLENGE",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "KSh ${summary.totalSaved}",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "SAVED",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "KSh ${summary.remaining} REMAINING",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                CircularGoalProgress(percent = summary.percentComplete)
                Spacer(Modifier.height(14.dp))
                LinearProgressIndicator(
                    progress = { (summary.percentComplete.coerceIn(0, 100)) / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(50)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(label = "COMPLETED", value = "${summary.completedCount}", modifier = Modifier.weight(1f))
                StatCard(label = "SAVED", value = "KSh ${summary.totalSaved}", modifier = Modifier.weight(1f))
                StatCard(label = "REMAINING", value = "KSh ${summary.remaining}", modifier = Modifier.weight(1f))
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(label = "DAYS LEFT", value = "${summary.daysRemaining}", modifier = Modifier.weight(1f))
                StatCard(
                    label = "DAILY TARGET",
                    value = "KSh ${"%.0f".format(summary.requiredDailyAverage)}",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (summary.currentStreak > 0) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "\uD83D\uDD25 ${summary.currentStreak} DAY STREAK",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }

        smartPick?.let { box ->
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "\uD83C\uDFAF SMART PICK",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Save KSh ${box.denomination} today",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = 6.dp, bottom = 12.dp)
                        )
                        Button(
                            onClick = { onSmartPickSave(box) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("SAVE KSh ${box.denomination}")
                        }
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(text = "TODAY", style = MaterialTheme.typography.labelSmall)
                    if (todaysBoxes.isEmpty()) {
                        Text(
                            text = "KSh 0 saved today",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    } else {
                        Text(
                            text = "KSh ${todaysBoxes.sumOf { it.denomination }} SAVED",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = 6.dp, bottom = 8.dp)
                        )
                        todaysBoxes.forEach {
                            Text("\u2611 KSh ${it.denomination}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }

        if (summary.percentComplete >= 100) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("\uD83C\uDF89", style = MaterialTheme.typography.displayLarge)
                        Text(
                            "KSh 10,000 SAVED",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Text("GOAL COMPLETED", color = Color.White.copy(alpha = 0.85f))
                    }
                }
            }
        }
    }
}
