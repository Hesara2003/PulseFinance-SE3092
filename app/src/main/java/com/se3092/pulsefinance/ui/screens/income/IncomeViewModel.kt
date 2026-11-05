package com.se3092.pulsefinance.ui.screens.income

import com.se3092.pulsefinance.ui.common.initialsFor
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.se3092.pulsefinance.domain.model.ApitTaxBand
import com.se3092.pulsefinance.domain.model.Currency
import com.se3092.pulsefinance.domain.model.DAY_MILLIS
import com.se3092.pulsefinance.domain.model.IncomeSource
import com.se3092.pulsefinance.domain.model.IncomeStream
import com.se3092.pulsefinance.domain.model.InvoiceStatus
import com.se3092.pulsefinance.domain.model.Periods
import com.se3092.pulsefinance.domain.repository.AuthRepository
import com.se3092.pulsefinance.domain.repository.IncomeRepository
import com.se3092.pulsefinance.domain.repository.TaxConfigRepository
import com.se3092.pulsefinance.domain.usecase.ConvertCurrencyUseCase
import com.se3092.pulsefinance.domain.usecase.asThisMonth
import com.se3092.pulsefinance.domain.usecase.recurringIncomeDue
import com.se3092.pulsefinance.ui.common.formatDate
import com.se3092.pulsefinance.ui.common.lkr
import com.se3092.pulsefinance.ui.common.signedLkr
import com.se3092.pulsefinance.ui.common.whenSignedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject
import com.se3092.pulsefinance.ui.common.incomeSourceIllustration
import com.se3092.pulsefinance.ui.components.NoticeTone
import com.se3092.pulsefinance.ui.components.UiNotice

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
    val receivedLkrInput: String = "",
    val isCreditedLkrMode: Boolean = false,
    val rateSourceInfo: String = "",
    val selectedStatus: InvoiceStatus = InvoiceStatus.RECEIVED,
    val dayOffset: Int = 0,
    val isCryptoLoss: Boolean = false,
    val isRecurringMonthly: Boolean = false,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    /** The record being edited (FR-INC-10); null when adding a new one. */
    val editing: IncomeStream? = null,
    /** False while editing means the stored date is kept; picking a date chip changes it. */
    val dateChanged: Boolean = false
) {
    val isEditing: Boolean get() = editing != null
    val usesOriginalDate: Boolean get() = editing != null && !dateChanged

    /** The date to save: the stored one while editing untouched, otherwise today plus [dayOffset]. */
    fun dateMillis(now: Long): Long =
        if (usesOriginalDate) editing!!.dateReceivedOrExpected else now + dayOffset * DAY_MILLIS

    companion object {
        /**
         * A form pre-filled from [income]. The rate is the one stored with the record, never today's
         * (ADR-0001: the rate received is frozen per entry), and a loss shows as a positive amount
         * with the loss toggle on.
         */
        fun editing(income: IncomeStream) = IncomeFormState(
            isVisible = true,
            titleInput = income.title,
            clientInput = income.clientOrPlatform,
            amountInput = formatInputNumber(kotlin.math.abs(income.originalAmount)),
            selectedSource = income.source,
            selectedCurrency = income.currency,
            exchangeRateInput = formatInputNumber(income.exchangeRateToLkr),
            rateSourceInfo = if (income.currency == Currency.LKR) "" else "Rate you recorded: ${formatInputNumber(income.exchangeRateToLkr)} LKR/${income.currency.name}",
            selectedStatus = if (income.status == InvoiceStatus.OVERDUE) InvoiceStatus.PENDING_INVOICE else income.status,
            isCryptoLoss = income.source == IncomeSource.CRYPTO && income.originalAmount < 0,
            isRecurringMonthly = income.isRecurringMonthly,
            editing = income
        )
    }
}

/** A number as he would type it: no trailing ".0", no grouping. */
internal fun formatInputNumber(value: Double): String =
    if (value == value.toLong().toDouble()) value.toLong().toString() else value.toString()

