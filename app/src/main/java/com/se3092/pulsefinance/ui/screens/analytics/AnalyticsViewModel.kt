package com.se3092.pulsefinance.ui.screens.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.se3092.pulsefinance.domain.model.ExpenseCategory
import com.se3092.pulsefinance.domain.repository.AuthRepository
import com.se3092.pulsefinance.domain.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class SubscriptionItem(
    val title: String,
    val amountLkr: Double,
    val sourceAccount: String,
    val isMarkedForCancellation: Boolean = false
)

data class TimelineDay(
    val dayNumber: String,
    val dayOfWeek: String,
    val fillRatio: Float,
    val amountLkr: Double,
    val isPeak: Boolean = false
)

data class TransactionDisplayItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val amountText: String,
    val isPositive: Boolean,
    val timeLabel: String,
    val isZombie: Boolean = false,
    val iconType: String = "payment" // "payment", "fitness", "taxi", "wifi"
)

data class AnalyticsUiState(
    val selectedInterval: String = "weekly", // "weekly" or "monthly"
    val periodIndex: Int = 1, // "April 2025"
    val selectedBarIndex: Int = 6, // 23 Sun (Peak)
    val showAllTransactions: Boolean = false,
    val periods: List<String> = listOf("March 2025", "April 2025", "May 2025", "October 2026"),
    val timelineDays: List<TimelineDay> = listOf(
        TimelineDay("17", "Sun", 0.65f, 24100.0),
        TimelineDay("18", "Mon", 0.40f, 12650.0),
        TimelineDay("19", "Tue", 0.80f, 31200.0),
        TimelineDay("20", "Wed", 0.55f, 19400.0),
        TimelineDay("21", "Thu", 0.25f, 9150.0),
        TimelineDay("22", "Fri", 0.70f, 28300.0),
        TimelineDay("23", "Sun", 0.90f, 38400.0, isPeak = true)
    ),
    val totalSpentLkr: Double = 148200.0,
    val committedLkr: Double = 84000.0,
    val discretionaryLkr: Double = 64200.0,
    val committedPercentage: Float = 0.57f,
    val discretionaryPercentage: Float = 0.43f,
    val categoryTotals: Map<ExpenseCategory, Double> = emptyMap(),
    val zombieSubscriptions: List<SubscriptionItem> = emptyList(),
    val transactions: List<TransactionDisplayItem> = listOf(
        TransactionDisplayItem(
            id = "tx_1",
            title = "Client Milestone Payout",
            subtitle = "Freelance USD Wire",
            amountText = "+ USD 450.00",
            isPositive = true,
            timeLabel = "Today 11:24 AM",
            iconType = "payment"
        ),
        TransactionDisplayItem(
            id = "tx_2",
            title = "Fitness First Gym",
            subtitle = "Auto-Debit (Zombie Subscription)",
            amountText = "- LKR 8,500.00",
            isPositive = false,
            timeLabel = "Yesterday",
            isZombie = true,
            iconType = "fitness"
        ),
        TransactionDisplayItem(
            id = "tx_3",
            title = "PickMe Daily Transit",
            subtitle = "Discretionary Travel",
            amountText = "- LKR 1,450.00",
            isPositive = false,
            timeLabel = "Apr 21",
            iconType = "taxi"
        ),
        TransactionDisplayItem(
            id = "tx_4",
            title = "Dialog Broadband Bill",
            subtitle = "Fixed Monthly Utility",
            amountText = "- LKR 3,890.00",
            isPositive = false,
            timeLabel = "Apr 19",
            iconType = "wifi"
        ),
        TransactionDisplayItem(
            id = "tx_5",
            title = "Upwork Escrow Wire",
            subtitle = "Mobile Frontend Milestone",
            amountText = "+ USD 1,200.00",
            isPositive = true,
            timeLabel = "Apr 18",
            iconType = "payment"
        )
    )
) {
    val currentPeriod: String get() = periods.getOrElse(periodIndex) { "April 2025" }

    val heroAmountFormatted: String get() {
        return if (selectedInterval == "monthly") {
            "LKR 586,300.00"
        } else {
            when (periodIndex) {
                1 -> "LKR 148,200.00"
                3 -> "LKR 192,450.00"
                else -> "LKR 124,800.00"
            }
        }
    }

    val selectedDayStat: TimelineDay get() = timelineDays.getOrElse(selectedBarIndex) { timelineDays.last() }
}

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private val userId: String
        get() = authRepository.currentUserId ?: "guest_kavindu"

    private val _screenState = MutableStateFlow(AnalyticsUiState())

    val uiState: StateFlow<AnalyticsUiState> = combine(
        expenseRepository.getExpenses(userId),
        _screenState
    ) { expenses, state ->
        val total = expenses.sumOf { it.amountLkr }
        val committed = expenses.filter { it.isCommitted }.sumOf { it.amountLkr }
        val discretionary = expenses.filter { !it.isCommitted }.sumOf { it.amountLkr }

        val committedPct = if (total > 0) (committed / total).toFloat() else 0.57f
        val discretionaryPct = if (total > 0) (discretionary / total).toFloat() else 0.43f

        val categoryMap = expenses.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amountLkr } }

        state.copy(
            totalSpentLkr = if (total > 0) total else 148200.0,
            committedLkr = if (committed > 0) committed else 84000.0,
            discretionaryLkr = if (discretionary > 0) discretionary else 64200.0,
            committedPercentage = committedPct,
            discretionaryPercentage = discretionaryPct,
            categoryTotals = categoryMap
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AnalyticsUiState()
    )

    fun switchInterval(interval: String) {
        _screenState.update { it.copy(selectedInterval = interval) }
    }

    fun shiftPeriod(delta: Int) {
        _screenState.update {
            val maxIdx = it.periods.size - 1
            val newIdx = (it.periodIndex + delta).coerceIn(0, maxIdx)
            it.copy(periodIndex = newIdx)
        }
    }

    fun selectBar(index: Int) {
        _screenState.update { it.copy(selectedBarIndex = index) }
    }

    fun toggleViewAll() {
        _screenState.update { it.copy(showAllTransactions = !it.showAllTransactions) }
    }
}
