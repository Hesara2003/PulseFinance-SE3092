package com.se3092.pulsefinance.ui.common

import com.se3092.pulsefinance.domain.model.Currency
import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.model.ExpenseCategory
import com.se3092.pulsefinance.domain.model.IncomeSource
import com.se3092.pulsefinance.domain.model.IncomeStream

/** One row of the combined income + expense ledger, already normalised to LKR. */
data class TransactionItem(
    val id: String,
    val title: String,
    val subtitle: String,
    /** Positive = money in, negative = money out. */
    val amountLkr: Double,
    /** Original-currency label for foreign income, e.g. "USD 45.00"; null for LKR. */
    val originalAmountLabel: String?,
    val timestamp: Long,
    val expenseCategory: ExpenseCategory? = null,
    val incomeSource: IncomeSource? = null
) {
    val isIncoming: Boolean get() = amountLkr >= 0
}

fun Expense.toTransactionItem() = TransactionItem(
    id = "exp_$id",
    title = title,
    subtitle = "${category.displayName} · ${if (isCommitted) "Committed" else "Discretionary"}",
    amountLkr = -amountLkr,
    originalAmountLabel = null,
    timestamp = timestamp,
    expenseCategory = category
)

fun IncomeStream.toTransactionItem() = TransactionItem(
    id = "inc_$id",
    title = title,
    subtitle = "${source.displayName} · $clientOrPlatform",
    amountLkr = amountInLkr,
    originalAmountLabel = if (currency != Currency.LKR) "${currency.name} ${"%,.2f".format(originalAmount)}" else null,
    timestamp = dateReceivedOrExpected,
    incomeSource = source
)

/** Realised income and all expenses, newest first. Unpaid invoices are not cash flow yet. */
fun buildLedger(incomes: List<IncomeStream>, expenses: List<Expense>): List<TransactionItem> =
    (incomes.filter { it.isRealized }.map { it.toTransactionItem() } + expenses.map { it.toTransactionItem() })
        .sortedByDescending { it.timestamp }
