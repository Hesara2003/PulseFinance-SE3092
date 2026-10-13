package com.se3092.pulsefinance.ui.screens.income

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.se3092.pulsefinance.domain.model.Currency
import com.se3092.pulsefinance.domain.model.DAY_MILLIS
import com.se3092.pulsefinance.domain.model.IncomeSource
import com.se3092.pulsefinance.domain.model.IncomeStream
import com.se3092.pulsefinance.domain.model.InvoiceStatus
import com.se3092.pulsefinance.domain.model.Periods
import com.se3092.pulsefinance.domain.repository.AuthRepository
import com.se3092.pulsefinance.domain.repository.IncomeRepository
import com.se3092.pulsefinance.domain.usecase.ConvertCurrencyUseCase
import com.se3092.pulsefinance.ui.common.formatDate
import com.se3092.pulsefinance.ui.common.lkr
import com.se3092.pulsefinance.ui.common.signedLkr
import com.se3092.pulsefinance.ui.common.whenSignedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class SourceTotal(val source: IncomeSource, val amountLkr: Double, val count: Int)

/** Add-income form. [dayOffset] is relative to today: negative = received in the past, positive = due later. */
data class IncomeFormState(
    val isVisible: Boolean = false,
    val titleInput: String = "",
    val clientInput: String = "",
    val amountInput: String = "",
    val selectedSource: IncomeSource = IncomeSource.FREELANCE,
    val selectedCurrency: Currency = Currency.LKR,
    val exchangeRateInput: String = "1",
    val selectedStatus: InvoiceStatus = InvoiceStatus.RECEIVED,
    val dayOffset: Int = 0,
    val isCryptoLoss: Boolean = false,
    val isRecurringMonthly: Boolean = false,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null
)

data class IncomeScreenUiState(
    val isLoading: Boolean = true,
    val monthLabel: String = "",
    val filterSource: IncomeSource? = null,
    val incomes: List<IncomeStream> = emptyList(),
    val totalCount: Int = 0,
    val countBySource: Map<IncomeSource, Int> = emptyMap(),
    val realizedThisMonthLkr: Double = 0.0,
    val pendingLkr: Double = 0.0,
    val overdueLkr: Double = 0.0,
    val sourceBreakdown: List<SourceTotal> = emptyList(),
    val cryptoNetYtdLkr: Double = 0.0,
    val cryptoEntriesYtd: Int = 0,
    val form: IncomeFormState = IncomeFormState(),
    val toastNotification: Pair<String, String>? = null
) {
    val totalProjectedLkr: Double get() = realizedThisMonthLkr.coerceAtLeast(0.0) + pendingLkr
    val realizedRatio: Float get() = if (totalProjectedLkr > 0) (realizedThisMonthLkr.coerceAtLeast(0.0) / totalProjectedLkr).toFloat().coerceIn(0f, 1f) else 0f
    val pendingRatio: Float get() = if (totalProjectedLkr > 0) (pendingLkr / totalProjectedLkr).toFloat().coerceIn(0f, 1f) else 0f
}

