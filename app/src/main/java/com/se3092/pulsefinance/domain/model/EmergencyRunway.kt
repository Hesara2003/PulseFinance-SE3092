package com.se3092.pulsefinance.domain.model

import kotlin.math.max

const val MIN_RUNWAY_MONTHS = 1
const val MAX_RUNWAY_MONTHS = 6

/** Setup preselects this, a common starting point; he can pick anything from 1 to 6. */
const val DEFAULT_RUNWAY_MONTHS = 3

/**
 * The Emergency Runway Buffer (FR-AUTH-6): [months] of committed costs he wants kept aside in
 * case income stops, and how far along he is. [savedLkr] is his own record of what he has set
 * aside for it; the app has no bank connection to measure it (C-13).
 */
data class EmergencyRunway(
    val months: Int,
    /** What his fixed costs come to in a month, the unit the buffer is measured in. */
    val monthlyCommittedLkr: Double,
    val savedLkr: Double
) {
    val targetLkr: Double get() = months * monthlyCommittedLkr

    val remainingLkr: Double get() = (targetLkr - savedLkr).coerceAtLeast(0.0)

    val isFunded: Boolean get() = targetLkr > 0 && savedLkr >= targetLkr

    val progress: Float
        get() = if (targetLkr > 0) (savedLkr / targetLkr).toFloat().coerceIn(0f, 1f) else 0f

    /** How many months of committed costs [savedLkr] already covers. */
    val monthsCovered: Double
        get() = if (monthlyCommittedLkr > 0) savedLkr / monthlyCommittedLkr else 0.0

    companion object {
        fun clampMonths(months: Int): Int = months.coerceIn(MIN_RUNWAY_MONTHS, MAX_RUNWAY_MONTHS)

        /**
         * His fixed commitments from setup are the floor, since they are costs he pays no matter
         * what. If he has logged more committed spending this month (a bill he didn't list), the
         * buffer is sized to that instead.
         */
        fun monthlyCommitted(fixedCommitments: List<FixedCommitment>, committedThisMonthLkr: Double): Double =
            max(fixedCommitments.sumOf { it.amountLkr }, committedThisMonthLkr)

        /** Null until he has chosen a number of months, or while there are no committed costs to size it from. */
        fun of(profile: OnboardingProfile?, committedThisMonthLkr: Double): EmergencyRunway? {
            val months = profile?.runwayMonths ?: return null
            val monthly = monthlyCommitted(profile.fixedCommitments, committedThisMonthLkr)
            if (monthly <= 0) return null
            return EmergencyRunway(months, monthly, profile.runwaySavedLkr)
        }
    }
}
