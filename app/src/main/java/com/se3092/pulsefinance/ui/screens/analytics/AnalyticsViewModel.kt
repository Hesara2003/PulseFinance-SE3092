package com.se3092.pulsefinance.ui.screens.analytics

import com.se3092.pulsefinance.ui.common.initialsFor
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.se3092.pulsefinance.domain.model.DAY_MILLIS
import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.model.ExpenseCategory
import com.se3092.pulsefinance.domain.model.IncomeStream
import com.se3092.pulsefinance.domain.model.Period
import com.se3092.pulsefinance.domain.model.Periods
import com.se3092.pulsefinance.domain.repository.AuthRepository
import com.se3092.pulsefinance.domain.repository.ExpenseRepository
import com.se3092.pulsefinance.domain.repository.IncomeRepository
import com.se3092.pulsefinance.ui.common.TransactionItem
import com.se3092.pulsefinance.ui.common.buildLedger
import com.se3092.pulsefinance.ui.common.formatDate
import com.se3092.pulsefinance.ui.common.whenSignedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

enum class AnalyticsInterval { WEEKLY, MONTHLY }

/** One capsule in the bar chart: a day (weekly view) or a week of the month (monthly view). */
data class TimelineBar(
    val primaryLabel: String,
    val secondaryLabel: String,
    val fillRatio: Float,
    val amountLkr: Double,
    val isPeak: Boolean
)

data class CategoryTotal(val category: ExpenseCategory, val amountLkr: Double, val share: Float)

data class AnalyticsUiState(
    val initials: String = "",
    val isLoading: Boolean = true,
    val interval: AnalyticsInterval = AnalyticsInterval.WEEKLY,
    /** 0 = current period, -1 = previous, ... */
    val periodOffset: Int = 0,
    val periodLabel: String = "",
    val bars: List<TimelineBar> = emptyList(),
    val selectedBarIndex: Int = 0,
    val totalSpentLkr: Double = 0.0,
    val committedLkr: Double = 0.0,
    val discretionaryLkr: Double = 0.0,
    val incomeLkr: Double = 0.0,
    val categoryTotals: List<CategoryTotal> = emptyList(),
    val transactions: List<TransactionItem> = emptyList(),
    val showAllTransactions: Boolean = false
) {
    val canGoForward: Boolean get() = periodOffset < 0
    val selectedBar: TimelineBar? get() = bars.getOrNull(selectedBarIndex)
}

private data class AnalyticsControls(
    val interval: AnalyticsInterval = AnalyticsInterval.WEEKLY,
    val periodOffset: Int = 0,
    val selectedBarIndex: Int? = null,
    val showAll: Boolean = false
)

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    authRepository: AuthRepository,
    expenseRepository: ExpenseRepository,
    incomeRepository: IncomeRepository
) : ViewModel() {

    private val controls = MutableStateFlow(AnalyticsControls())

    private val data = authRepository.whenSignedIn<Pair<List<Expense>, List<IncomeStream>>?>(signedOut = null) { userId ->
        combine(expenseRepository.getExpenses(userId), incomeRepository.getIncomeStreams(userId)) { e, i -> e to i }
    }

    private val initials = initialsFor(authRepository.userDisplayName, authRepository.userEmail)

    val uiState: StateFlow<AnalyticsUiState> = combine(data, controls) { data, controls ->
        if (data == null) AnalyticsUiState(interval = controls.interval)
        else buildState(data.first, data.second, controls)
    }.map { it.copy(initials = initials) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AnalyticsUiState()
    )

    private fun buildState(expenses: List<Expense>, incomes: List<IncomeStream>, controls: AnalyticsControls): AnalyticsUiState {
        val now = System.currentTimeMillis()
        val period = when (controls.interval) {
            AnalyticsInterval.WEEKLY -> Periods.week(now, controls.periodOffset)
            AnalyticsInterval.MONTHLY -> Periods.month(now, controls.periodOffset)
        }
        val periodExpenses = expenses.filter { it.timestamp in period }
        val periodIncomes = incomes.filter { it.isRealized && it.dateReceivedOrExpected in period }

        val buckets = when (controls.interval) {
            AnalyticsInterval.WEEKLY -> (0 until 7).map { i ->
                val start = period.startMillis + i * DAY_MILLIS
                Triple(formatDate(start, "dd"), formatDate(start, "EEE"), Period(start, start + DAY_MILLIS))
            }
            AnalyticsInterval.MONTHLY -> generateSequence(period.startMillis) { it + 7 * DAY_MILLIS }
                .takeWhile { it < period.endMillis }
                .mapIndexed { i, start ->
                    val end = minOf(start + 7 * DAY_MILLIS, period.endMillis)
                    Triple("W${i + 1}", "${formatDate(start, "d")}–${formatDate(end - 1, "d")}", Period(start, end))
                }
                .toList()
        }
        val bucketTotals = buckets.map { (_, _, range) -> periodExpenses.filter { it.timestamp in range }.sumOf { it.amountLkr } }
        val peak = bucketTotals.maxOrNull() ?: 0.0
        val peakIndex = if (peak > 0) bucketTotals.indexOf(peak) else -1
        val bars = buckets.mapIndexed { i, (primary, secondary, _) ->
            TimelineBar(
                primaryLabel = primary,
                secondaryLabel = secondary,
                fillRatio = if (peak > 0) (bucketTotals[i] / peak).toFloat() else 0f,
                amountLkr = bucketTotals[i],
                isPeak = i == peakIndex
            )
        }

        val total = periodExpenses.sumOf { it.amountLkr }
        val categoryTotals = periodExpenses
            .groupBy { it.category }
            .map { (category, items) ->
                val sum = items.sumOf { it.amountLkr }
                CategoryTotal(category, sum, if (total > 0) (sum / total).toFloat() else 0f)
            }
            .sortedByDescending { it.amountLkr }

        val label = when (controls.interval) {
            AnalyticsInterval.WEEKLY -> when (controls.periodOffset) {
                0 -> "This week"
                -1 -> "Last week"
                else -> "${formatDate(period.startMillis, "dd MMM")} – ${formatDate(period.endMillis - 1, "dd MMM")}"
            }
            AnalyticsInterval.MONTHLY -> formatDate(period.startMillis, "MMMM yyyy")
        }

        return AnalyticsUiState(
            isLoading = false,
            interval = controls.interval,
            periodOffset = controls.periodOffset,
            periodLabel = label,
            bars = bars,
            selectedBarIndex = (controls.selectedBarIndex ?: peakIndex.coerceAtLeast(0)).coerceIn(0, (bars.size - 1).coerceAtLeast(0)),
            totalSpentLkr = total,
            committedLkr = periodExpenses.filter { it.isCommitted }.sumOf { it.amountLkr },
            discretionaryLkr = periodExpenses.filter { !it.isCommitted }.sumOf { it.amountLkr },
            incomeLkr = periodIncomes.sumOf { it.amountInLkr },
            categoryTotals = categoryTotals,
            transactions = buildLedger(periodIncomes, periodExpenses),
            showAllTransactions = controls.showAll
        )
    }

    fun switchInterval(interval: AnalyticsInterval) {
        controls.value = AnalyticsControls(interval = interval)
    }

    fun shiftPeriod(delta: Int) {
        controls.update { it.copy(periodOffset = (it.periodOffset + delta).coerceAtMost(0), selectedBarIndex = null) }
    }

    fun selectBar(index: Int) {
        controls.update { it.copy(selectedBarIndex = index) }
    }

    fun toggleViewAll() {
        controls.update { it.copy(showAll = !it.showAll) }
    }
}
