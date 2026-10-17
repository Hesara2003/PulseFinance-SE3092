package com.se3092.pulsefinance.ui.screens.income

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.se3092.pulsefinance.domain.model.Currency
import com.se3092.pulsefinance.domain.model.IncomeSource
import com.se3092.pulsefinance.domain.model.IncomeStream
import com.se3092.pulsefinance.domain.model.InvoiceStatus
import com.se3092.pulsefinance.domain.repository.AuthRepository
import com.se3092.pulsefinance.domain.repository.IncomeRepository
import com.se3092.pulsefinance.domain.usecase.ConvertCurrencyUseCase
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

data class IncomeScreenUiState(
    val filterCategory: String = "all", // "all", "contracts", "usd", "yield"
    val streams: List<IncomeStream> = emptyList(),
    val toastNotification: Pair<String, String>? = null, // (title, subtitle)
    val totalProjectedLkr: Double = 0.0,
    val totalRealizedLkr: Double = 0.0,
    val totalPendingLkr: Double = 0.0,
    /** Realized foreign-currency inflow (USD/USDT/ETH) in LKR, for the sweep suggestion. */
    val foreignRealizedLkr: Double = 0.0,
    val userInitials: String = "",
    val isLoading: Boolean = true,
    val isAddSheetVisible: Boolean = false,
    val titleInput: String = "",
    val clientInput: String = "",
    val amountInput: String = "",
    val selectedSource: IncomeSource = IncomeSource.FREELANCE,
    val selectedCurrency: Currency = Currency.LKR,
    val exchangeRateInput: String = "1.0",
    val selectedStatus: InvoiceStatus = InvoiceStatus.RECEIVED,
    val isSubmitting: Boolean = false
) {
    val realizedRatio: Float get() = if (totalProjectedLkr > 0) (totalRealizedLkr / totalProjectedLkr).toFloat().coerceIn(0f, 1f) else 0f
    val pendingRatio: Float get() = if (totalProjectedLkr > 0) (totalPendingLkr / totalProjectedLkr).toFloat().coerceIn(0f, 1f) else 0f

    val visibleStreams: List<IncomeStream> get() = streams.filter { stream ->
        when (filterCategory) {
            "contracts" -> stream.source == IncomeSource.SALARY || stream.source == IncomeSource.FREELANCE
            "usd" -> stream.currency == Currency.USD
            "yield" -> stream.source == IncomeSource.CRYPTO
            else -> true
        }
    }
}

