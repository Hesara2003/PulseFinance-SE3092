package com.se3092.pulsefinance.ui.common

import java.util.Locale
import com.se3092.pulsefinance.domain.model.Currency
import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.model.ExpenseCategory
import com.se3092.pulsefinance.domain.model.IncomeSource
import com.se3092.pulsefinance.domain.model.IncomeStream
import com.se3092.pulsefinance.domain.model.Period

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
    val incomeSource: IncomeSource? = null,
    /** Account the expense was paid from; null for income, which isn't tied to an account. */
    val accountId: String? = null
) {
    val isIncoming: Boolean get() = amountLkr >= 0

    /** The expense's own id for an expense row, so it can be deleted; null for income. */
    val expenseId: String? get() = id.takeIf { it.startsWith(EXPENSE_PREFIX) }?.removePrefix(EXPENSE_PREFIX)
}

private const val EXPENSE_PREFIX = "exp_"

fun Expense.toTransactionItem() = TransactionItem(
    id = "$EXPENSE_PREFIX$id",
    title = title,
    subtitle = "${category.displayName} · ${if (isCommitted) "Committed" else "Discretionary"}",
    amountLkr = -amountLkr,
    originalAmountLabel = null,
    timestamp = timestamp,
    expenseCategory = category,
    accountId = paymentAccountId.ifBlank { null }
)

fun IncomeStream.toTransactionItem() = TransactionItem(
    id = "inc_$id",
    title = title,
    subtitle = "${source.displayName} · $clientOrPlatform",
    amountLkr = amountInLkr,
    originalAmountLabel = if (currency != Currency.LKR) "${currency.name} ${"%,.2f".format(Locale.US, originalAmount)}" else null,
    timestamp = dateReceivedOrExpected,
    incomeSource = source
)

/** Realised income and all expenses, newest first. Unpaid invoices are not cash flow yet. */
fun buildLedger(incomes: List<IncomeStream>, expenses: List<Expense>): List<TransactionItem> =
    (incomes.filter { it.isRealized }.map { it.toTransactionItem() } + expenses.map { it.toTransactionItem() })
        .sortedByDescending { it.timestamp }

/**
 * FR-ANA-5: keyword search plus category, account and date filters. A null filter matches everything.
 * Category and account filters only ever match expenses, since income has neither.
 */
fun filterLedger(
    items: List<TransactionItem>,
    query: String = "",
    category: ExpenseCategory? = null,
    accountId: String? = null,
    dateRange: Period? = null
): List<TransactionItem> = items.filter { item ->
    (query.isBlank() || item.title.contains(query, ignoreCase = true) || item.subtitle.contains(query, ignoreCase = true)) &&
        (category == null || item.expenseCategory == category) &&
        (accountId == null || item.accountId == accountId) &&
        (dateRange == null || item.timestamp in dateRange)
}
