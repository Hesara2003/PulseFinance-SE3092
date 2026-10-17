package com.se3092.pulsefinance.ui.screens.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.model.ExpenseCategory
import com.se3092.pulsefinance.domain.model.IncomeStream
import com.se3092.pulsefinance.domain.repository.AccountRepository
import com.se3092.pulsefinance.domain.repository.AuthRepository
import com.se3092.pulsefinance.domain.repository.ExpenseRepository
import com.se3092.pulsefinance.domain.repository.IncomeRepository
import com.se3092.pulsefinance.domain.repository.SubscriptionReviewRepository
import com.se3092.pulsefinance.domain.usecase.DetectSubscriptionsUseCase
import com.se3092.pulsefinance.ui.util.Formatters
import com.se3092.pulsefinance.ui.util.TransactionVisual
import com.se3092.pulsefinance.ui.util.TransactionVisuals
import com.se3092.pulsefinance.ui.util.userScopedFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class TimelineDay(
    val dayNumber: String,
    val dayOfWeek: String,
    val fillRatio: Float,
    val amountLkr: Double,
    val isPeak: Boolean = false
)

data class CategorySlice(
    val category: ExpenseCategory,
    val amountLkr: Double,
    val share: Float,
    val visual: TransactionVisual
)

data class TransactionDisplayItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val amountText: String,
    val isPositive: Boolean,
    val timeLabel: String,
    val visual: TransactionVisual,
    val isZombie: Boolean = false
)

/** User-controlled selections; everything else in [AnalyticsUiState] is derived from data. */
private data class AnalyticsSelection(
    val selectedInterval: String = "weekly",
    /** 0 = current week/month, -1 = previous, … */
    val periodOffset: Int = 0,
    val selectedBarIndex: Int? = null,
    val showAllTransactions: Boolean = false
)

