package com.se3092.pulsefinance.domain.usecase

import com.se3092.pulsefinance.data.remote.rates.ExchangeRateService
import com.se3092.pulsefinance.domain.model.Currency
import javax.inject.Inject

class ConvertCurrencyUseCase @Inject constructor(
    private val exchangeRateService: ExchangeRateService
) {

    fun toLkr(amount: Double, currency: Currency, customRate: Double? = null): Double {
        val rate = customRate ?: getBenchmarkRate(currency)
        return amount * rate
    }

    fun getBenchmarkRate(currency: Currency): Double {
        return exchangeRateService.getSuggestedRate(currency).rate
    }

    fun getSuggestedRateInfo(currency: Currency) = exchangeRateService.getSuggestedRate(currency)

    suspend fun refreshRates() = exchangeRateService.refreshRatesIfNeeded()
}

