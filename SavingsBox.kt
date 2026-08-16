package com.savingstracker.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A single, individually trackable savings box.
 * Many boxes can share the same denomination (e.g. multiple KSh 400 boxes) —
 * each has its own unique [id], so checking/unchecking one never affects another.
 */
@Entity(tableName = "savings_boxes")
data class SavingsBox(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val denomination: Int,
    val isSaved: Boolean = false,
    val completedAtEpochMillis: Long? = null
)

enum class BoxTier { SMALL, MEDIUM, LARGE }

fun tierFor(denomination: Int): BoxTier = when (denomination) {
    10, 20, 50 -> BoxTier.SMALL
    100, 200 -> BoxTier.MEDIUM
    else -> BoxTier.LARGE
}
