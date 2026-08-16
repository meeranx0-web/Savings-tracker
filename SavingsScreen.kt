package com.savingstracker.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.savingstracker.app.data.BoxTier
import com.savingstracker.app.data.SavingsBox
import com.savingstracker.app.data.tierFor
import com.savingstracker.app.ui.components.SavingsBoxCard

@Composable
fun SavingsScreen(
    boxes: List<SavingsBox>,
    onToggle: (SavingsBox) -> Unit
) {
    val grouped = boxes.groupBy { tierFor(it.denomination) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        contentPadding = PaddingValues(20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        listOf(BoxTier.SMALL to "SMALL", BoxTier.MEDIUM to "MEDIUM", BoxTier.LARGE to "LARGE").forEach { (tier, label) ->
            val tierBoxes = grouped[tier].orEmpty()
            if (tierBoxes.isNotEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    val completed = tierBoxes.count { it.isSaved }
                    Text(
                        text = "$label  ($completed/${tierBoxes.size})",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 2.dp)
                    )
                }
                items(tierBoxes, key = { it.id }) { box ->
                    SavingsBoxCard(
                        denomination = box.denomination,
                        isSaved = box.isSaved,
                        onClick = { onToggle(box) }
                    )
                }
            }
        }
    }
}
