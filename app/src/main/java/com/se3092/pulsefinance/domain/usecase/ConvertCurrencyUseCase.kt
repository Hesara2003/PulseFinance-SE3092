package com.se3092.pulsefinance.domain.usecase

import com.se3092.pulsefinance.domain.model.Currency
import javax.inject.Inject

class ConvertCurrencyUseCase @Inject constructor() {

    // Default reference exchange rates in Sri Lanka (LKR)
    val defaultUsdRate = 305.00
    val defaultUsdtRate = 306.50
    val defaultEthRate = 820000.00

    fun toLkr(amount: Double, currency: Currency, customRate: Double? = null): Double {
        val rate = customRate ?: when (currency) {
            Currency.LKR -> 1.0
            Currency.USD -> defaultUsdRate
            Currency.USDT -> defaultUsdtRate
            Currency.ETH -> defaultEthRate
        }
        return amount * rate
    }

    fun getBenchmarkRate(currency: Currency): Double {
        return when (currency) {
            Currency.LKR -> 1.0
            Currency.USD -> defaultUsdRate
            Currency.USDT -> defaultUsdtRate
            Currency.ETH -> defaultEthRate
        }
    }
}
