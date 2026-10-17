package com.se3092.pulsefinance.ui.screens.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.se3092.pulsefinance.domain.model.Account
import com.se3092.pulsefinance.domain.model.DetectedSubscription
import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.model.ExpenseCategory
import com.se3092.pulsefinance.domain.model.SavingsGoal
import com.se3092.pulsefinance.domain.model.SubscriptionReview
import com.se3092.pulsefinance.domain.model.SubscriptionReviewStatus
import com.se3092.pulsefinance.domain.model.SubscriptionRisk
import com.se3092.pulsefinance.domain.repository.AccountRepository
import com.se3092.pulsefinance.domain.repository.AuthRepository
import com.se3092.pulsefinance.domain.repository.ExpenseRepository
import com.se3092.pulsefinance.domain.repository.GoalRepository
import com.se3092.pulsefinance.domain.repository.SubscriptionReviewRepository
import com.se3092.pulsefinance.domain.usecase.DetectSubscriptionsUseCase
import com.se3092.pulsefinance.ui.util.Formatters
import com.se3092.pulsefinance.ui.util.userScopedFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Display model for one card on the Zombie Subscription Audit screen. */
data class ZombieSubscription(
    val id: String,
    val title: String,
    val subtitle: String,
    val tag: String,
    val isFlagged: Boolean, // true = leak, false = high utility
    val usageStatText: String,
    val usageHighlight: String,
    val drainRateDisplay: String,
    val drainRateSub: String,
    val actionLabel: String,
    val monthlyCostLkr: Double,
    val isDismissed: Boolean = false,
    val iconType: String // "fitness" (zombie), "vps" (watch), "ai" (healthy)
)

