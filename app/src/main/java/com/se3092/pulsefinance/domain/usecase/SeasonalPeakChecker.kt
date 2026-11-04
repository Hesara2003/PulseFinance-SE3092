package com.se3092.pulsefinance.domain.usecase

import com.se3092.pulsefinance.domain.model.DAY_MILLIS
import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.model.Periods
import java.text.DateFormatSymbols
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

data class SeasonalAlert(
    val title: String,
    val festivalName: String,
    val daysUntilPeak: Int,
    val suggestedSetAsideLkr: Double,
    val message: String,
    /** How far last year's spending in this month ran above his monthly average, when known (FR-LK-5). */
    val lastYearExcessLkr: Double? = null,
    /** True when the peak comes from his own history rather than the festival calendar. */
    val isPersonalPeak: Boolean = false
)

/**
 * Checks for Sri Lankan Seasonal Peaks (FR-LK-4, FR-LK-5):
 * - Sinhala & Tamil New Year (Avurudu - April)
 * - Vesak Festival (May)
 * - Poson Festival (June)
 * - December Year-End Festivities (December)
 * - after 12 months of history, any month where his own spending was 20%+ above his average
 *
 * Emits a warning 30 days prior to protect goal pace against impulsive holiday spikes.
 */
class SeasonalPeakChecker @Inject constructor() {

    operator fun invoke(
        expenses: List<Expense>,
        now: Long = System.currentTimeMillis()
    ): SeasonalAlert? {
        val history = SpendingHistory(expenses, now)
        val currentYear = Calendar.getInstance().apply { timeInMillis = now }.get(Calendar.YEAR)
        val todayStart = Periods.startOfDay(now)

        val festivalMonths = FESTIVALS.map { it.month }.toSet()
        val personalPeaks = history.personalPeakMonths()
            .filterNot { it in festivalMonths }
            .map { month -> SeasonalPeak(monthName(month), month, dayOfMonth = 1, defaultBufferLkr = 0.0, isPersonal = true) }

        // Next year too, so a January peak can warn in December.
        val (peak, daysUntil) = (FESTIVALS + personalPeaks)
            .flatMap { peak -> listOf(currentYear, currentYear + 1).map { year -> peak to daysUntil(peak, year, todayStart) } }
            .filter { (_, days) -> days in 1..30 }
            .minByOrNull { (_, days) -> days }
            ?: return null

        val excess = history.lastYearExcess(peak.month)
        val month = monthName(peak.month)
        return if (peak.isPersonal) {
            SeasonalAlert(
                title = "Personal Spending Peak",
                festivalName = "Your $month spending peak",
                daysUntilPeak = daysUntil,
                suggestedSetAsideLkr = excess ?: 0.0,
                message = "In $daysUntil days: $month, one of your highest-spending months. Last year it ran ${formatLkr(excess ?: 0.0)} above your monthly average. Set aside a buffer to keep your savings goal safe.",
                lastYearExcessLkr = excess,
                isPersonalPeak = true
            )
        } else {
            val lastYear = excess?.let { "Last year you spent ${formatLkr(it)} above your monthly average in $month." }
                ?: "Past spending tends to jump by 20%+."
            SeasonalAlert(
                title = "Seasonal Peak Alert",
                festivalName = peak.name,
                daysUntilPeak = daysUntil,
                suggestedSetAsideLkr = excess ?: peak.defaultBufferLkr,
                message = "In $daysUntil days: ${peak.name}. $lastYear Set aside an expense buffer to keep your savings goal safe.",
                lastYearExcessLkr = excess
            )
        }
    }

    private fun daysUntil(peak: SeasonalPeak, year: Int, todayStart: Long): Int {
        val peakStart = Calendar.getInstance().apply {
            set(year, peak.month, peak.dayOfMonth, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        return ((peakStart - todayStart) / DAY_MILLIS).toInt()
    }

    /**
     * Spending per calendar month over the 12 complete months before now, so each month
     * appears once. The average counts only months that have records: a month he didn't
     * log is missing data, not a cheap month (NFR-2).
     */
    private class SpendingHistory(expenses: List<Expense>, now: Long) {
        private val totalsByMonth: Map<Int, Double>
        private val average: Double?
        private val hasFullYear: Boolean

        init {
            val windowStart = Periods.month(now, -12).startMillis
            val currentMonthStart = Periods.month(now).startMillis
            val cal = Calendar.getInstance()
            totalsByMonth = expenses
                .filter { it.timestamp in windowStart until currentMonthStart }
                .groupBy { cal.timeInMillis = it.timestamp; cal.get(Calendar.MONTH) }
                .mapValues { (_, monthExpenses) -> monthExpenses.sumOf { it.amountLkr } }
            average = totalsByMonth.values.takeIf { it.isNotEmpty() }?.average()
            // A record from the first month of the window or earlier means 12 months of history.
            hasFullYear = expenses.any { it.timestamp < Periods.month(now, -11).startMillis }
        }

        fun lastYearExcess(month: Int): Double? {
            val total = totalsByMonth[month] ?: return null
            val avg = average ?: return null
            return (total - avg).takeIf { it > 0 }
        }

        fun personalPeakMonths(): Set<Int> {
            val avg = average ?: return emptySet()
            if (!hasFullYear) return emptySet()
            return totalsByMonth.filterValues { it >= avg * PERSONAL_PEAK_THRESHOLD }.keys
        }
    }

    private data class SeasonalPeak(
        val name: String,
        val month: Int,
        val dayOfMonth: Int,
        val defaultBufferLkr: Double,
        val isPersonal: Boolean = false
    )

    private companion object {
        const val PERSONAL_PEAK_THRESHOLD = 1.2

        val FESTIVALS = listOf(
            SeasonalPeak("Sinhala & Tamil New Year (Avurudu)", Calendar.APRIL, 14, 35000.0),
            SeasonalPeak("Vesak Festival", Calendar.MAY, 15, 20000.0),
            SeasonalPeak("Poson Festival", Calendar.JUNE, 15, 15000.0),
            SeasonalPeak("December Holiday Season", Calendar.DECEMBER, 25, 45000.0)
        )

        fun monthName(month: Int): String = DateFormatSymbols.getInstance(Locale.ENGLISH).months[month]

        fun formatLkr(amount: Double): String = "LKR ${"%,.0f".format(amount)}"
    }
}
