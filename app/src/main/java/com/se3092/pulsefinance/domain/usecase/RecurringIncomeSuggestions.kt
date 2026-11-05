package com.se3092.pulsefinance.domain.usecase

import com.se3092.pulsefinance.domain.model.Currency
import com.se3092.pulsefinance.domain.model.IncomeStream
import com.se3092.pulsefinance.domain.model.InvoiceStatus
import com.se3092.pulsefinance.domain.model.Periods

/**
 * FR-INC-11: income marked monthly (a salary, a retainer) that hasn't been recorded yet this month.
 *
 * Returns the latest entry of each such series so the app can offer to add this month's copy.
 * Nothing is created from here: the caller shows the suggestion and he taps to add it.
 *
 * Only rupee entries qualify. A foreign-currency entry needs the rate he actually received that
 * month (ADR-0001), which can't be assumed from last month's.
 */
fun recurringIncomeDue(incomes: List<IncomeStream>, now: Long): List<IncomeStream> {
    val thisMonth = Periods.month(now)
    return incomes
        .filter { it.isRecurringMonthly && it.currency == Currency.LKR && it.originalAmount > 0 }
        .groupBy { it.source to it.title.trim().lowercase() }
        .values
        .filter { series -> series.none { it.dateReceivedOrExpected in thisMonth } }
        .map { series -> series.maxBy { it.dateReceivedOrExpected } }
        // Only a series that actually paid before: a future-dated or still-pending entry isn't a habit yet.
        .filter { it.isRealized && it.dateReceivedOrExpected < thisMonth.startMillis }
        .sortedByDescending { it.amountInLkr }
}

/** This month's copy of a recurring entry: received today, same amount, a new record. */
fun IncomeStream.asThisMonth(now: Long): IncomeStream = copy(
    id = "",
    dateReceivedOrExpected = now,
    status = InvoiceStatus.RECEIVED
)
