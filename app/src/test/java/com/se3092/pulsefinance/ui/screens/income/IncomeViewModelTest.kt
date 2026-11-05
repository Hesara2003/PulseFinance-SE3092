package com.se3092.pulsefinance.ui.screens.income

import com.se3092.pulsefinance.data.remote.rates.SuggestedRateInfo
import com.se3092.pulsefinance.domain.model.ApitTaxBand
import com.se3092.pulsefinance.domain.model.Currency
import com.se3092.pulsefinance.domain.model.DAY_MILLIS
import com.se3092.pulsefinance.domain.model.IncomeSource
import com.se3092.pulsefinance.domain.model.IncomeStream
import com.se3092.pulsefinance.domain.model.InvoiceStatus
import com.se3092.pulsefinance.domain.repository.TaxConfigRepository
import com.se3092.pulsefinance.domain.usecase.CalculatePayslipUseCase
import com.se3092.pulsefinance.domain.usecase.ConvertCurrencyUseCase
import com.se3092.pulsefinance.testing.FakeAuthRepository
import com.se3092.pulsefinance.testing.FakeIncomeRepository
import com.se3092.pulsefinance.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

/** The income screen's behaviour with in-memory repositories: add, edit, suggest, mark received, delete. */
@OptIn(ExperimentalCoroutinesApi::class)
class IncomeViewModelTest {

    @get:Rule val main = MainDispatcherRule()

    private val convert = mock<ConvertCurrencyUseCase> {
        on { toLkr(any(), any(), any()) } doAnswer { (it.getArgument<Double>(0)) * (it.getArgument<Double?>(2) ?: 1.0) }
        on { getSuggestedRateInfo(any()) } doReturn SuggestedRateInfo(305.0, 0L, true, "test rate")
    }
    private val taxConfig = object : TaxConfigRepository {
        override fun observeApitBands(): Flow<List<ApitTaxBand>> = flowOf(ApitTaxBand.DEFAULTS)
    }

    private fun viewModel(repo: FakeIncomeRepository) =
        IncomeViewModel(FakeAuthRepository(), repo, convert, CalculatePayslipUseCase(), taxConfig)

    private val now get() = System.currentTimeMillis()

    private fun salary(daysAgo: Long) = IncomeStream(
        id = "sal-$daysAgo", userId = "u", source = IncomeSource.SALARY, title = "Monthly Salary", clientOrPlatform = "Employer",
        originalAmount = 185_000.0, currency = Currency.LKR, amountInLkr = 185_000.0, status = InvoiceStatus.RECEIVED,
        dateReceivedOrExpected = now - daysAgo * DAY_MILLIS, isRecurringMonthly = true
    )

    @Test
    fun `a salary from before this month is suggested, and nothing is saved until he taps`() = runTest {
        val repo = FakeIncomeRepository(listOf(salary(daysAgo = 40)))
        val vm = viewModel(repo)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }

        assertEquals(listOf("Monthly Salary"), vm.uiState.value.recurringDue.map { it.title })
        assertTrue(repo.saved.isEmpty())
    }

    @Test
    fun `tapping add records a received copy dated today and clears the suggestion`() = runTest {
        val repo = FakeIncomeRepository(listOf(salary(daysAgo = 40)))
        val vm = viewModel(repo)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }

        vm.addRecurring(vm.uiState.value.recurringDue.single())

        val added = repo.saved.single()
        assertEquals(185_000.0, added.amountInLkr, 0.0)
        assertEquals(InvoiceStatus.RECEIVED, added.status)
        assertTrue(now - added.dateReceivedOrExpected < DAY_MILLIS)
        assertTrue(vm.uiState.value.recurringDue.isEmpty())
        assertEquals(2, repo.items.value.size)
    }

    @Test
    fun `waving a suggestion off hides it without adding anything`() = runTest {
        val repo = FakeIncomeRepository(listOf(salary(daysAgo = 40)))
        val vm = viewModel(repo)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }

        vm.dismissRecurring(vm.uiState.value.recurringDue.single())

        assertTrue(vm.uiState.value.recurringDue.isEmpty())
        assertTrue(repo.saved.isEmpty())
    }

    @Test
    fun `recording new income saves it with the rate he typed`() = runTest {
        val repo = FakeIncomeRepository()
        val vm = viewModel(repo)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }

        vm.showAddSheet()
        vm.onSourceChanged(IncomeSource.ADSENSE)
        vm.onAmountChanged("412.5")
        vm.onRateChanged("301.25")
        vm.submitIncome()

        val saved = repo.saved.single()
        assertEquals(Currency.USD, saved.currency)
        assertEquals(301.25, saved.exchangeRateToLkr, 0.0)
        assertEquals(412.5 * 301.25, saved.amountInLkr, 0.001)
    }

    @Test
    fun `a zero amount is explained and nothing is saved`() = runTest {
        val repo = FakeIncomeRepository()
        val vm = viewModel(repo)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }

        vm.showAddSheet()
        vm.onAmountChanged("0")
        vm.submitIncome()

        assertNotNull(vm.uiState.value.form.errorMessage)
        assertTrue(repo.saved.isEmpty())
    }

    @Test
    fun `editing replaces the record in place and keeps its rate, date and notes`() = runTest {
        val original = IncomeStream(
            id = "ads-1", userId = "u", source = IncomeSource.ADSENSE, title = "AdSense Sept", clientOrPlatform = "Google AdSense",
            originalAmount = 400.0, currency = Currency.USD, exchangeRateToLkr = 301.25, amountInLkr = 120_500.0,
            status = InvoiceStatus.RECEIVED, dateReceivedOrExpected = now - 20 * DAY_MILLIS, notes = "paid to Payoneer"
        )
        val repo = FakeIncomeRepository(listOf(original))
        val vm = viewModel(repo)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }

        vm.showEditSheet(original)
        vm.onAmountChanged("500")
        vm.submitIncome()

        val saved = repo.items.value.single()
        assertEquals("ads-1", saved.id)
        assertEquals(500.0, saved.originalAmount, 0.0)
        assertEquals(301.25, saved.exchangeRateToLkr, 0.0) // not today's 305
        assertEquals(500.0 * 301.25, saved.amountInLkr, 0.001)
        assertEquals(original.dateReceivedOrExpected, saved.dateReceivedOrExpected)
        assertEquals("paid to Payoneer", saved.notes)
    }

    @Test
    fun `tapping the source an entry already has does not swap in today's rate`() = runTest {
        val usd = IncomeStream(
            id = "ads-1", userId = "u", source = IncomeSource.ADSENSE, title = "AdSense", clientOrPlatform = "Google",
            originalAmount = 400.0, currency = Currency.USD, exchangeRateToLkr = 301.25, amountInLkr = 120_500.0
        )
        val vm = viewModel(FakeIncomeRepository(listOf(usd)))
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }

        vm.showEditSheet(usd)
        vm.onSourceChanged(IncomeSource.ADSENSE)
        vm.onCurrencyChanged(Currency.USD)

        assertEquals("301.25", vm.uiState.value.form.exchangeRateInput)
    }

    @Test
    fun `marking an invoice received and deleting an entry go through the repository`() = runTest {
        val invoice = salary(1).copy(id = "inv", status = InvoiceStatus.PENDING_INVOICE, isRecurringMonthly = false)
        val repo = FakeIncomeRepository(listOf(invoice))
        val vm = viewModel(repo)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }

        vm.markReceived(invoice)
        assertEquals(InvoiceStatus.RECEIVED, repo.items.value.single().status)

        vm.deleteIncome(invoice)
        assertTrue(repo.items.value.isEmpty())
    }
}
