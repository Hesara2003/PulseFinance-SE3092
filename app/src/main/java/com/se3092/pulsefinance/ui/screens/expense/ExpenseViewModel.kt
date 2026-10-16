package com.se3092.pulsefinance.ui.screens.expense

import com.se3092.pulsefinance.ui.common.initialsFor
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.se3092.pulsefinance.domain.model.DetectedSubscription
import com.se3092.pulsefinance.domain.model.SubscriptionDecision
import com.se3092.pulsefinance.domain.model.SubscriptionReview
import com.se3092.pulsefinance.domain.repository.AuthRepository
import com.se3092.pulsefinance.domain.repository.ExpenseRepository
import com.se3092.pulsefinance.domain.repository.GoalRepository
import com.se3092.pulsefinance.domain.repository.SubscriptionReviewRepository
import com.se3092.pulsefinance.domain.usecase.DetectSubscriptionsUseCase
import com.se3092.pulsefinance.ui.common.lkr
import com.se3092.pulsefinance.ui.common.whenSignedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
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
    private val subscriptionReviewRepository: SubscriptionReviewRepository,
    private val detectSubscriptionsUseCase: DetectSubscriptionsUseCase
) : ViewModel() {

    private val message = MutableStateFlow<String?>(null)

    private val data = authRepository.whenSignedIn<AuditUiState?>(signedOut = null) { userId ->
        combine(
            expenseRepository.getExpenses(userId),
            subscriptionReviewRepository.getReviews(userId),
            goalRepository.getPrimaryGoal(userId)
        ) { expenses, reviews, goal ->
            val subscriptions = detectSubscriptionsUseCase(expenses, reviews)
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
        record(subscription, SubscriptionDecision.KEEP)
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

    private fun record(subscription: DetectedSubscription, decision: SubscriptionDecision) {
        val userId = authRepository.currentUserId ?: return
        subscriptionReviewRepository.saveReview(
            userId,
            SubscriptionReview(subscription.key, decision, System.currentTimeMillis())
        )
    }

    fun dismissMessage() {
        message.value = null
    }
}