data class AnalyticsUiState(
    val selectedInterval: String = "weekly", // "weekly" or "monthly"
    val periodOffset: Int = 0,
    val currentPeriod: String = "",
    val selectedBarIndex: Int = 6,
    val showAllTransactions: Boolean = false,
    val timelineDays: List<TimelineDay> = emptyList(),
    val totalSpentLkr: Double = 0.0,
    val committedLkr: Double = 0.0,
    val discretionaryLkr: Double = 0.0,
    val categoryBreakdown: List<CategorySlice> = emptyList(),
    val transactions: List<TransactionDisplayItem> = emptyList(),
    val userInitials: String = ""
) {
    val committedPercentage: Float get() = if (totalSpentLkr > 0) (committedLkr / totalSpentLkr).toFloat() else 0f
    val discretionaryPercentage: Float get() = if (totalSpentLkr > 0) (discretionaryLkr / totalSpentLkr).toFloat() else 0f
    val heroAmountFormatted: String get() = Formatters.lkr(totalSpentLkr, decimals = 2)
    val canShiftForward: Boolean get() = periodOffset < 0
    val selectedDayStat: TimelineDay
        get() = timelineDays.getOrElse(selectedBarIndex) { timelineDays.lastOrNull() ?: TimelineDay("", "", 0f, 0.0) }
}

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    expenseRepository: ExpenseRepository,
    incomeRepository: IncomeRepository,
    accountRepository: AccountRepository,
    subscriptionReviewRepository: SubscriptionReviewRepository,
    private val detectSubscriptions: DetectSubscriptionsUseCase
) : ViewModel() {

    private val selection = MutableStateFlow(AnalyticsSelection())

    private val expensesFlow = authRepository.userScopedFlow(emptyList()) { expenseRepository.getExpenses(it) }
    private val incomeFlow = authRepository.userScopedFlow(emptyList()) { incomeRepository.getIncomeStreams(it) }
    private val zombieKeysFlow = combine(
        expensesFlow,
        authRepository.userScopedFlow(emptyList()) { accountRepository.getAccounts(it) },
        authRepository.userScopedFlow(emptyMap()) { subscriptionReviewRepository.observeReviews(it) }
    ) { expenses, accounts, reviews ->
        detectSubscriptions(expenses, accounts, reviews).filter { it.isFlagged }.map { it.key }.toSet()
    }

    val uiState: StateFlow<AnalyticsUiState> = combine(
        expensesFlow,
        incomeFlow,
        zombieKeysFlow,
        selection
    ) { expenses, incomes, zombieKeys, sel ->
        buildState(expenses, incomes, zombieKeys, sel)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AnalyticsUiState()
    )

    private fun buildState(
        expenses: List<Expense>,
        incomes: List<IncomeStream>,
        zombieKeys: Set<String>,
        sel: AnalyticsSelection
    ): AnalyticsUiState {
        val buckets = if (sel.selectedInterval == "monthly") monthlyBuckets(sel.periodOffset) else weeklyBuckets(sel.periodOffset)
        val periodStart = buckets.first().start
        val periodEnd = buckets.last().end

        val periodExpenses = expenses.filter { it.timestamp in periodStart until periodEnd }
        val total = periodExpenses.sumOf { it.amountLkr }
        val committed = periodExpenses.filter { it.isCommitted }.sumOf { it.amountLkr }

        val bucketTotals = buckets.map { bucket ->
            periodExpenses.filter { it.timestamp in bucket.start until bucket.end }.sumOf { it.amountLkr }
        }
        val peak = bucketTotals.maxOrNull() ?: 0.0
        val timeline = buckets.mapIndexed { i, bucket ->
            TimelineDay(
                dayNumber = bucket.primaryLabel,
                dayOfWeek = bucket.secondaryLabel,
                fillRatio = if (peak > 0) (bucketTotals[i] / peak).toFloat().coerceIn(0.04f, 1f) else 0.04f,
                amountLkr = bucketTotals[i],
                isPeak = peak > 0 && bucketTotals[i] == peak
            )
        }
        val defaultBar = timeline.indexOfFirst { it.isPeak }.takeIf { it >= 0 } ?: timeline.lastIndex

        val breakdown = periodExpenses
            .groupBy { it.category }
            .map { (category, list) ->
                val amount = list.sumOf { it.amountLkr }
                CategorySlice(
                    category = category,
                    amountLkr = amount,
                    share = if (total > 0) (amount / total).toFloat() else 0f,
                    visual = TransactionVisuals.forCategory(category)
                )
            }
            .sortedByDescending { it.amountLkr }

        val now = System.currentTimeMillis()
        val ledger = periodExpenses.map { expense ->
            val isZombie = DetectSubscriptionsUseCase.subscriptionKeyFor(expense.title) in zombieKeys
            TransactionDisplayItem(
                id = "exp_${expense.id}",
                title = expense.title,
                subtitle = when {
                    isZombie -> "Auto-Debit (Zombie Subscription)"
                    expense.isCommitted -> "${expense.category.displayName} · Fixed"
                    else -> "${expense.category.displayName} · ${expense.paymentAccountName}"
                },
                amountText = Formatters.signedLkr(expense.amountLkr, isPositive = false, decimals = 2),
                isPositive = false,
                timeLabel = Formatters.relativeDay(expense.timestamp, now),
                visual = TransactionVisuals.forCategory(expense.category),
                isZombie = isZombie
            ) to expense.timestamp
        } + incomes
            .filter { it.isRealized && it.dateReceivedOrExpected in periodStart until periodEnd }
            .map { income ->
                TransactionDisplayItem(
                    id = "inc_${income.id}",
                    title = income.title,
                    subtitle = income.clientOrPlatform,
                    amountText = "+ ${Formatters.original(income.originalAmount, income.currency)}",
                    isPositive = true,
                    timeLabel = Formatters.relativeDay(income.dateReceivedOrExpected, now),
                    visual = TransactionVisuals.forIncome(income.source)
                ) to income.dateReceivedOrExpected
            }

        return AnalyticsUiState(
            selectedInterval = sel.selectedInterval,
            periodOffset = sel.periodOffset,
            currentPeriod = periodLabel(sel, periodStart, periodEnd),
            selectedBarIndex = (sel.selectedBarIndex ?: defaultBar).coerceIn(0, timeline.lastIndex),
            showAllTransactions = sel.showAllTransactions,
            timelineDays = timeline,
            totalSpentLkr = total,
            committedLkr = committed,
            discretionaryLkr = total - committed,
            categoryBreakdown = breakdown,
            transactions = ledger.sortedByDescending { it.second }.map { it.first },
            userInitials = Formatters.initials(authRepository.currentUser)
        )
    }

    private data class Bucket(val start: Long, val end: Long, val primaryLabel: String, val secondaryLabel: String)

    /** Seven days ending today (shifted back by whole weeks). */
    private fun weeklyBuckets(offset: Int): List<Bucket> {
        val dayNum = SimpleDateFormat("d", Locale.US)
        val dayName = SimpleDateFormat("EEE", Locale.US)
        val todayStart = startOfDay()
        return (6 downTo 0).map { daysAgo ->
            val start = (todayStart.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, offset * 7 - daysAgo) }
            val end = (start.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 1) }
            Bucket(start.timeInMillis, end.timeInMillis, dayNum.format(start.time), dayName.format(start.time))
        }
    }

    /** Seven calendar months ending with the selected month. */
    private fun monthlyBuckets(offset: Int): List<Bucket> {
        val monthName = SimpleDateFormat("MMM", Locale.US)
        val yearShort = SimpleDateFormat("''yy", Locale.US)
        val monthStart = startOfDay().apply { set(Calendar.DAY_OF_MONTH, 1) }
        return (6 downTo 0).map { monthsAgo ->
            val start = (monthStart.clone() as Calendar).apply { add(Calendar.MONTH, offset - monthsAgo) }
            val end = (start.clone() as Calendar).apply { add(Calendar.MONTH, 1) }
            Bucket(start.timeInMillis, end.timeInMillis, monthName.format(start.time), yearShort.format(start.time))
        }
    }

    private fun periodLabel(sel: AnalyticsSelection, start: Long, endExclusive: Long): String {
        if (sel.selectedInterval == "monthly") {
            return "${SimpleDateFormat("MMM yyyy", Locale.US).format(Date(start))} – " +
                SimpleDateFormat("MMM yyyy", Locale.US).format(Date(endExclusive - 1))
        }
        if (sel.periodOffset == 0) return "This Week"
        val fmt = SimpleDateFormat("MMM d", Locale.US)
        return "${fmt.format(Date(start))} – ${fmt.format(Date(endExclusive - 1))}"
    }

    private fun startOfDay(): Calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    fun switchInterval(interval: String) {
        selection.update { it.copy(selectedInterval = interval, periodOffset = 0, selectedBarIndex = null) }
    }

    fun shiftPeriod(delta: Int) {
        selection.update {
            it.copy(periodOffset = (it.periodOffset + delta).coerceAtMost(0), selectedBarIndex = null)
        }
    }

    fun selectBar(index: Int) {
        selection.update { it.copy(selectedBarIndex = index) }
    }

    fun toggleViewAll() {
        selection.update { it.copy(showAllTransactions = !it.showAllTransactions) }
    }
}
