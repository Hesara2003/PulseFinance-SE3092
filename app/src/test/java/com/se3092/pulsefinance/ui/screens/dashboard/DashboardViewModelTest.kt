package com.se3092.pulsefinance.ui.screens.dashboard

import com.se3092.pulsefinance.domain.model.Account
import com.se3092.pulsefinance.domain.model.AccountType
import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.model.ExpenseCategory
import com.se3092.pulsefinance.domain.usecase.BankSmsParser
import com.se3092.pulsefinance.domain.usecase.CalculateFinancialSummaryUseCase
import com.se3092.pulsefinance.domain.usecase.ExportDataToCsvUseCase
import com.se3092.pulsefinance.domain.usecase.SeasonalPeakChecker
import com.se3092.pulsefinance.testing.FakeAccountRepository
import com.se3092.pulsefinance.testing.FakeAuthRepository
import com.se3092.pulsefinance.testing.FakeExpenseRepository
import com.se3092.pulsefinance.testing.FakeGoalRepository
import com.se3092.pulsefinance.testing.FakeIncomeRepository
import com.se3092.pulsefinance.testing.FakeOnboardingRepository
import com.se3092.pulsefinance.testing.FakeSeasonalReserveRepository
import com.se3092.pulsefinance.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** The Today screen's expense and account flows, against in-memory repositories. */
@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    @get:Rule val main = MainDispatcherRule()

    private val primary = Account(id = "acc-primary", userId = "u", name = "Primary Bank Account", type = AccountType.BANK_PRIMARY)
    private val secondary = Account(id = "acc-secondary", userId = "u", name = "Secondary Account", type = AccountType.BANK_SECONDARY, isAutoDebitSource = true)

    private class Harness(
        val expenses: FakeExpenseRepository,
        val accounts: FakeAccountRepository,
        val vm: DashboardViewModel
    )

    private fun TestScope.harness(accounts: List<Account>, expenses: List<Expense> = emptyList()): Harness {
        val expenseRepo = FakeExpenseRepository(expenses)
        val accountRepo = FakeAccountRepository(accounts)
        val vm = DashboardViewModel(
            FakeAuthRepository(), expenseRepo, FakeIncomeRepository(), FakeGoalRepository(), accountRepo,
            FakeSeasonalReserveRepository(), FakeOnboardingRepository(),
            CalculateFinancialSummaryUseCase(), BankSmsParser(), SeasonalPeakChecker(), ExportDataToCsvUseCase()
        )
        backgroundScope.launch { vm.uiState.collect {} }
        return Harness(expenseRepo, accountRepo, vm)
    }

    @Test
    fun `logging an expense saves it against the chosen account`() = runTest {
        val h = harness(listOf(primary, secondary))

        h.vm.openAddExpense()
        h.vm.onAmountChanged("1,450")
        h.vm.onTitleChanged("Lunch")
        h.vm.onCategoryChanged(ExpenseCategory.FOOD_DINING)
        h.vm.onAccountSelected("acc-primary")
        h.vm.submitExpense()

        val saved = h.expenses.saved.single()
        assertEquals("Lunch", saved.title)
        assertEquals(1_450.0, saved.amountLkr, 0.0)
        assertEquals("Primary Bank Account", saved.paymentAccountName)
        assertTrue(!h.vm.uiState.value.addExpense.isVisible || h.vm.uiState.value.addExpense.amountInput.isEmpty())
    }

    @Test
    fun `a blank amount is explained and nothing is saved`() = runTest {
        val h = harness(listOf(primary))

        h.vm.openAddExpense()
        h.vm.submitExpense()

        assertNotNull(h.vm.uiState.value.addExpense.errorMessage)
        assertTrue(h.expenses.saved.isEmpty())
    }

    @Test
    fun `charging a frozen account warns first and saves only after the Freeze Break is confirmed`() = runTest {
        val frozen = secondary.copy(isFrozen = true, frozenUntilMillis = System.currentTimeMillis() + 86_400_000)
        val h = harness(listOf(primary, frozen))

        h.vm.openAddExpense()
        h.vm.onAmountChanged("5000")
        h.vm.onAccountSelected("acc-secondary")
        h.vm.submitExpense()

        assertNotNull(h.vm.uiState.value.frozenWarning)
        assertTrue(h.expenses.saved.isEmpty())

        h.vm.confirmFrozenExpenseBreak()

        assertEquals(1, h.expenses.saved.size)
        assertEquals(1, h.accounts.items.value.single { it.id == "acc-secondary" }.freezeBreakCount)
        assertNull(h.vm.uiState.value.frozenWarning)
    }

    @Test
    fun `cancelling the warning saves nothing and breaks no freeze`() = runTest {
        val frozen = secondary.copy(isFrozen = true, frozenUntilMillis = System.currentTimeMillis() + 86_400_000)
        val h = harness(listOf(primary, frozen))

        h.vm.openAddExpense()
        h.vm.onAmountChanged("5000")
        h.vm.onAccountSelected("acc-secondary")
        h.vm.submitExpense()
        h.vm.dismissFrozenWarning()

        assertTrue(h.expenses.saved.isEmpty())
        assertEquals(0, h.accounts.items.value.single { it.id == "acc-secondary" }.freezeBreakCount)
    }

    @Test
    fun `editing an expense corrects it in place`() = runTest {
        val lunch = Expense(id = "e1", userId = "u", title = "Lunch", amountLkr = 1_450.0, category = ExpenseCategory.FOOD_DINING, paymentAccountId = "acc-primary", paymentAccountName = "Primary Bank Account")
        val h = harness(listOf(primary), listOf(lunch))

        h.vm.startEditExpense("e1")
        h.vm.onExpenseEditChange { it.copy(amountInput = "1850") }
        h.vm.saveExpenseEdit()

        val stored = h.expenses.items.value.single()
        assertEquals("e1", stored.id)
        assertEquals(1_850.0, stored.amountLkr, 0.0)
        assertNull(h.vm.editingExpense.value)
    }

    @Test
    fun `deleting an expense removes it`() = runTest {
        val lunch = Expense(id = "e1", userId = "u", title = "Lunch", amountLkr = 1_450.0, category = ExpenseCategory.FOOD_DINING)
        val h = harness(listOf(primary), listOf(lunch))

        h.vm.deleteExpense("e1")

        assertTrue(h.expenses.items.value.isEmpty())
    }

    @Test
    fun `an account can be added with a balance`() = runTest {
        val h = harness(listOf(primary))

        h.vm.openAccountEditor()
        h.vm.onAccountEditorChange { it.withType(AccountType.CRYPTO_EXCHANGE).copy(balanceInput = "83000") }
        h.vm.saveAccountEditor()

        val added = h.accounts.items.value.single { it.type == AccountType.CRYPTO_EXCHANGE }
        assertEquals(83_000.0, added.balanceLkr, 0.0)
        assertNull(h.vm.accountEditor.value)
    }

    @Test
    fun `his last account cannot be deleted`() = runTest {
        val h = harness(listOf(primary))

        h.vm.openAccountEditor(primary)
        h.vm.requestDeleteAccount()

        assertNotNull(h.vm.accountEditor.value?.errorMessage)
        assertEquals(1, h.accounts.items.value.size)
    }

    @Test
    fun `an account can be deleted while another remains`() = runTest {
        val h = harness(listOf(primary, secondary))

        h.vm.openAccountEditor(secondary)
        h.vm.requestDeleteAccount()
        h.vm.confirmDeleteAccount()

        assertEquals(listOf("acc-primary"), h.accounts.items.value.map { it.id })
    }
}
