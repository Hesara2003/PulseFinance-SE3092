package com.se3092.pulsefinance.ui.screens.expense

import com.se3092.pulsefinance.domain.model.DAY_MILLIS
import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.model.ExpenseCategory

/** A one-tap template that pre-fills the expense form. */
data class ExpensePreset(
    val title: String,
    val amountLkr: Double,
    val category: ExpenseCategory,
    val isCommitted: Boolean
)

data class AddExpenseFormState(
    val isVisible: Boolean = false,
    val titleInput: String = "",
    val amountInput: String = "",
    val category: ExpenseCategory = ExpenseCategory.FOOD_DINING,
    val isCommitted: Boolean = false,
    val accountId: String = "",
    /** 0 = today. Lets a forgotten expense be back-dated instead of abandoned. */
    val daysAgo: Int = 0,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null
)

/** Starter templates drawn from the scenario's recurring spends; replaced by real habits as they appear (FR-EXP-4). */
private val starterPresets = listOf(
    ExpensePreset("Coffee", 850.0, ExpenseCategory.FOOD_DINING, false),
    ExpensePreset("PickMe ride", 1450.0, ExpenseCategory.TRANSPORT, false),
    ExpensePreset("UberEats", 2400.0, ExpenseCategory.FOOD_DINING, false),
    ExpensePreset("Dialog Reload", 1000.0, ExpenseCategory.UTILITIES, false),
    ExpensePreset("Mobitel Data Package", 950.0, ExpenseCategory.UTILITIES, false),
    ExpensePreset("Fiber Broadband", 4800.0, ExpenseCategory.UTILITIES, true),
    ExpensePreset("Rent", 34000.0, ExpenseCategory.RENT, true)
)

/**
 * The user's own most repeated expenses in the last 60 days (latest amount wins), topped up
 * with starter templates. Logging a habitual spend becomes a single tap plus confirm.
 */
fun deriveExpensePresets(expenses: List<Expense>, now: Long = System.currentTimeMillis(), limit: Int = 6): List<ExpensePreset> {
    val frequent = expenses
        .filter { it.timestamp >= now - 60 * DAY_MILLIS }
        .groupBy { it.title.trim().lowercase() }
        .filterValues { it.size >= 2 }
        .values
        .sortedByDescending { it.size }
        .map { group ->
            val latest = group.maxBy { it.timestamp }
            ExpensePreset(latest.title, latest.amountLkr, latest.category, latest.isCommitted)
        }
    val takenTitles = frequent.map { it.title.lowercase() }.toSet()
    return (frequent + starterPresets.filter { it.title.lowercase() !in takenTitles }).take(limit)
}
