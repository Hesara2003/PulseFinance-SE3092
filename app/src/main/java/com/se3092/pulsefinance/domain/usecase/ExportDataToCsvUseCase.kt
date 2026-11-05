package com.se3092.pulsefinance.domain.usecase

import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.model.IncomeStream
import com.se3092.pulsefinance.domain.model.SavingsGoal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * Exports all user financial records (Incomes, Expenses, Goals) to CSV (FR-DATA-1, UC15).
 * Ensures Kavindu's financial records outlive any individual tool or platform.
 * Pure Kotlin: it only builds the CSV text; sharing it is the UI's job.
 */
class ExportDataToCsvUseCase @Inject constructor() {

    fun generateCsv(
        incomes: List<IncomeStream>,
        expenses: List<Expense>,
        goals: List<SavingsGoal>
    ): String {
        val sb = StringBuilder()

        // 1. Incomes Section
        sb.appendLine("=== INCOMES & INVOICES ===")
        sb.appendLine("Date,Source Title,Source,Amount LKR,Status,Currency,Original Amount,Client or Platform,Exchange Rate")
        for (inc in incomes) {
            val dateStr = formatDate(inc.dateReceivedOrExpected, "yyyy-MM-dd")
            val status = if (inc.isRealized) "REALIZED" else inc.status.name
            val rate = inc.exchangeRateToLkr.toString()
            sb.appendLine(
                listOf(
                    dateStr,
                    escape(inc.title),
                    inc.source.name,
                    inc.amountInLkr.toString(),
                    status,
                    inc.currency.name,
                    inc.originalAmount.toString(),
                    escape(inc.clientOrPlatform),
                    rate
                ).joinToString(",")
            )
        }
        sb.appendLine()

        // 2. Expenses Section
        sb.appendLine("=== EXPENSES & TRANSACTIONS ===")
        sb.appendLine("Date,Merchant/Title,Category,Amount LKR,Committed,Payment Account")
        for (exp in expenses) {
            val dateStr = formatDate(exp.timestamp, "yyyy-MM-dd")
            sb.appendLine(
                listOf(
                    dateStr,
                    escape(exp.title),
                    exp.category.name,
                    exp.amountLkr.toString(),
                    if (exp.isCommitted) "COMMITTED" else "DISCRETIONARY",
                    escape(exp.paymentAccountName)
                ).joinToString(",")
            )
        }
        sb.appendLine()

        // 3. Goals Section
        sb.appendLine("=== SAVINGS GOALS ===")
        sb.appendLine("Goal Title,Target LKR,Current Saved LKR,Progress %,Target Date,Status,USD Reference")
        for (g in goals) {
            val dateStr = formatDate(g.targetDateMillis, "yyyy-MM-dd")
            val usdRef = g.usdReferencePrice?.toString() ?: "N/A"
            sb.appendLine(
                listOf(
                    escape(g.title),
                    g.targetAmountLkr.toString(),
                    g.currentSavedLkr.toString(),
                    "%.1f%%".format(Locale.US, g.progressPercentage * 100),
                    dateStr,
                    if (g.isComplete) "COMPLETED" else "ACTIVE",
                    usdRef
                ).joinToString(",")
            )
        }

        return sb.toString()
    }

    /** ISO dates, so spreadsheets read them as dates in any locale (NFR-11). */
    private fun formatDate(millis: Long, pattern: String): String =
        SimpleDateFormat(pattern, Locale.US).format(Date(millis))

    private fun escape(value: String): String {
        return if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            "\"" + value.replace("\"", "\"\"") + "\""
        } else {
            value
        }
    }
}
