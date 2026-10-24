package com.se3092.pulsefinance.ui.screens.expense

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.se3092.pulsefinance.data.local.UsageStatsHelper
import com.se3092.pulsefinance.domain.model.DetectedSubscription
import com.se3092.pulsefinance.domain.model.SubscriptionDecision
import com.se3092.pulsefinance.domain.model.SubscriptionReview
import com.se3092.pulsefinance.domain.model.SubscriptionStatus
import com.se3092.pulsefinance.domain.repository.AccountRepository
import com.se3092.pulsefinance.domain.repository.AuthRepository
import com.se3092.pulsefinance.domain.repository.ExpenseRepository
import com.se3092.pulsefinance.domain.repository.GoalRepository
import com.se3092.pulsefinance.domain.repository.SubscriptionReviewRepository
import com.se3092.pulsefinance.domain.usecase.DetectSubscriptionsUseCase
import com.se3092.pulsefinance.ui.common.initialsFor
import com.se3092.pulsefinance.ui.common.lkr
import com.se3092.pulsefinance.ui.common.whenSignedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class AuditUiState(
    val initials: String = "",
    val isLoading: Boolean = true,
    val subscriptions: List<DetectedSubscription> = emptyList(),
    val flaggedCount: Int = 0,
    val flaggedMonthlyLkr: Double = 0.0,
    val flaggedAnnualLkr: Double = 0.0,
    /** Flagged annual cost as a share of what's left to save for the goal; null without a goal. */
    val shareOfGoalRemainingPct: Double? = null,
    val goalTitle: String? = null,
    val message: String? = null
)

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    expenseRepository: ExpenseRepository,
    goalRepository: GoalRepository,
    accountRepository: AccountRepository,
    private val subscriptionReviewRepository: SubscriptionReviewRepository,
    private val detectSubscriptionsUseCase: DetectSubscriptionsUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val message = MutableStateFlow<String?>(null)

    private val data = authRepository.whenSignedIn<AuditUiState?>(signedOut = null) { userId ->
        combine(
            expenseRepository.getExpenses(userId),
            subscriptionReviewRepository.getReviews(userId),
            goalRepository.getPrimaryGoal(userId),
            accountRepository.getAccounts(userId)
        ) { expenses, reviews, goal, accounts ->
            val subscriptions = detectSubscriptionsUseCase(
                expenses = expenses,
                reviews = reviews,
                accounts = accounts,
                appUsageDaysProvider = { key -> UsageStatsHelper.getLastOpenedDaysAgo(context, key) }
            )
            val flagged = subscriptions.filter { it.isFlagged }
            val monthly = flagged.sumOf { it.monthlyCostLkr }
            AuditUiState(
                isLoading = false,
                subscriptions = subscriptions,
                flaggedCount = flagged.size,
                flaggedMonthlyLkr = monthly,
                flaggedAnnualLkr = monthly * 12,
                shareOfGoalRemainingPct = goal?.takeIf { it.remainingAmountLkr > 0 }
                    ?.let { monthly * 12 / it.remainingAmountLkr * 100 },
                goalTitle = goal?.title
            )
        }
    }

    private val initials = initialsFor(authRepository.userDisplayName, authRepository.userEmail)

    val uiState: StateFlow<AuditUiState> = combine(data, message) { data, message ->
        (data ?: AuditUiState()).copy(message = message)
    }.map { it.copy(initials = initials) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AuditUiState()
    )

    fun keepSubscription(subscription: DetectedSubscription) {
        record(subscription, SubscriptionDecision.KEEP, resetUnused = true)
        message.value = "Keeping ${subscription.title}"
    }

    /** The user cancels with the provider; the app remembers and re-flags it if it charges again. */
    fun markCancelled(subscription: DetectedSubscription) {
        record(subscription, SubscriptionDecision.CANCELLED)
        message.value = "${subscription.title} marked cancelled — saves ${lkr(subscription.monthlyCostLkr)}/mo"
    }

    fun markAllFlaggedCancelled() {
        val flagged = uiState.value.subscriptions.filter { it.isFlagged }
        if (flagged.isEmpty()) return
        flagged.forEach { record(it, SubscriptionDecision.CANCELLED) }
        message.value = "${flagged.size} marked cancelled — saves ${lkr(flagged.sumOf { it.monthlyCostLkr })}/mo"
    }

    fun recordUsageAnswer(subscription: DetectedSubscription, usedThisMonth: Boolean) {
        val userId = authRepository.currentUserId ?: return
        val newUnused = if (usedThisMonth) 0 else subscription.consecutiveUnusedMonths + 1
        val decision = if (subscription.status == SubscriptionStatus.CANCELLED) SubscriptionDecision.CANCELLED else SubscriptionDecision.KEEP
        subscriptionReviewRepository.saveReview(
            userId,
            SubscriptionReview(
                key = subscription.key,
                decision = decision,
                decidedAt = System.currentTimeMillis(),
                consecutiveUnusedMonths = newUnused
            )
        )
        message.value = if (usedThisMonth) {
            "Recorded as used this month 👍"
        } else {
            if (newUnused >= 2) "⚠️ High Risk: Unused for $newUnused consecutive months! Cancel recommended." else "Recorded as unused this month."
        }
    }

    private fun record(subscription: DetectedSubscription, decision: SubscriptionDecision, resetUnused: Boolean = false) {
        val userId = authRepository.currentUserId ?: return
        val newUnused = if (resetUnused) 0 else subscription.consecutiveUnusedMonths
        subscriptionReviewRepository.saveReview(
            userId,
            SubscriptionReview(subscription.key, decision, System.currentTimeMillis(), newUnused)
        )
    }

    fun dismissMessage() {
        message.value = null
    }
}
