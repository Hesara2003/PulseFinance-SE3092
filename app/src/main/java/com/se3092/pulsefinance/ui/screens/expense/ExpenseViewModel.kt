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
import com.se3092.pulsefinance.domain.model.ExpenseCategory
import com.se3092.pulsefinance.ui.common.presetIllustration
import com.se3092.pulsefinance.ui.components.NoticeTone
import com.se3092.pulsefinance.ui.components.UiNotice

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
    val notice: UiNotice? = null
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

    private val notice = MutableStateFlow<UiNotice?>(null)

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

    val uiState: StateFlow<AuditUiState> = combine(data, notice) { data, notice ->
        (data ?: AuditUiState()).copy(notice = notice)
    }.map { it.copy(initials = initials) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AuditUiState()
    )

    fun keepSubscription(subscription: DetectedSubscription) {
        record(subscription, SubscriptionDecision.KEEP, resetUnused = true)
        notice.value = UiNotice("Keeping ${subscription.title}", "${lkr(subscription.monthlyCostLkr)} a month", illustration = presetIllustration(subscription.title, ExpenseCategory.SUBSCRIPTION))
    }

    /** The user cancels with the provider; the app remembers and re-flags it if it charges again. */
    fun markCancelled(subscription: DetectedSubscription) {
        record(subscription, SubscriptionDecision.CANCELLED)
        notice.value = UiNotice("${subscription.title} marked cancelled", "Saves ${lkr(subscription.monthlyCostLkr)} a month", illustration = presetIllustration(subscription.title, ExpenseCategory.SUBSCRIPTION))
    }

    fun markAllFlaggedCancelled() {
        val flagged = uiState.value.subscriptions.filter { it.isFlagged }
        if (flagged.isEmpty()) return
        flagged.forEach { record(it, SubscriptionDecision.CANCELLED) }
        notice.value = UiNotice("${flagged.size} subscriptions marked cancelled", "Saves ${lkr(flagged.sumOf { it.monthlyCostLkr })} a month")
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
        notice.value = when {
            usedThisMonth -> UiNotice("Recorded as used this month", subscription.title)
            newUnused >= 2 -> UiNotice("Unused for $newUnused months in a row", "${subscription.title} is high risk. Consider cancelling it.", NoticeTone.ERROR)
            else -> UiNotice("Recorded as unused this month", subscription.title)
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

    fun dismissNotice() {
        notice.value = null
    }
}
