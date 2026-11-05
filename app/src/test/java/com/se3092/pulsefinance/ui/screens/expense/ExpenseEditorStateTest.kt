package com.se3092.pulsefinance.ui.screens.expense

import com.se3092.pulsefinance.domain.model.Account
import com.se3092.pulsefinance.domain.model.AccountType
import com.se3092.pulsefinance.domain.model.DAY_MILLIS
import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.model.ExpenseCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

/** FR-EXP-8: correcting a logged expense instead of deleting and re-entering it. */
class ExpenseEditorStateTest {

    private val now = 1_770_000_000_000L
    private val lunch = Expense(
        id = "e1", userId = "uid", title = "Lunch", amountLkr = 1_450.0, category = ExpenseCategory.FOOD_DINING,
        paymentAccountId = "acc_primary", paymentAccountName = "Primary Bank Account",
        timestamp = now - 2 * DAY_MILLIS, notes = "with the team"
    )
    private val primary = Account(id = "acc_primary", name = "Primary Bank Account", type = AccountType.BANK_PRIMARY)
    private val frozen = Account(id = "acc_secondary", name = "Secondary Account", type = AccountType.BANK_SECONDARY, isFrozen = true)
    private val accounts = listOf(primary, frozen)

    @Test
    fun `the form opens showing what was logged`() {
        val state = ExpenseEditorState(lunch)

        assertEquals("1450", state.amountInput)
        assertEquals("Lunch", state.title)
        assertEquals("acc_primary", state.accountId)
        assertEquals(lunch.timestamp, state.timestamp)
    }

    @Test
    fun `saving a corrected amount keeps the id, the date, the account and the notes`() {
        val saved = ExpenseEditorState(lunch).copy(amountInput = "1,850").toExpense(accounts, now)!!

        assertEquals("e1", saved.id)
        assertEquals(1_850.0, saved.amountLkr, 0.0)
        assertEquals(lunch.timestamp, saved.timestamp)
        assertEquals("Primary Bank Account", saved.paymentAccountName)
        assertEquals("with the team", saved.notes)
    }

    @Test
    fun `a blank title falls back to the category`() {
        val saved = ExpenseEditorState(lunch).copy(title = "  ").toExpense(accounts, now)!!

        assertEquals(ExpenseCategory.FOOD_DINING.displayName, saved.title)
    }

    @Test
    fun `choosing a category re-suggests committed and can still be overridden`() {
        val rent = ExpenseEditorState(lunch).withCategory(ExpenseCategory.RENT)
        assertTrue(rent.isCommitted)

        assertEquals(false, rent.copy(isCommitted = false).isCommitted)
    }

    @Test
    fun `no amount, zero or text is explained instead of saved`() {
        for (bad in listOf("", "0", "-5", "lots")) {
            val state = ExpenseEditorState(lunch).copy(amountInput = bad)
            assertNotNull("'$bad'", state.validationError(accounts, now))
            assertNull(state.toExpense(accounts, now))
        }
    }

    @Test
    fun `an expense cannot be dated in the future`() {
        val state = ExpenseEditorState(lunch).copy(timestamp = now + 5 * DAY_MILLIS)

        assertNotNull(state.validationError(accounts, now))
    }

    @Test
    fun `it cannot be moved onto a frozen account but may stay on one it was already charged to`() {
        assertNotNull(ExpenseEditorState(lunch).copy(accountId = "acc_secondary").validationError(accounts, now))

        val alreadyOnFrozen = lunch.copy(paymentAccountId = "acc_secondary", paymentAccountName = "Secondary Account")
        assertNull(ExpenseEditorState(alreadyOnFrozen).validationError(accounts, now))
    }

    @Test
    fun `picking a day keeps the time of day the expense had`() {
        val utcMidnight = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            clear(); set(2026, Calendar.MARCH, 14)
        }.timeInMillis

        val picked = ExpenseEditorState(lunch).withPickedDate(utcMidnight).timestamp

        val before = Calendar.getInstance().apply { timeInMillis = lunch.timestamp }
        val after = Calendar.getInstance().apply { timeInMillis = picked }
        assertEquals(2026, after.get(Calendar.YEAR))
        assertEquals(Calendar.MARCH, after.get(Calendar.MONTH))
        assertEquals(14, after.get(Calendar.DAY_OF_MONTH))
        assertEquals(before.get(Calendar.HOUR_OF_DAY), after.get(Calendar.HOUR_OF_DAY))
        assertEquals(before.get(Calendar.MINUTE), after.get(Calendar.MINUTE))
    }
}
