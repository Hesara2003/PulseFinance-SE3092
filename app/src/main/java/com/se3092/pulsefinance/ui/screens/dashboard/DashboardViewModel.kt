package com.se3092.pulsefinance.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.se3092.pulsefinance.domain.model.Account
import com.se3092.pulsefinance.domain.model.DAY_MILLIS
import com.se3092.pulsefinance.domain.model.DEFAULT_RUNWAY_MONTHS
import com.se3092.pulsefinance.domain.model.EmergencyRunway
import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.model.ExpenseCategory
import com.se3092.pulsefinance.domain.model.IncomeStream
import com.se3092.pulsefinance.domain.model.InvoiceStatus
import com.se3092.pulsefinance.domain.model.OnboardingProfile
import com.se3092.pulsefinance.domain.model.Periods
import com.se3092.pulsefinance.domain.model.SavingsGoal
import com.se3092.pulsefinance.domain.model.SeasonalReserve
import com.se3092.pulsefinance.domain.repository.AccountRepository
import com.se3092.pulsefinance.domain.repository.AuthRepository
import com.se3092.pulsefinance.domain.repository.ExpenseRepository
import com.se3092.pulsefinance.domain.repository.GoalRepository
import com.se3092.pulsefinance.domain.repository.IncomeRepository
import com.se3092.pulsefinance.domain.repository.OnboardingRepository
import com.se3092.pulsefinance.domain.repository.SeasonalReserveRepository
import com.se3092.pulsefinance.domain.usecase.BankSmsParser
import com.se3092.pulsefinance.domain.usecase.CalculateFinancialSummaryUseCase
import com.se3092.pulsefinance.domain.usecase.ExportDataToCsvUseCase
import com.se3092.pulsefinance.domain.usecase.SeasonalPeakChecker
import com.se3092.pulsefinance.ui.common.buildLedger
import com.se3092.pulsefinance.ui.common.displayNameFor
import com.se3092.pulsefinance.ui.common.formatDate
import com.se3092.pulsefinance.ui.common.greetingFor
import com.se3092.pulsefinance.ui.common.initialsFor
import com.se3092.pulsefinance.ui.common.lkr
import com.se3092.pulsefinance.ui.common.whenSignedIn
import com.se3092.pulsefinance.ui.screens.expense.AddExpenseFormState
import com.se3092.pulsefinance.ui.screens.expense.ExpenseEditSession
import com.se3092.pulsefinance.ui.screens.expense.ExpenseEditorState
import com.se3092.pulsefinance.ui.screens.expense.ExpensePreset
import com.se3092.pulsefinance.ui.screens.expense.deriveExpensePresets
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt
import com.se3092.pulsefinance.ui.common.presetIllustration
import com.se3092.pulsefinance.ui.components.NoticeTone
import com.se3092.pulsefinance.ui.components.UiNotice
import com.se3092.pulsefinance.ui.common.accountIllustration