@HiltViewModel
class IncomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val incomeRepository: IncomeRepository,
    private val convertCurrencyUseCase: ConvertCurrencyUseCase
) : ViewModel() {

    private val _screenState = MutableStateFlow(IncomeScreenUiState())

    private val incomeFlow = authRepository.userScopedFlow(emptyList()) { incomeRepository.getIncomeStreams(it) }

    val uiState: StateFlow<IncomeScreenUiState> = combine(incomeFlow, _screenState) { incomes, state ->
        // Overdue items first so they get attention, then pending, then realized by date
        val ordered = incomes.sortedWith(
            compareBy<IncomeStream> {
                when (it.status) {
                    InvoiceStatus.OVERDUE -> 0
                    InvoiceStatus.PENDING_INVOICE -> 1
                    InvoiceStatus.RECEIVED -> 2
                }
            }.thenByDescending { it.dateReceivedOrExpected }
        )
        val realized = incomes.filter { it.isRealized }.sumOf { it.amountInLkr }
        val pending = incomes.filterNot { it.isRealized }.sumOf { it.amountInLkr }

        state.copy(
            streams = ordered,
            totalRealizedLkr = realized,
            totalPendingLkr = pending,
            totalProjectedLkr = realized + pending,
            foreignRealizedLkr = incomes
                .filter { it.isRealized && it.currency != Currency.LKR }
                .sumOf { it.amountInLkr },
            userInitials = Formatters.initials(authRepository.currentUser),
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = IncomeScreenUiState()
    )

    fun setFilterCategory(category: String) {
        _screenState.update { it.copy(filterCategory = category) }
    }

    fun markReceived(incomeId: String) {
        val uid = authRepository.currentUserId ?: return
        val stream = uiState.value.streams.find { it.id == incomeId } ?: return
        viewModelScope.launch {
            incomeRepository.updateInvoiceStatus(incomeId, uid, InvoiceStatus.RECEIVED)
            _screenState.update {
                it.copy(
                    toastNotification = Pair(
                        "Milestone marked as realized!",
                        "+${Formatters.lkr(stream.amountInLkr, decimals = 2)} added to vault"
                    )
                )
            }
        }
    }

    fun deleteStream(incomeId: String) {
        val uid = authRepository.currentUserId ?: return
        viewModelScope.launch { incomeRepository.deleteIncomeStream(incomeId, uid) }
    }

    fun dismissToast() {
        _screenState.update { it.copy(toastNotification = null) }
    }

    fun showAddSheet() {
        _screenState.update { it.copy(isAddSheetVisible = true) }
    }

    fun hideAddSheet() {
        _screenState.update {
            it.copy(
                isAddSheetVisible = false,
                titleInput = "",
                clientInput = "",
                amountInput = ""
            )
        }
    }

    fun onTitleChanged(title: String) = _screenState.update { it.copy(titleInput = title) }
    fun onClientChanged(client: String) = _screenState.update { it.copy(clientInput = client) }
    fun onAmountChanged(amount: String) = _screenState.update { it.copy(amountInput = amount) }

    fun onSourceChanged(source: IncomeSource) {
        val currency = when (source) {
            IncomeSource.ADSENSE -> Currency.USD
            IncomeSource.CRYPTO -> Currency.USDT
            else -> Currency.LKR
        }
        val rate = convertCurrencyUseCase.getBenchmarkRate(currency).toString()
        _screenState.update {
            it.copy(
                selectedSource = source,
                selectedCurrency = currency,
                exchangeRateInput = rate
            )
        }
    }

    fun onCurrencyChanged(currency: Currency) {
        val rate = convertCurrencyUseCase.getBenchmarkRate(currency).toString()
        _screenState.update { it.copy(selectedCurrency = currency, exchangeRateInput = rate) }
    }

    fun onRateChanged(rate: String) = _screenState.update { it.copy(exchangeRateInput = rate) }
    fun onStatusChanged(status: InvoiceStatus) = _screenState.update { it.copy(selectedStatus = status) }

    fun submitIncome() {
        val uid = authRepository.currentUserId ?: return
        val state = uiState.value
        val amount = state.amountInput.toDoubleOrNull()?.takeIf { it > 0 } ?: return
        val rate = state.exchangeRateInput.toDoubleOrNull() ?: 1.0
        val amountInLkr = convertCurrencyUseCase.toLkr(amount, state.selectedCurrency, rate)

        val title = state.titleInput.ifBlank { "${state.selectedSource.displayName} Income" }
        val client = state.clientInput.ifBlank { state.selectedSource.displayName }

        viewModelScope.launch {
            _screenState.update { it.copy(isSubmitting = true) }
            incomeRepository.addIncomeStream(
                IncomeStream(
                    userId = uid,
                    source = state.selectedSource,
                    title = title,
                    clientOrPlatform = client,
                    originalAmount = amount,
                    currency = state.selectedCurrency,
                    exchangeRateToLkr = rate,
                    amountInLkr = amountInLkr,
                    status = state.selectedStatus,
                    isRecurringMonthly = state.selectedSource == IncomeSource.SALARY
                )
            )
            hideAddSheet()
            _screenState.update {
                it.copy(
                    isSubmitting = false,
                    toastNotification = Pair("Income Stream Added!", "+${Formatters.lkr(amountInLkr)} recorded")
                )
            }
        }
    }
}
