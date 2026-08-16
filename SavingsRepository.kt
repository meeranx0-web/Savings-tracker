package com.savingstracker.app.data

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

data class SavingsSummary(
    val totalSaved: Int,
    val remaining: Int,
    val percentComplete: Int,
    val completedCount: Int,
    val remainingCount: Int,
    val averageContribution: Double,
    val largestContribution: Int,
    val currentStreak: Int,
    val daysRemaining: Long,
    val requiredDailyAverage: Double
)

class SavingsRepository(
    private val dao: SavingsDao,
    private val zone: ZoneId = SettingsDataStore.ZONE
) {

    val boxesFlow = dao.observeAllBoxes()

    suspend fun ensureSeeded() {
        if (dao.count() == 0) {
            dao.insertAll(BoxGenerator.buildInitialBoxes())
        }
    }

    suspend fun resetChallenge() {
        dao.deleteAll()
        dao.insertAll(BoxGenerator.buildInitialBoxes())
    }

    /** Toggle a single box's saved state. Never allows exceeding the goal — this
     * is structurally guaranteed since the fixed box set sums to exactly 10,000. */
    suspend fun toggleBox(box: SavingsBox) {
        val updated = if (box.isSaved) {
            box.copy(isSaved = false, completedAtEpochMillis = null)
        } else {
            box.copy(isSaved = true, completedAtEpochMillis = Instant.now().toEpochMilli())
        }
        dao.update(updated)
    }

    suspend fun saveSpecificBox(boxId: Long, boxes: List<SavingsBox>) {
        val box = boxes.firstOrNull { it.id == boxId && !it.isSaved } ?: return
        dao.update(box.copy(isSaved = true, completedAtEpochMillis = Instant.now().toEpochMilli()))
    }

    fun buildSummary(boxes: List<SavingsBox>, deadline: LocalDate): SavingsSummary {
        val saved = boxes.filter { it.isSaved }
        val totalSaved = saved.sumOf { it.denomination }
        val remaining = BoxGenerator.TOTAL_GOAL - totalSaved
        val percent = if (BoxGenerator.TOTAL_GOAL == 0) 0
            else (totalSaved * 100) / BoxGenerator.TOTAL_GOAL
        val today = LocalDate.now(zone)
        val daysRemaining = ChronoUnit.DAYS.between(today, deadline).coerceAtLeast(0)
        val requiredDaily = if (daysRemaining > 0) remaining.toDouble() / daysRemaining
            else remaining.toDouble()

        return SavingsSummary(
            totalSaved = totalSaved,
            remaining = remaining,
            percentComplete = percent,
            completedCount = saved.size,
            remainingCount = boxes.size - saved.size,
            averageContribution = if (saved.isNotEmpty()) totalSaved.toDouble() / saved.size else 0.0,
            largestContribution = saved.maxOfOrNull { it.denomination } ?: 0,
            currentStreak = calculateStreak(saved, today),
            daysRemaining = daysRemaining,
            requiredDailyAverage = requiredDaily
        )
    }

    /** Groups saved boxes by the local calendar date they were completed. */
    fun groupByDate(boxes: List<SavingsBox>): Map<LocalDate, List<SavingsBox>> {
        return boxes.filter { it.isSaved && it.completedAtEpochMillis != null }
            .sortedByDescending { it.completedAtEpochMillis }
            .groupBy { dateOf(it.completedAtEpochMillis!!) }
    }

    fun todaysBoxes(boxes: List<SavingsBox>): List<SavingsBox> {
        val today = LocalDate.now(zone)
        return boxes.filter {
            it.isSaved && it.completedAtEpochMillis != null && dateOf(it.completedAtEpochMillis) == today
        }
    }

    /** Suggests one currently-unsaved box (largest-first to make fastest progress). */
    fun smartPick(boxes: List<SavingsBox>): SavingsBox? =
        boxes.filter { !it.isSaved }.maxByOrNull { it.denomination }

    private fun dateOf(epochMillis: Long): LocalDate =
        Instant.ofEpochMilli(epochMillis).atZone(zone).toLocalDate()

    /** Consecutive days ending today (or yesterday, if nothing saved today yet)
     * on which at least one box was saved. */
    private fun calculateStreak(saved: List<SavingsBox>, today: LocalDate): Int {
        val savedDates = saved.mapNotNull { it.completedAtEpochMillis }
            .map { dateOf(it) }
            .toSet()
        if (savedDates.isEmpty()) return 0

        var cursor = if (today in savedDates) today else today.minusDays(1)
        if (cursor !in savedDates) return 0

        var streak = 0
        while (cursor in savedDates) {
            streak++
            cursor = cursor.minusDays(1)
        }
        return streak
    }
}
