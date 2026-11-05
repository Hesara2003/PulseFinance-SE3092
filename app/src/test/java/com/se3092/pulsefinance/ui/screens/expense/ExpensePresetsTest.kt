package com.se3092.pulsefinance.ui.screens.expense

import com.se3092.pulsefinance.domain.model.DAY_MILLIS
import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.model.ExpenseCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExpensePresetsTest {

    private val now = 1_800_000_000_000L

    private fun expense(title: String, amount: Double, daysAgo: Int) = Expense(
        title = title,
        amountLkr = amount,
        category = ExpenseCategory.FOOD_DINING,
        timestamp = now - daysAgo * DAY_MILLIS
    )

    @Test
    fun `a new user's starter shortcuts carry no amount`() {
        val presets = deriveExpensePresets(emptyList(), now)
        assertTrue(presets.isNotEmpty())
        presets.forEach { assertNull("${it.title} should have no amount", it.amountLkr) }
    }

    @Test
    fun `a repeated spend keeps the user's latest amount`() {
        val expenses = listOf(expense("Coffee", 700.0, daysAgo = 5), expense("Coffee", 950.0, daysAgo = 1))
        val coffee = deriveExpensePresets(expenses, now).first { it.title == "Coffee" }
        assertEquals(950.0, coffee.amountLkr!!, 0.0)
    }

    @Test
    fun `a user's own spend replaces the starter of the same name`() {
        val expenses = listOf(expense("Coffee", 700.0, daysAgo = 3), expense("coffee", 800.0, daysAgo = 2))
        val coffees = deriveExpensePresets(expenses, now).filter { it.title.equals("coffee", ignoreCase = true) }
        assertEquals(1, coffees.size)
        assertEquals(800.0, coffees.single().amountLkr!!, 0.0)
    }
}
