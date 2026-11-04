package com.se3092.pulsefinance.domain.model

data class IncomeStream(
    val id: String = "",
    val userId: String = "",
    val source: IncomeSource,
    val title: String,
    val clientOrPlatform: String,
    /** Negative for a realised crypto trading loss, so the net position stays honest. */
    val originalAmount: Double,
    val currency: Currency,
    val exchangeRateToLkr: Double = 1.0,
    val amountInLkr: Double = originalAmount * exchangeRateToLkr,
    val status: InvoiceStatus = InvoiceStatus.RECEIVED,
    /** Received date for realised income; due date for an invoice still pending. */
    val dateReceivedOrExpected: Long = System.currentTimeMillis(),
    val isRecurringMonthly: Boolean = false,
    val notes: String = ""
) {
    val isRealized: Boolean get() = status == InvoiceStatus.RECEIVED

    val isLoss: Boolean get() = amountInLkr < 0

    /** A pending invoice whose due date has passed is overdue even if nobody re-labelled it. */
    fun effectiveStatus(now: Long = System.currentTimeMillis()): InvoiceStatus = when {
        status == InvoiceStatus.PENDING_INVOICE && dateReceivedOrExpected < Periods.startOfDay(now) -> InvoiceStatus.OVERDUE
        else -> status
    }
}
