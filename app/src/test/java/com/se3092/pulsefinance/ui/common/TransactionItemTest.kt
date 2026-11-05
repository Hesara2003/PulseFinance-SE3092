package com.se3092.pulsefinance.ui.common

import com.se3092.pulsefinance.domain.model.Currency
import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.model.ExpenseCategory
import com.se3092.pulsefinance.domain.model.IncomeSource
import com.se3092.pulsefinance.domain.model.IncomeStream
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TransactionItemTest {

    @Test
    fun `an expense row knows its expense id, so it can be deleted`() {
        val row = Expense(id = "abc123", title = "Coffee", amountLkr = 850.0, category = ExpenseCategory.FOOD_DINING)
            .toTransactionItem()
        assertEquals("abc123", row.expenseId)
    }

    @Test
    fun `an income row has no expense id`() {
        val row = IncomeStream(
            id = "abc123",
            source = IncomeSource.SALARY,
            title = "Salary",
            clientOrPlatform = "Employer",
            originalAmount = 250_000.0,
            currency = Currency.LKR
        ).toTransactionItem()
        assertNull(row.expenseId)
    }
}
