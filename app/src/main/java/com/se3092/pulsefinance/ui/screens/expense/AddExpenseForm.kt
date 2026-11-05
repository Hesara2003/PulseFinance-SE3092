package com.se3092.pulsefinance.ui.screens.expense

import com.se3092.pulsefinance.domain.model.DAY_MILLIS
import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.model.ExpenseCategory

/**
 * A one-tap template that pre-fills the expense form. [amountLkr] is the user's own last
 * amount for a repeated spend, and null for a starter shortcut, which fills in only the title,
 * icon and category so the user types what they actually paid.
 */
data class ExpensePreset(
    val title: String,
    val amountLkr: Double?,
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

/**
 * Starter shortcuts for common Sri Lankan spends (FR-EXP-3, FR-EXP-4), shown until the user's
 * own habits appear. No amounts: a price we guessed would be wrong for most people.
 */
private val starterPresets = listOf(
    ExpensePreset("Coffee", null, ExpenseCategory.FOOD_DINING, false),
    ExpensePreset("PickMe ride", null, ExpenseCategory.TRANSPORT, false),
    ExpensePreset("UberEats", null, ExpenseCategory.FOOD_DINING, false),
    ExpensePreset("Dialog Reload", null, ExpenseCategory.UTILITIES, false),
    ExpensePreset("Mobitel Data Package", null, ExpenseCategory.UTILITIES, false),
    ExpensePreset("Fiber Broadband", null, ExpenseCategory.UTILITIES, true),
    ExpensePreset("Rent", null, ExpenseCategory.RENT, true)
)

/**
 * The user's own most repeated expenses in the last 60 days (latest amount wins), topped up
 * with starter shortcuts that carry no amount. Logging a habitual spend becomes a single tap
 * plus confirm.
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
