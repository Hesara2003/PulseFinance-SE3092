package com.se3092.pulsefinance.domain.model

data class IncomeStream(
    val id: String = "",
    val userId: String = "",
    val source: IncomeSource,
    val title: String,
    val clientOrPlatform: String,
    val originalAmount: Double,
    val currency: Currency,
    val exchangeRateToLkr: Double = 1.0,
    val amountInLkr: Double = originalAmount * exchangeRateToLkr,
    val status: InvoiceStatus = InvoiceStatus.RECEIVED,
    val dateReceivedOrExpected: Long = System.currentTimeMillis(),
    val isRecurringMonthly: Boolean = false,
    val notes: String = ""
) {
    val isRealized: Boolean get() = status == InvoiceStatus.RECEIVED
}
