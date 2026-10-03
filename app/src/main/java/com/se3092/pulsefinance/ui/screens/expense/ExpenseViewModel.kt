package com.se3092.pulsefinance.ui.screens.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.se3092.pulsefinance.domain.model.Account
import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.model.ExpenseCategory
import com.se3092.pulsefinance.domain.repository.AccountRepository
import com.se3092.pulsefinance.domain.repository.AuthRepository
import com.se3092.pulsefinance.domain.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

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
    val isDismissed: Boolean = false,
    val iconType: String // "fitness", "vps", "ai"
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
    val zombieSubscriptions: List<ZombieSubscription> = listOf(
        ZombieSubscription(
            id = "sub_fitness",
            title = "Fitness First Gym",
            subtitle = "Auto-Debited on 5th",
            tag = "Zombie Leak",
            isFlagged = true,
            usageStatText = "Last check-in: ",
            usageHighlight = "74 days ago",
            drainRateDisplay = "LKR 8,500",
            drainRateSub = " / mo",
            actionLabel = "Cancel & Block",
            iconType = "fitness"
        ),
        ZombieSubscription(
            id = "sub_vps",
            title = "Cloud VPS (DigitalOcean)",
            subtitle = "Auto-Debited on 12th",
            tag = "Idle Instance",
            isFlagged = true,
            usageStatText = "Zero network traffic in ",
            usageHighlight = "60 days",
            drainRateDisplay = "LKR 3,200",
            drainRateSub = " / mo",
            actionLabel = "Destroy & Cancel",
            iconType = "vps"
        ),
        ZombieSubscription(
            id = "sub_ai",
            title = "Claude Pro / OpenAI",
            subtitle = "Primary Card Auto-Debit",
            tag = "High Utility",
            isFlagged = false,
            usageStatText = "Active use: ",
            usageHighlight = "142 queries/week",
            drainRateDisplay = "USD $20",
            drainRateSub = " (≈ LKR 6.1k)",
            actionLabel = "Keep Active",
            iconType = "ai"
        )
    ),
    val toastMessage: String? = null
) {
    val activeFlaggedCount: Int get() = zombieSubscriptions.count { it.isFlagged && !it.isDismissed }
    val totalDrainMonthlyFormatted: String get() = if (activeFlaggedCount > 0) "LKR 16,500 / month" else "LKR 0 / month"
    val totalAnnualDrainDisplay: String get() = if (activeFlaggedCount > 0) "LKR 198k" else "LKR 0"
    val potentialSavingsPct: String get() = if (activeFlaggedCount == 0) "0.0%" else "40.4%"
    val isAllFlaggedNuked: Boolean get() = activeFlaggedCount == 0
}

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val expenseRepository: ExpenseRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val userId: String
        get() = authRepository.currentUserId ?: "guest_kavindu"

    private val _screenState = MutableStateFlow(ExpenseScreenUiState())

    val uiState: StateFlow<ExpenseScreenUiState> = combine(
        expenseRepository.getExpenses(userId),
        accountRepository.getAccounts(userId),
        _screenState
    ) { expenses, accounts, state ->
        val filteredExpenses = if (state.selectedCategoryFilter == null) {
            expenses
        } else {
            expenses.filter { it.category == state.selectedCategoryFilter }
        }

        val defaultAcc = accounts.firstOrNull()?.id ?: ""
        val selectedAcc = if (state.selectedAccountId.isBlank()) defaultAcc else state.selectedAccountId

        state.copy(
            expenses = filteredExpenses,
            accounts = accounts,
            selectedAccountId = selectedAcc
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ExpenseScreenUiState()
    )

    fun dismissZombieSubscription(id: String) {
        val current = _screenState.value.zombieSubscriptions
        val item = current.find { it.id == id }
        val updated = current.map {
            if (it.id == id) it.copy(isDismissed = true) else it
        }
        _screenState.update {
            it.copy(
                zombieSubscriptions = updated,
                toastMessage = "Blocked ${item?.title ?: "subscription"} & stopped auto-debit."
            )
        }
    }

    fun nukeAllFlagged() {
        val updated = _screenState.value.zombieSubscriptions.map {
            if (it.isFlagged) it.copy(isDismissed = true) else it
        }
        _screenState.update {
            it.copy(
                zombieSubscriptions = updated,
                toastMessage = "All flagged zombie leaks blocked & auto-debits stopped!"
            )
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

    fun applyPreset(title: String, amount: Double, category: ExpenseCategory, isCommitted: Boolean) {
        _screenState.update {
            it.copy(
                titleInput = title,
                amountInput = amount.toInt().toString(),
                selectedCategory = category,
                isCommittedInput = isCommitted
            )
        }
    }

    fun submitExpense() {
        val state = uiState.value
        val amount = state.amountInput.toDoubleOrNull() ?: return
        val title = if (state.titleInput.isNotBlank()) state.titleInput else state.selectedCategory.displayName
        val account = state.accounts.find { it.id == state.selectedAccountId } ?: state.accounts.firstOrNull()

        viewModelScope.launch {
            _screenState.update { it.copy(isSubmitting = true) }
            val newExpense = Expense(
                userId = userId,
                title = title,
                amountLkr = amount,
                category = state.selectedCategory,
                isCommitted = state.isCommittedInput,
                paymentAccountId = account?.id ?: "acc_primary_bank",
                paymentAccountName = account?.name ?: "Commercial Bank"
            )
            expenseRepository.addExpense(newExpense)
            hideAddSheet()
            _screenState.update { it.copy(isSubmitting = false) }
        }
    }

    fun deleteExpense(expenseId: String) {
        viewModelScope.launch {
            expenseRepository.deleteExpense(expenseId, userId)
        }
    }
}
