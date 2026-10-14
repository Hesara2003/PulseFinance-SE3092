package com.se3092.pulsefinance.ui.screens.expense

import com.se3092.pulsefinance.domain.model.ExpenseCategory
import com.se3092.pulsefinance.ui.util.Formatters

/** One-tap expense templates for Kavindu's most frequent Colombo spends. */
data class ExpensePreset(
    val id: String,
    val emoji: String,
    val label: String,
    val title: String,
    val amountLkr: Double,
    val category: ExpenseCategory,
    val isCommitted: Boolean = category.isTypicallyCommitted
) {
    val chipLabel: String get() = "$emoji $label ${Formatters.lkr(amountLkr)}"
    val badgeAmount: String get() = Formatters.compact(amountLkr)
}

object ExpensePresets {
    val defaults: List<ExpensePreset> = listOf(
        ExpensePreset("cafe", "☕", "Coffee", "Coffee", 850.0, ExpenseCategory.FOOD_DINING),
        ExpensePreset("pickme", "🚗", "PickMe ride", "PickMe ride", 1450.0, ExpenseCategory.TRANSPORT),
        ExpensePreset("ubereats", "🍔", "UberEats", "UberEats", 2400.0, ExpenseCategory.FOOD_DINING),
        ExpensePreset("rent", "🏠", "Rent", "Rent", 34000.0, ExpenseCategory.RENT)
    )
}
