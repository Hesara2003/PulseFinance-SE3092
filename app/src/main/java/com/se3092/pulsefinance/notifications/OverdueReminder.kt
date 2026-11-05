package com.se3092.pulsefinance.notifications

import com.se3092.pulsefinance.domain.model.IncomeStream
import com.se3092.pulsefinance.domain.model.InvoiceStatus
import com.se3092.pulsefinance.domain.model.Periods
import com.se3092.pulsefinance.ui.common.lkr

/**
 * FR-NTF-1: what to tell Kavindu about invoices whose payment date has passed without payment.
 * [daysLateOldest] is for the one waiting longest, since that's the follow-up that matters most.
 */
data class OverdueReminder(
    val count: Int,
    val totalLkr: Double,
    val daysLateOldest: Int,
    val mostOwedTitle: String
) {
    val title: String get() = if (count == 1) "An invoice is overdue" else "$count invoices are overdue"

    val text: String
        get() = if (count == 1) {
            "$mostOwedTitle · ${lkr(totalLkr)} is ${days(daysLateOldest)} late. Time to follow up."
        } else {
            "${lkr(totalLkr)} is waiting on you to chase, the oldest ${days(daysLateOldest)} late."
        }

    private fun days(n: Int) = if (n == 1) "1 day" else "$n days"
}

/** The reminder for [incomes] as of [now], or null when nothing is overdue. */
fun overdueReminder(incomes: List<IncomeStream>, now: Long): OverdueReminder? {
    val overdue = incomes.filter { it.effectiveStatus(now) == InvoiceStatus.OVERDUE }
    if (overdue.isEmpty()) return null
    return OverdueReminder(
        count = overdue.size,
        totalLkr = overdue.sumOf { it.amountInLkr },
        daysLateOldest = overdue.maxOf { it.daysOverdue(now) },
        mostOwedTitle = overdue.maxBy { it.amountInLkr }.title
    )
}

/** At most one reminder a day, however often the worker runs. */
fun shouldNotifyToday(lastNotifiedDayStart: Long, now: Long): Boolean =
    lastNotifiedDayStart < Periods.startOfDay(now)