private data class DashboardData(
    val expenses: List<Expense>,
    val incomes: List<IncomeStream>,
    val goal: SavingsGoal?,
    val accounts: List<Account>,
    val seasonalReserves: Map<String, SeasonalReserve>,
    /** What setup captured, including the emergency buffer (FR-AUTH-6). */
    val profile: OnboardingProfile? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val expenseRepository: ExpenseRepository,
    private val incomeRepository: IncomeRepository,
    private val goalRepository: GoalRepository,
    private val accountRepository: AccountRepository,
    private val seasonalReserveRepository: SeasonalReserveRepository,
    private val onboardingRepository: OnboardingRepository,
    private val calculateFinancialSummaryUseCase: CalculateFinancialSummaryUseCase,
    private val bankSmsParser: BankSmsParser,
    private val seasonalPeakChecker: SeasonalPeakChecker,
    private val exportDataToCsvUseCase: ExportDataToCsvUseCase
) : ViewModel() {

    private val form = MutableStateFlow(AddExpenseFormState())
    private val notice = MutableStateFlow<UiNotice?>(null)
    private val expenseEdit = ExpenseEditSession(viewModelScope, expenseRepository) { notice.value = it }
    val editingExpense: StateFlow<ExpenseEditorState?> = expenseEdit.state
    private val frozenWarning = MutableStateFlow<FrozenExpenseWarning?>(null)
    private val revealedAccountIds = MutableStateFlow<Set<String>>(emptySet())
    private var lastData: DashboardData? = null

    private val _accountEditor = MutableStateFlow<AccountEditorState?>(null)

    /** The add/edit account form while it is open (FR-ACC-8, FR-ACC-9). */
    val accountEditor: StateFlow<AccountEditorState?> = _accountEditor.asStateFlow()

    private val data = authRepository.whenSignedIn<DashboardData?>(signedOut = null) { userId ->
        combine(
            combine(
                expenseRepository.getExpenses(userId),
                incomeRepository.getIncomeStreams(userId),
                goalRepository.getPrimaryGoal(userId),
                accountRepository.getAccounts(userId),
                seasonalReserveRepository.getReserves(userId)
            ) { expenses, incomes, goal, accounts, reserves ->
                DashboardData(expenses, incomes, goal, accounts, reserves)
            },
            onboardingRepository.getProfile(userId)
        ) { base, profile ->
            base.copy(profile = profile).also { lastData = it }
        }
    }

    val uiState: StateFlow<DashboardUiState> = combine(
        data,
        form,
        notice,
        frozenWarning,
        revealedAccountIds
    ) { data, form, msg, warning, revealed ->
        buildState(data, form, msg, warning, revealed)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )

    private fun buildState(
        data: DashboardData?,
        form: AddExpenseFormState,
        notice: UiNotice?,
        warning: FrozenExpenseWarning?,
        revealed: Set<String>
    ): DashboardUiState {
        val now = System.currentTimeMillis()
        val email = authRepository.userEmail
        val displayName = authRepository.userDisplayName
        val header = DashboardUiState(
            isLoading = data == null,
            firstName = displayNameFor(displayName, email),
            initials = initialsFor(displayName, email),
            greeting = greetingFor(now),
            monthLabel = formatDate(now, "MMMM yyyy"),
            addExpense = form,
            notice = notice,
            frozenWarning = warning,
            revealedAccountIds = revealed
        )
        if (data == null) return header

        val unpaid = data.incomes
            .filter { !it.isRealized }
            .sortedWith(compareByDescending<IncomeStream> { it.effectiveStatus(now) == InvoiceStatus.OVERDUE }.thenBy { it.dateReceivedOrExpected })
        val (weekBars, weekTotal, weekChange) = weeklyDiscretionary(data.expenses, now)

        val seasonal = seasonalPeakChecker(data.expenses, now)
        val summary = calculateFinancialSummaryUseCase(data.incomes, data.expenses, data.goal, now, data.seasonalReserves.values.toList())

        return header.copy(
            summary = summary,
            runwayMonthlyCommittedLkr = EmergencyRunway.monthlyCommitted(
                data.profile?.fixedCommitments.orEmpty(),
                summary.committedExpensesLkr
            ),
            runwayMonths = data.profile?.runwayMonths,
            runwaySavedLkr = data.profile?.runwaySavedLkr ?: 0.0,
            primaryGoal = data.goal,
            recentTransactions = buildLedger(data.incomes, data.expenses).take(6),
            accounts = data.accounts,
            presets = deriveExpensePresets(data.expenses, now),
            weekBars = weekBars,
            weekDiscretionaryLkr = weekTotal,
            weekChangePct = weekChange,
            unpaidInvoices = unpaid,
            overdueInvoiceCount = unpaid.count { it.effectiveStatus(now) == InvoiceStatus.OVERDUE },
            addExpense = form.copy(accountId = form.accountId.ifBlank { data.accounts.firstOrNull()?.id.orEmpty() }),
            seasonalAlert = seasonal,
            seasonalReserve = seasonal?.let { data.seasonalReserves[it.peakKey] }
        )
    }

    /** Discretionary spend per day this week, and the change vs the same weekdays last week. */
    private fun weeklyDiscretionary(expenses: List<Expense>, now: Long): Triple<List<DayBar>, Double, Int?> {
        val discretionary = expenses.filter { !it.isCommitted }
        val week = Periods.week(now)
        val today = Periods.startOfDay(now)
        val dayTotals = (0 until 7).map { i ->
            val start = week.startMillis + i * DAY_MILLIS
            start to discretionary.filter { it.timestamp in start until start + DAY_MILLIS }.sumOf { it.amountLkr }
        }
        val peak = dayTotals.maxOf { it.second }.coerceAtLeast(1.0)
        val bars = dayTotals.mapIndexed { i, (start, total) ->
            DayBar(
                label = WEEKDAY_LETTERS[i],
                amountLkr = total,
                fillRatio = (total / peak).toFloat(),
                isToday = start == today
            )
        }

        val elapsed = (today + DAY_MILLIS) - week.startMillis
        val thisWeek = discretionary.filter { it.timestamp in week.startMillis until week.startMillis + elapsed }.sumOf { it.amountLkr }
        val lastWeekStart = week.startMillis - 7 * DAY_MILLIS
        val lastWeek = discretionary.filter { it.timestamp in lastWeekStart until lastWeekStart + elapsed }.sumOf { it.amountLkr }
        val change = if (lastWeek > 0) ((thisWeek - lastWeek) / lastWeek * 100).roundToInt() else null
        return Triple(bars, thisWeek, change)
    }

    // ---------------- Spending Freeze & Account Security ----------------

    fun freezeAccount(account: Account, days: Int = 14) {
        val untilMillis = System.currentTimeMillis() + days * DAY_MILLIS
        viewModelScope.launch {
            accountRepository.freezeAccount(account, untilMillis).fold(
                onSuccess = { notice.value = UiNotice("${account.name} frozen", "No spending from it until ${formatDate(untilMillis, "dd MMM")}", illustration = accountIllustration(account.type)) },
                onFailure = { notice.value = UiNotice("Couldn't freeze ${account.name}", it.localizedMessage, NoticeTone.ERROR) }
            )
        }
    }

    fun unfreezeAccount(account: Account) {
        viewModelScope.launch {
            accountRepository.unfreezeAccount(account).fold(
                onSuccess = { notice.value = UiNotice("Spending freeze lifted", account.name, illustration = accountIllustration(account.type)) },
                onFailure = { notice.value = UiNotice("Couldn't lift the freeze", it.localizedMessage, NoticeTone.ERROR) }
            )
        }
    }

    // ---------------- Account management (FR-ACC-8, FR-ACC-9) ----------------

    fun openAccountEditor(account: Account? = null) {
        _accountEditor.value = account?.let(AccountEditorState::editing) ?: AccountEditorState.forNewAccount()
    }

    fun onAccountEditorChange(transform: (AccountEditorState) -> AccountEditorState) {
        _accountEditor.update { editor -> editor?.let(transform) }
    }

    fun dismissAccountEditor() {
        _accountEditor.value = null
    }

    fun saveAccountEditor() {
        val editor = _accountEditor.value ?: return
        val userId = authRepository.currentUserId ?: return
        val account = editor.toAccount(userId) ?: run {
            _accountEditor.update { it?.copy(errorMessage = editor.validationError) }
            return
        }
        viewModelScope.launch {
            accountRepository.saveAccount(account).fold(
                onSuccess = {
                    _accountEditor.value = null
                    notice.value = UiNotice(
                        if (editor.isNew) "Account added" else "Account updated",
                        account.name,
                        illustration = accountIllustration(account.type)
                    )
                },
                onFailure = { e ->
                    _accountEditor.update { it?.copy(errorMessage = e.localizedMessage ?: "Couldn't save the account") }
                }
            )
        }
    }

    fun requestDeleteAccount() {
        // He always keeps at least one account, so an expense always has somewhere to go.
        if (uiState.value.accounts.size <= 1) {
            _accountEditor.update { it?.copy(errorMessage = "Keep at least one account so your expenses have somewhere to go") }
            return
        }
        _accountEditor.update { it?.copy(isConfirmingDelete = true, errorMessage = null) }
    }

    fun confirmDeleteAccount() {
        val original = _accountEditor.value?.original ?: return
        val userId = authRepository.currentUserId ?: return
        viewModelScope.launch {
            accountRepository.deleteAccount(userId, original.id).fold(
                onSuccess = {
                    _accountEditor.value = null
                    notice.value = UiNotice("Account deleted", "${original.name}. Its expenses are kept.")
                },
                onFailure = { e ->
                    _accountEditor.update { it?.copy(isConfirmingDelete = false, errorMessage = e.localizedMessage ?: "Couldn't delete the account") }
                }
            )
        }
    }

    // ---------------- Seasonal Peak set-aside (FR-LK-5) ----------------

    fun setAsideForSeasonalPeak() {
        val userId = authRepository.currentUserId ?: return
        val alert = uiState.value.seasonalAlert ?: return
        if (alert.suggestedSetAsideLkr <= 0) return
        seasonalReserveRepository.setAside(
            userId,
            SeasonalReserve(
                peakKey = alert.peakKey,
                amountLkr = alert.suggestedSetAsideLkr,
                setAsideAt = System.currentTimeMillis(),
                peakDateMillis = alert.peakDateMillis
            )
        )
        notice.value = UiNotice("${lkr(alert.suggestedSetAsideLkr)} set aside", "For ${alert.festivalName} · Safe-to-Spend updated")
    }

    fun undoSeasonalSetAside() {
        val userId = authRepository.currentUserId ?: return
        val reserve = uiState.value.seasonalReserve ?: return
        seasonalReserveRepository.release(userId, reserve.peakKey)
        notice.value = UiNotice("Set-aside released", "${lkr(reserve.amountLkr)} back in Safe-to-Spend")
    }

    // ---------------- Emergency buffer (FR-AUTH-6) ----------------

    fun setRunwayMonths(months: Int) {
        val userId = authRepository.currentUserId ?: return
        onboardingRepository.saveRunway(userId, months, uiState.value.runwaySavedLkr)
    }

    /** Adds to what he has recorded as set aside for the buffer; it is his own tally, not a transaction. */
    fun addToRunway(amountLkr: Double) {
        val userId = authRepository.currentUserId ?: return
        if (amountLkr <= 0) return
        val state = uiState.value
        val months = state.runwayMonths ?: DEFAULT_RUNWAY_MONTHS
        val saved = state.runwaySavedLkr + amountLkr
        onboardingRepository.saveRunway(userId, months, saved)
        notice.value = UiNotice("${lkr(amountLkr)} added to your safety buffer", "${lkr(saved)} set aside so far")
    }

    fun setAccountRevealed(accountId: String, revealed: Boolean) {
        revealedAccountIds.update { current ->
            if (revealed) current + accountId else current - accountId
        }
    }

    // ---------------- Add expense sheet & Spending Freeze friction ----------------

    fun openAddExpense(preset: ExpensePreset? = null) {
        form.update {
            AddExpenseFormState(
                isVisible = true,
                accountId = it.accountId,
                titleInput = preset?.title.orEmpty(),
                amountInput = preset?.amountLkr?.let { amount -> "%.0f".format(amount) }.orEmpty(),
                category = preset?.category ?: ExpenseCategory.FOOD_DINING,
                isCommitted = preset?.isCommitted ?: false
            )
        }
    }

    fun dismissAddExpense() = form.update { it.copy(isVisible = false, errorMessage = null) }
    fun onTitleChanged(value: String) = form.update { it.copy(titleInput = value) }
    fun onAmountChanged(value: String) = form.update { it.copy(amountInput = value, errorMessage = null) }
    fun onCommittedToggled(value: Boolean) = form.update { it.copy(isCommitted = value) }
    fun onAccountSelected(accountId: String) = form.update { it.copy(accountId = accountId) }
    fun onDaysAgoSelected(days: Int) = form.update { it.copy(daysAgo = days) }

    fun onCategoryChanged(category: ExpenseCategory) = form.update {
        it.copy(category = category, isCommitted = category.isTypicallyCommitted)
    }

    fun onPresetSelected(preset: ExpensePreset) = form.update {
        it.copy(
            titleInput = preset.title,
            // A starter shortcut has no amount, so keep whatever the user has already typed.
            amountInput = preset.amountLkr?.let { amount -> "%.0f".format(amount) } ?: it.amountInput,
            category = preset.category,
            isCommitted = preset.isCommitted,
            errorMessage = null
        )
    }

    fun handleSharedBankSms(smsText: String) {
        val parsed = bankSmsParser.parse(smsText) ?: return
        val currentAccounts = uiState.value.accounts
        val matchedAccount = currentAccounts.find { acct ->
            parsed.bankName.isNotBlank() && acct.name.contains(parsed.bankName, ignoreCase = true)
        }
        form.update {
            it.copy(
                isVisible = true,
                titleInput = parsed.merchant,
                amountInput = "%.0f".format(parsed.amountLkr),
                category = parsed.suggestedCategory,
                isCommitted = parsed.isCommitted,
                accountId = matchedAccount?.id ?: it.accountId,
                errorMessage = null
            )
        }
        notice.value = UiNotice("Draft expense from your bank alert", "${parsed.merchant} · check it and save")
    }

    fun submitExpense() {
        val state = uiState.value
        val current = state.addExpense
        val amount = current.amountInput.replace(",", "").trim().toDoubleOrNull()
        if (amount == null || amount <= 0) {
            form.update { it.copy(errorMessage = "Enter an amount greater than zero") }
            return
        }
        val userId = authRepository.currentUserId ?: run {
            form.update { it.copy(errorMessage = "You're signed out. Sign in again to save.") }
            return
        }
        val account = state.accounts.find { it.id == current.accountId }
        val title = current.titleInput.trim().ifBlank { current.category.displayName }

        // Check if account is frozen (FR-ACC-3, FR-ACC-4)
        if (account?.isFrozen == true) {
            val dailyPace = state.primaryGoal?.requiredDailySavingsLkr?.takeIf { it > 0 }
                ?: (state.primaryGoal?.targetAmountLkr?.div(90) ?: 1000.0)
            val delayDays = (amount / dailyPace).roundToInt().coerceAtLeast(1)
            frozenWarning.value = FrozenExpenseWarning(
                account = account,
                amountLkr = amount,
                title = title,
                goalDaysDelayed = delayDays
            )
            return
        }

        saveExpenseRecord(userId, account, title, amount, current.category, current.isCommitted, current.daysAgo)
    }

    fun confirmFrozenExpenseBreak() {
        val warning = frozenWarning.value ?: return
        val state = uiState.value
        val current = state.addExpense
        val userId = authRepository.currentUserId ?: return

        frozenWarning.value = null
        viewModelScope.launch {
            accountRepository.recordFreezeBreak(warning.account)
        }
        saveExpenseRecord(userId, warning.account, warning.title, warning.amountLkr, current.category, current.isCommitted, current.daysAgo)
    }

    fun dismissFrozenWarning() {
        frozenWarning.value = null
    }

    private fun saveExpenseRecord(
        userId: String,
        account: Account?,
        title: String,
        amount: Double,
        category: ExpenseCategory,
        isCommitted: Boolean,
        daysAgo: Int
    ) {
        viewModelScope.launch {
            form.update { it.copy(isSubmitting = true) }
            val result = expenseRepository.saveExpense(
                Expense(
                    userId = userId,
                    title = title,
                    amountLkr = amount,
                    category = category,
                    isCommitted = isCommitted,
                    paymentAccountId = account?.id.orEmpty(),
                    paymentAccountName = account?.name.orEmpty(),
                    timestamp = System.currentTimeMillis() - daysAgo * DAY_MILLIS
                )
            )
            result.fold(
                onSuccess = {
                    form.update { AddExpenseFormState(accountId = it.accountId) }
                    notice.value = UiNotice("Expense logged", "${lkr(amount)} · $title", illustration = presetIllustration(title, category))
                },
                onFailure = { e ->
                    form.update { it.copy(isSubmitting = false, errorMessage = e.localizedMessage ?: "Couldn't save this expense") }
                }
            )
        }
    }

    // ---------------- CSV Data Export ----------------

    /** Builds the CSV of every record; the screen hands it to the share sheet. */
    fun exportCsv(): String {
        val d = lastData
        val incomes = d?.incomes ?: uiState.value.unpaidInvoices
        val expenses = d?.expenses.orEmpty()
        val goals = listOfNotNull(d?.goal ?: uiState.value.primaryGoal)
        val csv = exportDataToCsvUseCase.generateCsv(
            incomes = incomes,
            expenses = expenses,
            goals = goals
        )
        notice.value = UiNotice("CSV export ready", "Choose where to send it")
        return csv
    }

    // ---------------- Other actions ----------------

    // ---------------- Edit an expense (FR-EXP-8) ----------------

    fun startEditExpense(expenseId: String) {
        val userId = authRepository.currentUserId ?: return
        expenseEdit.start(userId, expenseId)
    }

    fun onExpenseEditChange(transform: (ExpenseEditorState) -> ExpenseEditorState) = expenseEdit.change(transform)
    fun dismissExpenseEdit() = expenseEdit.dismiss()
    fun saveExpenseEdit() = expenseEdit.save(uiState.value.accounts)

    fun deleteExpense(expenseId: String) {
        val userId = authRepository.currentUserId ?: return
        viewModelScope.launch {
            expenseRepository.deleteExpense(expenseId, userId)
                .onSuccess { notice.value = UiNotice("Expense deleted") }
                .onFailure { notice.value = UiNotice("Couldn't delete the expense", it.localizedMessage, NoticeTone.ERROR) }
        }
    }

    fun dismissNotice() {
        notice.value = null
    }

    fun signOut() {
        viewModelScope.launch { authRepository.signOut() }
    }

    private companion object {
        val WEEKDAY_LETTERS = listOf("M", "T", "W", "T", "F", "S", "S")
    }
}
