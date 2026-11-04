package com.se3092.pulsefinance.domain.model

import kotlin.math.ceil

/** One checkpoint on the savings journey; reaching [thresholdLkr] clears it. */
data class QuestLevel(
    val number: Int,
    val name: String,
    val thresholdLkr: Double
)

/**
 * A savings goal expressed as a journey of equal-sized levels.
 *
 * Every level is a real rupee amount (target ÷ 10), so the game never drifts away from the
 * money: clearing a level means LKR 49,000 more is actually saved. There are no streaks, lives
 * or penalties, so a week without logging never sets Kavindu back on the map.
 */
data class QuestProgress(
    val levels: List<QuestLevel>,
    /** Number of levels fully cleared (0..levels.size). */
    val clearedLevels: Int,
    /** The level currently being worked on, or null once the goal is funded. */
    val currentLevel: QuestLevel?,
    val savedLkr: Double,
    val toClearCurrentLkr: Double,
    /** 0..1 progress through the current level. */
    val levelProgress: Float,
    /** 0..1 progress through the whole journey. */
    val overallProgress: Float,
    /** Days to clear the current level at the goal's required daily pace. */
    val daysToClearAtPace: Int?
) {
    val isComplete: Boolean get() = currentLevel == null
    val levelCount: Int get() = levels.size
}

object GoalQuest {

    const val LEVEL_COUNT = 10

    fun of(goal: SavingsGoal): QuestProgress {
        val target = goal.targetAmountLkr.coerceAtLeast(0.0)
        val step = if (target > 0) target / LEVEL_COUNT else 0.0
        val levels = (1..LEVEL_COUNT).map { n ->
            QuestLevel(
                number = n,
                name = if (n == LEVEL_COUNT) goal.title.ifBlank { "Goal" } else "Level $n",
                thresholdLkr = if (n == LEVEL_COUNT) target else step * n
            )
        }
        val saved = goal.currentSavedLkr.coerceAtLeast(0.0)
        val cleared = if (step > 0) levels.count { saved >= it.thresholdLkr } else 0
        val current = levels.getOrNull(cleared)
        val levelStart = if (cleared == 0) 0.0 else levels[cleared - 1].thresholdLkr
        val toClear = current?.let { (it.thresholdLkr - saved).coerceAtLeast(0.0) } ?: 0.0
        val levelProgress = current?.let {
            val span = it.thresholdLkr - levelStart
            if (span > 0) ((saved - levelStart) / span).toFloat().coerceIn(0f, 1f) else 0f
        } ?: 1f
        val pace = goal.requiredDailySavingsLkr
        return QuestProgress(
            levels = levels,
            clearedLevels = cleared,
            currentLevel = current,
            savedLkr = saved,
            toClearCurrentLkr = toClear,
            levelProgress = levelProgress,
            overallProgress = goal.progressPercentage,
            daysToClearAtPace = if (current != null && pace > 0) ceil(toClear / pace).toInt() else null
        )
    }
}