data class IncomeScreenUiState(
    val initials: String = "",
    val isLoading: Boolean = true,
    val monthLabel: String = "",
    val filterSource: IncomeSource? = null,
    /** Only income paid in a foreign currency (USD AdSense, USDT trades…); combines with [filterSource]. */
    val foreignCurrencyOnly: Boolean = false,
    val incomes: List<IncomeStream> = emptyList(),
    /** Monthly income not yet recorded this month; he taps to add it (FR-INC-11). */
    val recurringDue: List<IncomeStream> = emptyList(),
    val totalCount: Int = 0,
    val foreignCurrencyCount: Int = 0,
    val countBySource: Map<IncomeSource, Int> = emptyMap(),
    val realizedThisMonthLkr: Double = 0.0,
    val pendingLkr: Double = 0.0,
    val overdueLkr: Double = 0.0,
    val sourceBreakdown: List<SourceTotal> = emptyList(),
    val cryptoNetYtdLkr: Double = 0.0,
    val cryptoEntriesYtd: Int = 0,
    val isPayslipSheetVisible: Boolean = false,
    val totalEpfAccumulatedLkr: Double = 0.0,
    val form: IncomeFormState = IncomeFormState(),
    val notice: UiNotice? = null
) {
    val totalProjectedLkr: Double get() = realizedThisMonthLkr.coerceAtLeast(0.0) + pendingLkr
    val realizedRatio: Float get() = if (totalProjectedLkr > 0) (realizedThisMonthLkr.coerceAtLeast(0.0) / totalProjectedLkr).toFloat().coerceIn(0f, 1f) else 0f
    val pendingRatio: Float get() = if (totalProjectedLkr > 0) (pendingLkr / totalProjectedLkr).toFloat().coerceIn(0f, 1f) else 0f
}

private data class IncomeFilter(
    val source: IncomeSource? = null,
    val foreignCurrencyOnly: Boolean = false
)

