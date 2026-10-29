package com.se3092.pulsefinance.domain.model

/**
 * Money Kavindu set aside from a Seasonal Peak alert (FR-LK-5). It is an earmark, not a
 * transaction: it lowers Safe-to-Spend instead of appearing as spending.
 */
data class SeasonalReserve(
    /** Identifies the peak occurrence, e.g. "vesak_festival_2027"; one reserve per peak. */
    val peakKey: String,
    val amountLkr: Double,
    val setAsideAt: Long,
    val peakDateMillis: Long
) {
    /**
     * Counts against Safe-to-Spend only in the month it was set aside and until the peak day
     * arrives. From then on the festival spending itself is recorded, so keeping the reserve
     * would count the same money twice.
     */
    fun isActive(now: Long): Boolean =
        setAsideAt in Periods.month(now) && peakDateMillis >= Periods.startOfDay(now)
}