@HiltViewModel
class IncomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val incomeRepository: IncomeRepository,
    private val convertCurrencyUseCase: ConvertCurrencyUseCase
) : ViewModel() {

    private val filter = MutableStateFlow<IncomeSource?>(null)
    private val form = MutableStateFlow(IncomeFormState())
    private val toast = MutableStateFlow<Pair<String, String>?>(null)

    private val incomes = authRepository.whenSignedIn<List<IncomeStream>?>(signedOut = null) { userId ->
        incomeRepository.getIncomeStreams(userId)
    }

    val uiState: StateFlow<IncomeScreenUiState> = combine(incomes, filter, form, toast) { incomes, filter, form, toast ->
        buildState(incomes, filter, form, toast)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = IncomeScreenUiState()
    )

    private fun buildState(
        incomes: List<IncomeStream>?,
        filter: IncomeSource?,
        form: IncomeFormState,
        toast: Pair<String, String>?
    ): IncomeScreenUiState {
        val now = System.currentTimeMillis()
        val base = IncomeScreenUiState(
            isLoading = incomes == null,
            monthLabel = formatDate(now, "MMMM yyyy"),
            filterSource = filter,
            form = form,
            toastNotification = toast
        )
        if (incomes == null) return base

        val month = Periods.month(now)
        val realizedThisMonth = incomes.filter { it.isRealized && it.dateReceivedOrExpected in month }
        val unpaid = incomes.filter { !it.isRealized }
        val yearStart = Calendar.getInstance().apply {
            timeInMillis = Periods.month(now).startMillis
            set(Calendar.MONTH, Calendar.JANUARY)
        }.timeInMillis
        val cryptoYtd = incomes.filter { it.source == IncomeSource.CRYPTO && it.isRealized && it.dateReceivedOrExpected >= yearStart }

        // Unpaid first (overdue at the very top) so follow-ups can't be missed, then newest first.
        val sorted = incomes.sortedWith(
            compareBy<IncomeStream> {
                when (it.effectiveStatus(now)) {
                    InvoiceStatus.OVERDUE -> 0
                    InvoiceStatus.PENDING_INVOICE -> 1
                    InvoiceStatus.RECEIVED -> 2
                }
            }.thenByDescending { it.dateReceivedOrExpected }
        )

        return base.copy(
            incomes = sorted.filter { filter == null || it.source == filter },
            totalCount = incomes.size,
            countBySource = incomes.groupingBy { it.source }.eachCount(),
            realizedThisMonthLkr = realizedThisMonth.sumOf { it.amountInLkr },
            pendingLkr = unpaid.sumOf { it.amountInLkr },
            overdueLkr = unpaid.filter { it.effectiveStatus(now) == InvoiceStatus.OVERDUE }.sumOf { it.amountInLkr },
            sourceBreakdown = IncomeSource.entries.map { source ->
                val items = realizedThisMonth.filter { it.source == source }
                SourceTotal(source, items.sumOf { it.amountInLkr }, items.size)
            },
            cryptoNetYtdLkr = cryptoYtd.sumOf { it.amountInLkr },
            cryptoEntriesYtd = cryptoYtd.size
        )
    }

    fun setFilter(source: IncomeSource?) {
        filter.value = source
    }

    fun markReceived(income: IncomeStream) {
        val userId = authRepository.currentUserId ?: return
        viewModelScope.launch {
            incomeRepository.updateInvoiceStatus(income.id, userId, InvoiceStatus.RECEIVED, System.currentTimeMillis())
                .onSuccess { toast.value = "Marked as received" to "+${lkr(income.amountInLkr)} · ${income.title}" }
                .onFailure { toast.value = "Couldn't update" to (it.localizedMessage ?: "Try again") }
        }
    }

    fun deleteIncome(income: IncomeStream) {
        val userId = authRepository.currentUserId ?: return
        viewModelScope.launch {
            incomeRepository.deleteIncomeStream(income.id, userId)
                .onSuccess { toast.value = "Income removed" to income.title }
                .onFailure { toast.value = "Couldn't delete" to (it.localizedMessage ?: "Try again") }
        }
    }

    fun dismissToast() {
        toast.value = null
    }

    // ---------------- Add income sheet ----------------

    fun showAddSheet() = form.update { IncomeFormState(isVisible = true) }
    fun hideAddSheet() = form.update { it.copy(isVisible = false, errorMessage = null) }

    fun onTitleChanged(title: String) = form.update { it.copy(titleInput = title) }
    fun onClientChanged(client: String) = form.update { it.copy(clientInput = client) }
    fun onAmountChanged(amount: String) = form.update { it.copy(amountInput = amount, errorMessage = null) }
    fun onRateChanged(rate: String) = form.update { it.copy(exchangeRateInput = rate, errorMessage = null) }
    fun onDayOffsetChanged(offset: Int) = form.update { it.copy(dayOffset = offset) }
    fun onCryptoLossToggled(isLoss: Boolean) = form.update { it.copy(isCryptoLoss = isLoss) }
    fun onRecurringToggled(isRecurring: Boolean) = form.update { it.copy(isRecurringMonthly = isRecurring) }

    fun onStatusChanged(status: InvoiceStatus) = form.update {
        // Received income is dated in the past; an invoice is due in the future.
        it.copy(selectedStatus = status, dayOffset = if (status == InvoiceStatus.RECEIVED) 0 else 14)
    }

    fun onSourceChanged(source: IncomeSource) {
        val currency = when (source) {
            IncomeSource.ADSENSE -> Currency.USD
            IncomeSource.CRYPTO -> Currency.USDT
            else -> Currency.LKR
        }
        form.update {
            it.copy(
                selectedSource = source,
                selectedCurrency = currency,
                exchangeRateInput = formatRate(convertCurrencyUseCase.getBenchmarkRate(currency)),
                isRecurringMonthly = source == IncomeSource.SALARY,
                isCryptoLoss = false
            )
        }
    }

    fun onCurrencyChanged(currency: Currency) = form.update {
        it.copy(selectedCurrency = currency, exchangeRateInput = formatRate(convertCurrencyUseCase.getBenchmarkRate(currency)))
    }

    fun submitIncome() {
        val current = form.value
        val amount = current.amountInput.replace(",", "").trim().toDoubleOrNull()
        if (amount == null || amount <= 0) {
            form.update { it.copy(errorMessage = "Enter an amount greater than zero") }
            return
        }
        val rate = if (current.selectedCurrency == Currency.LKR) 1.0 else current.exchangeRateInput.trim().toDoubleOrNull()
        if (rate == null || rate <= 0) {
            form.update { it.copy(errorMessage = "Enter the rate you actually received to LKR") }
            return
        }
        val userId = authRepository.currentUserId ?: run {
            form.update { it.copy(errorMessage = "You're signed out. Sign in again to save.") }
            return
        }

        val signedAmount = if (current.selectedSource == IncomeSource.CRYPTO && current.isCryptoLoss) -amount else amount
        val amountInLkr = convertCurrencyUseCase.toLkr(signedAmount, current.selectedCurrency, rate)
        val title = current.titleInput.trim().ifBlank { "${current.selectedSource.displayName} ${if (signedAmount < 0) "loss" else "income"}" }
        val client = current.clientInput.trim().ifBlank { current.selectedSource.displayName }
        val date = System.currentTimeMillis() + current.dayOffset * DAY_MILLIS

        viewModelScope.launch {
            form.update { it.copy(isSubmitting = true) }
            incomeRepository.saveIncomeStream(
                IncomeStream(
                    userId = userId,
                    source = current.selectedSource,
                    title = title,
                    clientOrPlatform = client,
                    originalAmount = signedAmount,
                    currency = current.selectedCurrency,
                    exchangeRateToLkr = rate,
                    amountInLkr = amountInLkr,
                    status = current.selectedStatus,
                    dateReceivedOrExpected = date,
                    isRecurringMonthly = current.isRecurringMonthly
                )
            ).fold(
                onSuccess = {
                    form.value = IncomeFormState()
                    toast.value = (if (current.selectedStatus == InvoiceStatus.RECEIVED) "Income recorded" else "Invoice tracked") to
                        "${signedLkr(amountInLkr)} · $title"
                },
                onFailure = { e ->
                    form.update { it.copy(isSubmitting = false, errorMessage = e.localizedMessage ?: "Couldn't save") }
                }
            )
        }
    }

    private fun formatRate(rate: Double): String =
        if (rate == rate.toLong().toDouble()) rate.toLong().toString() else rate.toString()
}
