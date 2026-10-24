package com.se3092.pulsefinance.domain.usecase

import com.se3092.pulsefinance.domain.model.DAY_MILLIS
import com.se3092.pulsefinance.domain.model.SavingsGoal
import javax.inject.Inject
import kotlin.math.ceil

data class GoalVelocityReport(
    val goal: SavingsGoal,
    val actualMonthlySavingsLkr: Double,
    val isPacingOnTrack: Boolean,
    val projectedShortfallLkr: Double,
    val dailyPacingTargetLkr: Double,
    val monthlyPacingTargetLkr: Double,
    /** When the goal is reached at the current savings rate; null if the user is not saving. */
    val projectedArrivalMillis: Long?,
    val behavioralInsight: String
)

class CalculateGoalVelocityUseCase @Inject constructor() {

    operator fun invoke(
        goal: SavingsGoal,
        actualMonthlySavingsRateLkr: Double,
        now: Long = System.currentTimeMillis()
    ): GoalVelocityReport {
        val requiredMonthly = goal.requiredMonthlySavingsLkr(now)
        val requiredDaily = goal.requiredDailySavingsLkr(now)
        val isPacing = goal.isComplete || actualMonthlySavingsRateLkr >= requiredMonthly

        val monthsLeft = (goal.daysRemaining(now) / 30.0).coerceAtLeast(1.0)
        val shortfall = if (isPacing) 0.0 else (requiredMonthly - actualMonthlySavingsRateLkr.coerceAtLeast(0.0)) * monthsLeft

        val projectedArrival = when {
            goal.isComplete -> now
            actualMonthlySavingsRateLkr > 0 -> {
                val daysNeeded = ceil(goal.remainingAmountLkr / actualMonthlySavingsRateLkr * 30).toLong()
                now + daysNeeded * DAY_MILLIS
            }
            else -> null
        }

        val title = goal.title.ifBlank { "your goal" }
        val insight = when {
            goal.isComplete ->
                "Target reached — $title is fully funded."
            actualMonthlySavingsRateLkr <= 0 ->
                "You're currently spending everything you earn. Saving LKR ${"%,.0f".format(requiredMonthly)}/month gets $title on time."
            isPacing ->
                "On track: at LKR ${"%,.0f".format(actualMonthlySavingsRateLkr)}/month you reach $title before the deadline."
            else ->
                "Behind pace by LKR ${"%,.0f".format(requiredMonthly - actualMonthlySavingsRateLkr)}/month — LKR ${"%,.0f".format(shortfall)} short at the deadline."
        }

        return GoalVelocityReport(
            goal = goal,
            actualMonthlySavingsLkr = actualMonthlySavingsRateLkr,
            isPacingOnTrack = isPacing,
            projectedShortfallLkr = shortfall,
            dailyPacingTargetLkr = requiredDaily,
            monthlyPacingTargetLkr = requiredMonthly,
            projectedArrivalMillis = projectedArrival,
            behavioralInsight = insight
        )
    }
}
