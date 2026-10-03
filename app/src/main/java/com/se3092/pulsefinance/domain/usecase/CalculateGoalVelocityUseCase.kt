package com.se3092.pulsefinance.domain.usecase

import com.se3092.pulsefinance.domain.model.SavingsGoal
import javax.inject.Inject

data class GoalVelocityReport(
    val goal: SavingsGoal,
    val isPacingOnTrack: Boolean,
    val projectedShortfallLkr: Double,
    val dailyPacingTargetLkr: Double,
    val monthlyPacingTargetLkr: Double,
    val behavioralInsight: String
)

class CalculateGoalVelocityUseCase @Inject constructor() {

    operator fun invoke(goal: SavingsGoal, actualMonthlySavingsRateLkr: Double): GoalVelocityReport {
        val requiredMonthly = goal.requiredMonthlySavingsLkr
        val requiredDaily = goal.requiredDailySavingsLkr
        val isPacing = actualMonthlySavingsRateLkr >= requiredMonthly

        val shortfall = if (actualMonthlySavingsRateLkr < requiredMonthly) {
            val monthsLeft = (goal.daysRemaining / 30.0).coerceAtLeast(1.0)
            (requiredMonthly - actualMonthlySavingsRateLkr) * monthsLeft
        } else {
            0.0
        }

        val insight = when {
            actualMonthlySavingsRateLkr <= 0 -> 
                "Currently saving 0/month. Saving LKR ${String.format("%.0f", requiredMonthly)}/mo is required to get your MacBook Pro M4 on time."
            isPacing -> 
                "You are on track! Maintaining this pace will deliver your MacBook M4 before the deadline."
            else -> 
                "Pacing warning: At your current savings rate, you will be short by LKR ${String.format("%.0f", shortfall)}."
        }

        return GoalVelocityReport(
            goal = goal,
            isPacingOnTrack = isPacing,
            projectedShortfallLkr = shortfall,
            dailyPacingTargetLkr = requiredDaily,
            monthlyPacingTargetLkr = requiredMonthly,
            behavioralInsight = insight
        )
    }
}
