package com.se3092.pulsefinance.domain.usecase

import com.se3092.pulsefinance.domain.model.DAY_MILLIS
import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.model.Periods
import java.util.Calendar
import javax.inject.Inject

data class SeasonalAlert(
    val title: String,
    val festivalName: String,
    val daysUntilPeak: Int,
    val suggestedSetAsideLkr: Double,
    val message: String
)

/**
 * Checks for Sri Lankan Seasonal Peaks (FR-LK-4, FR-LK-5):
 * - Sinhala & Tamil New Year (Avurudu - April)
 * - Vesak Festival (May)
 * - Poson Festival (June)
 * - December Year-End Festivities (December)
 *
 * Emits a warning 30 days prior to protect goal pace against impulsive holiday spikes.
 */
class SeasonalPeakChecker @Inject constructor() {

    operator fun invoke(
        expenses: List<Expense>,
        now: Long = System.currentTimeMillis()
    ): SeasonalAlert? {
        val cal = Calendar.getInstance().apply { timeInMillis = now }
        val currentYear = cal.get(Calendar.YEAR)

        // Peak targets in current calendar year
        val peaks = listOf(
            SeasonalPeak("Sinhala & Tamil New Year (Avurudu)", currentYear, Calendar.APRIL, 14, 35000.0),
            SeasonalPeak("Vesak Festival", currentYear, Calendar.MAY, 15, 20000.0),
            SeasonalPeak("Poson Festival", currentYear, Calendar.JUNE, 15, 15000.0),
            SeasonalPeak("December Holiday Season", currentYear, Calendar.DECEMBER, 25, 45000.0)
        )

        val todayStart = Periods.startOfDay(now)
        for (peak in peaks) {
            val peakCal = Calendar.getInstance().apply {
                set(peak.year, peak.month, peak.dayOfMonth, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val diffMillis = peakCal.timeInMillis - todayStart
            val diffDays = (diffMillis / DAY_MILLIS).toInt()

            if (diffDays in 1..30) {
                // Calculate historical excess in this month if 12+ months of history exist
                val historicalExcess = calculateHistoricalExcess(expenses, peak.month, now)
                val setAsideAmount = historicalExcess ?: peak.defaultBufferLkr

                return SeasonalAlert(
                    title = "Seasonal Peak Alert",
                    festivalName = peak.name,
                    daysUntilPeak = diffDays,
                    suggestedSetAsideLkr = setAsideAmount,
                    message = "In $diffDays days: ${peak.name}. Past spending tends to jump by 20%+. Set aside an expense buffer to keep your savings goal safe."
                )
            }
        }
        return null
    }

    private fun calculateHistoricalExcess(expenses: List<Expense>, targetMonth: Int, now: Long): Double? {
        val oneYearAgo = now - 365L * DAY_MILLIS
        val pastExpenses = expenses.filter { it.timestamp < oneYearAgo }
        if (pastExpenses.isEmpty()) return null

        val cal = Calendar.getInstance()
        val targetMonthExpenses = pastExpenses.filter {
            cal.timeInMillis = it.timestamp
            cal.get(Calendar.MONTH) == targetMonth
        }.sumOf { it.amountLkr }

        val monthlyAverage = pastExpenses.sumOf { it.amountLkr } / 12.0
        val excess = targetMonthExpenses - monthlyAverage
        return if (excess > 0) excess else null
    }

    private data class SeasonalPeak(
        val name: String,
        val year: Int,
        val month: Int,
        val dayOfMonth: Int,
        val defaultBufferLkr: Double
    )
}