data class ExpenseScreenUiState(
    val expenses: List<Expense> = emptyList(),
    val accounts: List<Account> = emptyList(),
    val selectedCategoryFilter: ExpenseCategory? = null,
    val isAddSheetVisible: Boolean = false,
    val titleInput: String = "",
    val amountInput: String = "",
    val selectedCategory: ExpenseCategory = ExpenseCategory.FOOD_DINING,
    val isCommittedInput: Boolean = false,
    val selectedAccountId: String = "",
    val isSubmitting: Boolean = false,
    val zombieSubscriptions: List<ZombieSubscription> = emptyList(),
    val goalRemainingLkr: Double = 0.0,
    val userInitials: String = "",
    val toastMessage: String? = null
) {
    private val activeFlagged: List<ZombieSubscription>
        get() = zombieSubscriptions.filter { it.isFlagged && !it.isDismissed }

    val activeFlaggedCount: Int get() = activeFlagged.size
    val flaggedMonthlyDrainLkr: Double get() = activeFlagged.sumOf { it.monthlyCostLkr }
    val totalDrainMonthlyFormatted: String get() = "${Formatters.lkr(flaggedMonthlyDrainLkr)} / month"
    val totalAnnualDrainDisplay: String get() = Formatters.lkrCompact(flaggedMonthlyDrainLkr * 12)

    /** How much of the remaining goal one year of these leaks would fund. */
    val potentialSavingsPct: String get() {
        if (goalRemainingLkr <= 0 || activeFlaggedCount == 0) return "0.0%"
        val ratio = (flaggedMonthlyDrainLkr * 12 / goalRemainingLkr * 100).coerceAtMost(100.0)
        return String.format(java.util.Locale.US, "%.1f%%", ratio)
    }

    val isAllFlaggedNuked: Boolean get() = activeFlaggedCount == 0
    val hasDetections: Boolean get() = zombieSubscriptions.isNotEmpty()
}

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val expenseRepository: ExpenseRepository,
    private val accountRepository: AccountRepository,
    private val goalRepository: GoalRepository,
    private val subscriptionReviewRepository: SubscriptionReviewRepository,
    private val detectSubscriptions: DetectSubscriptionsUseCase
) : ViewModel() {

    private val userId: String?
        get() = authRepository.currentUserId

    private val _screenState = MutableStateFlow(ExpenseScreenUiState())

    private val expensesFlow = authRepository.userScopedFlow(emptyList()) { expenseRepository.getExpenses(it) }
    private val accountsFlow = authRepository.userScopedFlow(emptyList()) { accountRepository.getAccounts(it) }
    private val goalFlow = authRepository.userScopedFlow<SavingsGoal?>(null) { goalRepository.getPrimaryGoal(it) }
    private val reviewsFlow = authRepository.userScopedFlow(emptyMap()) {
        subscriptionReviewRepository.observeReviews(it)
    }

    val uiState: StateFlow<ExpenseScreenUiState> = combine(
        expensesFlow,
        accountsFlow,
        goalFlow,
        reviewsFlow,
        _screenState
    ) { expenses, accounts, goal, reviews, state ->
        val filteredExpenses = if (state.selectedCategoryFilter == null) {
            expenses
        } else {
            expenses.filter { it.category == state.selectedCategoryFilter }
        }

        val detected = detectSubscriptions(expenses, accounts, reviews)
            // Cancelled / "not a subscription" items drop off the audit list
            .filter { it.reviewStatus == null || it.reviewStatus == SubscriptionReviewStatus.KEEP }
            .sortedWith(compareBy<DetectedSubscription> { it.risk.ordinal }.thenByDescending { it.monthlyCostLkr })

        val selectedAcc = state.selectedAccountId.takeIf { id -> accounts.any { it.id == id } }
            ?: accounts.firstOrNull()?.id.orEmpty()

        state.copy(
            expenses = filteredExpenses,
            accounts = accounts,
            selectedAccountId = selectedAcc,
            zombieSubscriptions = detected.map { it.toCard() },
            goalRemainingLkr = goal?.remainingAmountLkr ?: 0.0,
            userInitials = Formatters.initials(authRepository.currentUser)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ExpenseScreenUiState()
    )

    private fun DetectedSubscription.toCard(): ZombieSubscription {
        val now = System.currentTimeMillis()
        val daysSinceCharge = Formatters.daysBetween(lastChargedMillis, now)
        val cadenceLabel = when {
            cadenceDays <= 8 -> " / wk"
            cadenceDays >= 355 -> " / yr"
            else -> " / mo"
        }
        return when (risk) {
            SubscriptionRisk.ZOMBIE -> ZombieSubscription(
                id = key,
                title = title,
                subtitle = "Auto-debited on the ${Formatters.dayOfMonthOrdinal(lastChargedMillis)} · $paymentAccountName",
                tag = risk.displayName,
                isFlagged = true,
                usageStatText = "Charged $occurrences× · next debit ",
                usageHighlight = Formatters.relativeDay(nextExpectedMillis, now),
                drainRateDisplay = Formatters.lkr(averageAmountLkr),
                drainRateSub = cadenceLabel,
                actionLabel = "Cancel & Block",
                monthlyCostLkr = monthlyCostLkr,
                iconType = "fitness"
            )
            SubscriptionRisk.WATCH -> ZombieSubscription(
                id = key,
                title = title,
                subtitle = "Recurring on $paymentAccountName",
                tag = risk.displayName,
                isFlagged = true,
                usageStatText = "Last charged ",
                usageHighlight = if (daysSinceCharge <= 0) "today" else "$daysSinceCharge days ago",
                drainRateDisplay = Formatters.lkr(averageAmountLkr),
                drainRateSub = cadenceLabel,
                actionLabel = "Cancel",
                monthlyCostLkr = monthlyCostLkr,
                iconType = "vps"
            )
            SubscriptionRisk.HEALTHY -> ZombieSubscription(
                id = key,
                title = title,
                subtitle = "Primary card · $paymentAccountName",
                tag = if (reviewStatus == SubscriptionReviewStatus.KEEP) "Kept Active" else risk.displayName,
                isFlagged = false,
                usageStatText = "Annual cost: ",
                usageHighlight = Formatters.lkrCompact(annualCostLkr),
                drainRateDisplay = Formatters.lkr(averageAmountLkr),
                drainRateSub = cadenceLabel,
                actionLabel = "Keep Active",
                monthlyCostLkr = monthlyCostLkr,
                iconType = "ai"
            )
        }
    }

    fun dismissZombieSubscription(id: String) {
        val uid = userId ?: return
        val item = uiState.value.zombieSubscriptions.find { it.id == id }
        viewModelScope.launch {
            subscriptionReviewRepository.saveReview(
                SubscriptionReview(subscriptionKey = id, userId = uid, status = SubscriptionReviewStatus.CANCELLED)
            )
            _screenState.update {
                it.copy(toastMessage = "Blocked ${item?.title ?: "subscription"} & stopped auto-debit.")
            }
        }
    }

    fun keepSubscription(id: String) {
        val uid = userId ?: return
        viewModelScope.launch {
            subscriptionReviewRepository.saveReview(
                SubscriptionReview(subscriptionKey = id, userId = uid, status = SubscriptionReviewStatus.KEEP)
            )
            _screenState.update { it.copy(toastMessage = "Marked as high-utility — we'll stop flagging it.") }
        }
    }

    fun nukeAllFlagged() {
        val uid = userId ?: return
        val flagged = uiState.value.zombieSubscriptions.filter { it.isFlagged && !it.isDismissed }
        if (flagged.isEmpty()) return
        val saved = flagged.sumOf { it.monthlyCostLkr }
        viewModelScope.launch {
            flagged.forEach { sub ->
                subscriptionReviewRepository.saveReview(
                    SubscriptionReview(subscriptionKey = sub.id, userId = uid, status = SubscriptionReviewStatus.CANCELLED)
                )
            }
            _screenState.update {
                it.copy(toastMessage = "All flagged leaks blocked — saving ${Formatters.lkr(saved)}/mo")
            }
        }
    }

    fun dismissToast() {
        _screenState.update { it.copy(toastMessage = null) }
    }

    fun onCategoryFilterSelected(category: ExpenseCategory?) {
        _screenState.update { it.copy(selectedCategoryFilter = category) }
    }

    fun showAddSheet() {
        _screenState.update { it.copy(isAddSheetVisible = true) }
    }

    fun hideAddSheet() {
        _screenState.update {
            it.copy(
                isAddSheetVisible = false,
                titleInput = "",
                amountInput = "",
                selectedCategory = ExpenseCategory.FOOD_DINING,
                isCommittedInput = false
            )
        }
    }

    fun onTitleChanged(title: String) {
        _screenState.update { it.copy(titleInput = title) }
    }

    fun onAmountChanged(amount: String) {
        _screenState.update { it.copy(amountInput = amount) }
    }

    fun onCategoryChanged(category: ExpenseCategory) {
        _screenState.update {
            it.copy(
                selectedCategory = category,
                isCommittedInput = category.isTypicallyCommitted
            )
        }
    }

    fun onCommittedToggled(isCommitted: Boolean) {
        _screenState.update { it.copy(isCommittedInput = isCommitted) }
    }

    fun onAccountSelected(accountId: String) {
        _screenState.update { it.copy(selectedAccountId = accountId) }
    }

    fun applyPreset(preset: ExpensePreset) {
        _screenState.update {
            it.copy(
                titleInput = preset.title,
                amountInput = preset.amountLkr.toInt().toString(),
                selectedCategory = preset.category,
                isCommittedInput = preset.isCommitted
            )
        }
    }

    /** One-tap logging straight from a preset badge, paid from the default account. */
    fun logPreset(preset: ExpensePreset) {
        val uid = userId ?: return
        val account = uiState.value.accounts.firstOrNull()
        viewModelScope.launch {
            expenseRepository.addExpense(
                Expense(
                    userId = uid,
                    title = preset.title,
                    amountLkr = preset.amountLkr,
                    category = preset.category,
                    isCommitted = preset.isCommitted,
                    paymentAccountId = account?.id.orEmpty(),
                    paymentAccountName = account?.name ?: "Cash"
                )
            )
            _screenState.update { it.copy(toastMessage = "Logged ${preset.label} · ${Formatters.lkr(preset.amountLkr)}") }
        }
    }

    fun submitExpense() {
        val uid = userId ?: return
        val state = uiState.value
        val amount = state.amountInput.toDoubleOrNull()?.takeIf { it > 0 } ?: return
        val title = state.titleInput.ifBlank { state.selectedCategory.displayName }
        val account = state.accounts.find { it.id == state.selectedAccountId } ?: state.accounts.firstOrNull()

        viewModelScope.launch {
            _screenState.update { it.copy(isSubmitting = true) }
            expenseRepository.addExpense(
                Expense(
                    userId = uid,
                    title = title,
                    amountLkr = amount,
                    category = state.selectedCategory,
                    isCommitted = state.isCommittedInput,
                    paymentAccountId = account?.id.orEmpty(),
                    paymentAccountName = account?.name ?: "Cash"
                )
            )
            hideAddSheet()
            _screenState.update {
                it.copy(isSubmitting = false, toastMessage = "Logged $title · ${Formatters.lkr(amount)}")
            }
        }
    }

    fun deleteExpense(expenseId: String) {
        val uid = userId ?: return
        viewModelScope.launch {
            expenseRepository.deleteExpense(expenseId, uid)
        }
    }
}