@HiltViewModel
class IncomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val incomeRepository: IncomeRepository,
    private val convertCurrencyUseCase: ConvertCurrencyUseCase,
    val calculatePayslipUseCase: com.se3092.pulsefinance.domain.usecase.CalculatePayslipUseCase,
    taxConfigRepository: TaxConfigRepository
) : ViewModel() {

    /** APIT schedule from Firestore config (FR-LK-2); starts with the bundled bands. */
    val apitBands: StateFlow<List<ApitTaxBand>> = taxConfigRepository.observeApitBands().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ApitTaxBand.DEFAULTS
    )

    private val filter = MutableStateFlow(IncomeFilter())
    private val form = MutableStateFlow(IncomeFormState())
    private val isPayslipVisible = MutableStateFlow(false)
    private val toast = MutableStateFlow<UiNotice?>(null)
    /** Suggestions he waved off this session; they come back next time the screen is opened. */
    private val dismissedRecurring = MutableStateFlow<Set<String>>(emptySet())

    private val incomes = authRepository.whenSignedIn<List<IncomeStream>?>(signedOut = null) { userId ->
        incomeRepository.getIncomeStreams(userId)
    }

    private val initials = initialsFor(authRepository.userDisplayName, authRepository.userEmail)

    val uiState: StateFlow<IncomeScreenUiState> = combine(incomes, filter, form, isPayslipVisible, toast) { incomes, filter, form, isPayslip, toast ->
        buildState(incomes, filter, form, isPayslip, toast)
    }.combine(dismissedRecurring) { state, dismissed ->
        state.copy(recurringDue = state.recurringDue.filter { it.id !in dismissed })
    }.map { it.copy(initials = initials) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = IncomeScreenUiState()
    )

    private fun buildState(
        incomes: List<IncomeStream>?,
        filter: IncomeFilter,
        form: IncomeFormState,
        isPayslip: Boolean,
        toast: UiNotice?
    ): IncomeScreenUiState {
        val now = System.currentTimeMillis()
        val base = IncomeScreenUiState(
            isLoading = incomes == null,
            monthLabel = formatDate(now, "MMMM yyyy"),
            filterSource = filter.source,
            foreignCurrencyOnly = filter.foreignCurrencyOnly,
            isPayslipSheetVisible = isPayslip,
            form = form,
            notice = toast
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
        val totalEpfAccumulated = incomes.filter { it.source == IncomeSource.SALARY && it.isRealized }
            .sumOf { (it.amountInLkr / 0.86) * 0.20 }

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
            incomes = sorted.filter { income ->
                (filter.source == null || income.source == filter.source) &&
                    (!filter.foreignCurrencyOnly || income.currency != Currency.LKR)
            },
            recurringDue = recurringIncomeDue(incomes, now),
            totalCount = incomes.size,
            foreignCurrencyCount = incomes.count { it.currency != Currency.LKR },
            countBySource = incomes.groupingBy { it.source }.eachCount(),
            realizedThisMonthLkr = realizedThisMonth.sumOf { it.amountInLkr },
            pendingLkr = unpaid.sumOf { it.amountInLkr },
            overdueLkr = unpaid.filter { it.effectiveStatus(now) == InvoiceStatus.OVERDUE }.sumOf { it.amountInLkr },
            sourceBreakdown = IncomeSource.entries.map { source ->
                val items = realizedThisMonth.filter { it.source == source }
                SourceTotal(source, items.sumOf { it.amountInLkr }, items.size)
            },
            cryptoNetYtdLkr = cryptoYtd.sumOf { it.amountInLkr },
            cryptoEntriesYtd = cryptoYtd.size,
            totalEpfAccumulatedLkr = totalEpfAccumulated
        )
    }

    fun openPayslipSheet() { isPayslipVisible.value = true }
    fun closePayslipSheet() { isPayslipVisible.value = false }

    fun savePayslipSalary(gross: Double, takeHome: Double, employeeEpf: Double, employerEpf: Double) {
        val userId = authRepository.currentUserId ?: return
        viewModelScope.launch {
            incomeRepository.saveIncomeStream(
                IncomeStream(
                    userId = userId,
                    source = IncomeSource.SALARY,
                    title = "Tech Company Monthly Salary",
                    clientOrPlatform = "Primary Tech Employer",
                    originalAmount = takeHome,
                    currency = Currency.LKR,
                    exchangeRateToLkr = 1.0,
                    amountInLkr = takeHome,
                    status = InvoiceStatus.RECEIVED,
                    dateReceivedOrExpected = System.currentTimeMillis(),
                    isRecurringMonthly = true,
                    notes = "Gross: LKR ${gross.toLong()} · EPF: LKR ${(employeeEpf + employerEpf).toLong()}"
                )
            ).fold(
                onSuccess = {
                    isPayslipVisible.value = false
                    toast.value = UiNotice("Salary recorded", "+${lkr(takeHome)} take-home · ${lkr(employeeEpf + employerEpf)} EPF saved", illustration = incomeSourceIllustration(IncomeSource.SALARY))
                },
                onFailure = { e ->
                    toast.value = UiNotice("Couldn't save salary", e.localizedMessage ?: "Try again", NoticeTone.ERROR)
                }
            )
        }
    }

    fun setFilter(source: IncomeSource?) {
        filter.update { it.copy(source = source) }
    }

    fun toggleForeignCurrencyFilter() {
        filter.update { it.copy(foreignCurrencyOnly = !it.foreignCurrencyOnly) }
    }

    fun markReceived(income: IncomeStream) {
        val userId = authRepository.currentUserId ?: return
        viewModelScope.launch {
            incomeRepository.updateInvoiceStatus(income.id, userId, InvoiceStatus.RECEIVED, System.currentTimeMillis())
                .onSuccess { toast.value = UiNotice("Marked as received", "+${lkr(income.amountInLkr)} · ${income.title}", illustration = incomeSourceIllustration(income.source)) }
                .onFailure { toast.value = UiNotice("Couldn't update", it.localizedMessage ?: "Try again", NoticeTone.ERROR) }
        }
    }

    /** Adds this month's copy of a recurring entry (FR-INC-11). Only ever called from his tap. */
    fun addRecurring(income: IncomeStream) {
        val userId = authRepository.currentUserId ?: return
        val entry = income.asThisMonth(System.currentTimeMillis()).copy(userId = userId)
        viewModelScope.launch {
            incomeRepository.saveIncomeStream(entry)
                .onSuccess { toast.value = UiNotice("Income recorded", "+${lkr(entry.amountInLkr)} · ${entry.title}", illustration = incomeSourceIllustration(entry.source)) }
                .onFailure { toast.value = UiNotice("Couldn't save", it.localizedMessage ?: "Try again", NoticeTone.ERROR) }
        }
    }

    fun dismissRecurring(income: IncomeStream) = dismissedRecurring.update { it + income.id }

    fun deleteIncome(income: IncomeStream) {
        val userId = authRepository.currentUserId ?: return
        viewModelScope.launch {
            incomeRepository.deleteIncomeStream(income.id, userId)
                .onSuccess { toast.value = UiNotice("Income removed", income.title) }
                .onFailure { toast.value = UiNotice("Couldn't delete", it.localizedMessage ?: "Try again", NoticeTone.ERROR) }
        }
    }

    fun dismissNotice() {
        toast.value = null
    }

    // ---------------- Add income sheet ----------------

    init {
        viewModelScope.launch {
            convertCurrencyUseCase.refreshRates()
        }
    }

    fun showAddSheet() {
        val rateInfo = convertCurrencyUseCase.getSuggestedRateInfo(Currency.LKR)
        form.update { IncomeFormState(isVisible = true, rateSourceInfo = rateInfo.sourceDescription) }
    }
    fun showEditSheet(income: IncomeStream) = form.update { IncomeFormState.editing(income) }
    fun hideAddSheet() = form.update { it.copy(isVisible = false, errorMessage = null) }

    fun onTitleChanged(title: String) = form.update { it.copy(titleInput = title) }
    fun onClientChanged(client: String) = form.update { it.copy(clientInput = client) }
    fun onAmountChanged(amount: String) = form.update {
        val parsedAmount = amount.replace(",", "").trim().toDoubleOrNull()
        val parsedCredited = it.receivedLkrInput.replace(",", "").trim().toDoubleOrNull()
        val derivedRate = if (it.isCreditedLkrMode && parsedAmount != null && parsedAmount > 0 && parsedCredited != null) {
            formatRate(parsedCredited / parsedAmount)
        } else it.exchangeRateInput
        it.copy(amountInput = amount, exchangeRateInput = derivedRate, errorMessage = null)
    }
    fun onRateChanged(rate: String) = form.update { it.copy(exchangeRateInput = rate, errorMessage = null) }
    fun onCreditedLkrChanged(creditedLkr: String) = form.update {
        val parsedCredited = creditedLkr.replace(",", "").trim().toDoubleOrNull()
        val parsedAmount = it.amountInput.replace(",", "").trim().toDoubleOrNull()
        val derivedRate = if (parsedAmount != null && parsedAmount > 0 && parsedCredited != null) {
            formatRate(parsedCredited / parsedAmount)
        } else it.exchangeRateInput
        it.copy(receivedLkrInput = creditedLkr, exchangeRateInput = derivedRate, errorMessage = null)
    }
    fun onToggleCreditedLkrMode(useCreditedLkr: Boolean) = form.update {
        it.copy(isCreditedLkrMode = useCreditedLkr)
    }
    fun onDayOffsetChanged(offset: Int) = form.update { it.copy(dayOffset = offset, dateChanged = true) }
    fun onCryptoLossToggled(isLoss: Boolean) = form.update { it.copy(isCryptoLoss = isLoss) }
    fun onRecurringToggled(isRecurring: Boolean) = form.update { it.copy(isRecurringMonthly = isRecurring) }

    fun onStatusChanged(status: InvoiceStatus) = form.update {
        // Received income is dated in the past; an invoice is due in the future.
        if (it.selectedStatus == status) it
        else it.copy(selectedStatus = status, dayOffset = if (status == InvoiceStatus.RECEIVED) 0 else 14, dateChanged = true)
    }

    fun onSourceChanged(source: IncomeSource) {
        // Tapping the source it already has must not swap the stored rate for today's (ADR-0001).
        if (form.value.isEditing && form.value.selectedSource == source) return
        val currency = when (source) {
            IncomeSource.ADSENSE -> Currency.USD
            IncomeSource.CRYPTO -> Currency.USDT
            else -> Currency.LKR
        }
        val rateInfo = convertCurrencyUseCase.getSuggestedRateInfo(currency)
        form.update {
            it.copy(
                selectedSource = source,
                selectedCurrency = currency,
                exchangeRateInput = formatRate(rateInfo.rate),
                rateSourceInfo = "${formatRate(rateInfo.rate)} LKR/${currency.name} · ${rateInfo.sourceDescription}",
                isRecurringMonthly = source == IncomeSource.SALARY,
                isCryptoLoss = false
            )
        }
    }

    fun onCurrencyChanged(currency: Currency) {
        if (form.value.isEditing && form.value.selectedCurrency == currency) return
        val rateInfo = convertCurrencyUseCase.getSuggestedRateInfo(currency)
        form.update {
            it.copy(
                selectedCurrency = currency,
                exchangeRateInput = formatRate(rateInfo.rate),
                rateSourceInfo = "${formatRate(rateInfo.rate)} LKR/${currency.name} · ${rateInfo.sourceDescription}"
            )
        }
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
        val date = current.dateMillis(System.currentTimeMillis())

        // Editing keeps the id (so it replaces the record), the notes and the owner.
        val record = (current.editing ?: IncomeStream(userId = userId, source = current.selectedSource, title = title, clientOrPlatform = client, originalAmount = signedAmount, currency = current.selectedCurrency))
            .copy(
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

        viewModelScope.launch {
            form.update { it.copy(isSubmitting = true) }
            incomeRepository.saveIncomeStream(record).fold(
                onSuccess = {
                    form.value = IncomeFormState()
                    toast.value = UiNotice(
                        if (current.isEditing) "Income updated"
                        else if (current.selectedStatus == InvoiceStatus.RECEIVED) "Income recorded" else "Invoice tracked",
                        "${signedLkr(amountInLkr)} · $title",
                        illustration = incomeSourceIllustration(current.selectedSource)
                    )
                },
                onFailure = { e ->
                    form.update { it.copy(isSubmitting = false, errorMessage = e.localizedMessage ?: "Couldn't save") }
                }
            )
        }
    }

    private fun formatRate(rate: Double): String = formatInputNumber(rate)
}
