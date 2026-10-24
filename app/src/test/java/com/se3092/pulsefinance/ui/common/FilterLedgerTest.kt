package com.se3092.pulsefinance.ui.common

import com.se3092.pulsefinance.domain.model.Currency
import com.se3092.pulsefinance.domain.model.DAY_MILLIS
import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.model.ExpenseCategory
import com.se3092.pulsefinance.domain.model.IncomeSource
import com.se3092.pulsefinance.domain.model.IncomeStream
import com.se3092.pulsefinance.domain.model.Period
import org.junit.Assert.assertEquals
import org.junit.Test

class FilterLedgerTest {

    private val now = 1700000000000L

    private val ledger = buildLedger(
        incomes = listOf(
            IncomeStream(
                id = "i1",
                source = IncomeSource.FREELANCE,
                title = "Upwork milestone",
                clientOrPlatform = "Upwork",
                originalAmount = 300.0,
                currency = Currency.USD,
                exchangeRateToLkr = 300.0,
                dateReceivedOrExpected = now - 2 * DAY_MILLIS
            )
        ),
        expenses = listOf(
            Expense(
                id = "e1",
                title = "Coffee",
                amountLkr = 850.0,
                category = ExpenseCategory.FOOD_DINING,
                paymentAccountId = "cash",
                timestamp = now - 1 * DAY_MILLIS
            ),
            // The title mentions "Rent", but it's a transport expense on the bank account.
            Expense(
                id = "e2",
                title = "PickMe to Rent office",
                amountLkr = 640.0,
                category = ExpenseCategory.TRANSPORT,
                paymentAccountId = "bank",
                timestamp = now - 5 * DAY_MILLIS
            ),
            Expense(
                id = "e3",
                title = "Room rent",
                amountLkr = 34000.0,
                category = ExpenseCategory.RENT,
                paymentAccountId = "bank",
                timestamp = now - 20 * DAY_MILLIS
            )
        )
    )

    private fun ids(items: List<TransactionItem>) = items.map { it.id }.toSet()

    @Test
    fun `no filters keeps every transaction`() {
        assertEquals(4, filterLedger(ledger).size)
    }

    @Test
    fun `category filter uses the expense category, not text in the title`() {
        val result = filterLedger(ledger, category = ExpenseCategory.RENT)
        assertEquals(setOf("exp_e3"), ids(result))
    }

    @Test
    fun `account filter keeps only expenses paid from that account`() {
        val result = filterLedger(ledger, accountId = "bank")
        assertEquals(setOf("exp_e2", "exp_e3"), ids(result))
    }

    @Test
    fun `date range is half-open and includes income`() {
        val range = Period(now - 3 * DAY_MILLIS, now - 1 * DAY_MILLIS)
        val result = filterLedger(ledger, dateRange = range)
        assertEquals(setOf("inc_i1"), ids(result))
    }

    @Test
    fun `filters combine with keyword search`() {
        val result = filterLedger(ledger, query = "pickme", accountId = "bank", dateRange = Period(now - 10 * DAY_MILLIS, now))
        assertEquals(setOf("exp_e2"), ids(result))
    }
}
