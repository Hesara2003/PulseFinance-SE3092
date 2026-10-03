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
    val isMilestoneRealized: Boolean = false,
    val toastNotification: Pair<String, String>? = null, // (title, subtitle)
    val totalProjectedLkr: Double = 412600.0,
    val totalRealizedLkr: Double = 347600.0,
    val totalPendingLkr: Double = 65000.0,
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
    val realizedRatio: Float get() = if (totalProjectedLkr > 0) (totalRealizedLkr / totalProjectedLkr).toFloat().coerceIn(0f, 1f) else 0.842f
    val pendingRatio: Float get() = if (totalProjectedLkr > 0) (totalPendingLkr / totalProjectedLkr).toFloat().coerceIn(0f, 1f) else 0.158f
}

@HiltViewModel
class IncomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val incomeRepository: IncomeRepository,
    private val convertCurrencyUseCase: ConvertCurrencyUseCase
) : ViewModel() {

    private val userId: String
        get() = authRepository.currentUserId ?: "guest_kavindu"

    private val _screenState = MutableStateFlow(IncomeScreenUiState())

    val uiState: StateFlow<IncomeScreenUiState> = combine(
        incomeRepository.getIncomeStreams(userId),
        _screenState
    ) { incomes, state ->
        state
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = IncomeScreenUiState()
    )

    fun setFilterCategory(category: String) {
        _screenState.update { it.copy(filterCategory = category) }
    }

    fun markMilestoneReceived() {
        _screenState.update {
            it.copy(
                isMilestoneRealized = true,
                totalRealizedLkr = 412600.0,
                totalPendingLkr = 0.0,
                toastNotification = Pair(
                    "Milestone marked as realized!",
                    "+LKR 65,000.00 added to vault"
                )
            )
        }
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
        val state = uiState.value
        val amount = state.amountInput.toDoubleOrNull() ?: return
        val rate = state.exchangeRateInput.toDoubleOrNull() ?: 1.0
        val amountInLkr = convertCurrencyUseCase.toLkr(amount, state.selectedCurrency, rate)

        val title = if (state.titleInput.isNotBlank()) state.titleInput else "${state.selectedSource.displayName} Income"
        val client = if (state.clientInput.isNotBlank()) state.clientInput else state.selectedSource.displayName

        viewModelScope.launch {
            _screenState.update { it.copy(isSubmitting = true) }
            val newIncome = IncomeStream(
                userId = userId,
                source = state.selectedSource,
                title = title,
                clientOrPlatform = client,
                originalAmount = amount,
                currency = state.selectedCurrency,
                exchangeRateToLkr = rate,
                amountInLkr = amountInLkr,
                status = state.selectedStatus
            )
            incomeRepository.addIncomeStream(newIncome)
            hideAddSheet()
            _screenState.update {
                it.copy(
                    isSubmitting = false,
                    toastNotification = Pair("Income Stream Added!", "+LKR ${String.format("%,.0f", amountInLkr)} recorded")
                )
            }
        }
    }
}
