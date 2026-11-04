package com.se3092.pulsefinance.data.remote.rates

import android.content.Context
import com.se3092.pulsefinance.domain.model.Currency
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

data class SuggestedRateInfo(
    val rate: Double,
    val fetchedAtMillis: Long,
    val isLiveOrCached: Boolean,
    val sourceDescription: String
)

@Singleton
class ExchangeRateService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("pulse_exchange_rates", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USD_LKR = "rate_usd_lkr"
        private const val KEY_USDT_USD = "rate_usdt_usd"
        private const val KEY_ETH_USD = "rate_eth_usd"
        private const val KEY_FETCHED_AT = "rates_fetched_at"
        private const val ONE_DAY_MILLIS = 24 * 60 * 60 * 1000L

        // Robust Sri Lankan market fallback benchmarks
        const val FALLBACK_USD_LKR = 305.00
        const val FALLBACK_USDT_LKR = 306.50
        const val FALLBACK_ETH_LKR = 820000.00
    }

    /**
     * Pre-fetches daily exchange rates if older than 24 hours.
     * Complies with Constraint C-17 (calls free external APIs at most once per day).
     */
    suspend fun refreshRatesIfNeeded(): Boolean = withContext(Dispatchers.IO) {
        val lastFetched = prefs.getLong(KEY_FETCHED_AT, 0L)
        val now = System.currentTimeMillis()
        if (now - lastFetched < ONE_DAY_MILLIS && prefs.contains(KEY_USD_LKR)) {
            return@withContext false
        }

        try {
            // 1. Fetch open.er-api.com for USD -> LKR
            val urlUsd = URL("https://open.er-api.com/v6/latest/USD")
            val connUsd = (urlUsd.openConnection() as HttpURLConnection).apply {
                connectTimeout = 4000
                readTimeout = 4000
                requestMethod = "GET"
            }
            if (connUsd.responseCode == 200) {
                val response = connUsd.inputStream.bufferedReader().use(BufferedReader::readText)
                val json = JSONObject(response)
                val rates = json.optJSONObject("rates")
                val usdLkr = rates?.optDouble("LKR", FALLBACK_USD_LKR) ?: FALLBACK_USD_LKR

                prefs.edit()
                    .putFloat(KEY_USD_LKR, usdLkr.toFloat())
                    .putLong(KEY_FETCHED_AT, now)
                    .apply()
            }
            connUsd.disconnect()

            // 2. Fetch CoinGecko for crypto (ETH and USDT vs USD)
            try {
                val urlCrypto = URL("https://api.coingecko.com/api/v3/simple/price?ids=ethereum,tether&vs_currencies=usd")
                val connCrypto = (urlCrypto.openConnection() as HttpURLConnection).apply {
                    connectTimeout = 4000
                    readTimeout = 4000
                    requestMethod = "GET"
                }
                if (connCrypto.responseCode == 200) {
                    val response = connCrypto.inputStream.bufferedReader().use(BufferedReader::readText)
                    val json = JSONObject(response)
                    val ethUsd = json.optJSONObject("ethereum")?.optDouble("usd", 2600.0) ?: 2600.0
                    val usdtUsd = json.optJSONObject("tether")?.optDouble("usd", 1.0) ?: 1.0
                    prefs.edit()
                        .putFloat(KEY_ETH_USD, ethUsd.toFloat())
                        .putFloat(KEY_USDT_USD, usdtUsd.toFloat())
                        .apply()
                }
                connCrypto.disconnect()
            } catch (_: Exception) {
                // Secondary API failure safely handled
            }
            true
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Returns the suggested rate for a given currency along with metadata (source & age).
     */
    fun getSuggestedRate(currency: Currency): SuggestedRateInfo {
        val lastFetched = prefs.getLong(KEY_FETCHED_AT, 0L)
        val hasCached = lastFetched > 0
        val ageHours = if (hasCached) ((System.currentTimeMillis() - lastFetched) / (1000 * 60 * 60)).coerceAtLeast(0) else 0

        val ageStr = when {
            !hasCached -> "Offline benchmark"
            ageHours < 1 -> "Updated just now"
            ageHours == 1L -> "Updated 1 hour ago"
            ageHours < 24 -> "Updated ${ageHours}h ago"
            else -> "Cached (${ageHours / 24}d ago)"
        }

        val usdLkr = prefs.getFloat(KEY_USD_LKR, FALLBACK_USD_LKR.toFloat()).toDouble()
        val ethUsd = prefs.getFloat(KEY_ETH_USD, 2600.0f).toDouble()
        val usdtUsd = prefs.getFloat(KEY_USDT_USD, 1.0f).toDouble()

        val rate = when (currency) {
            Currency.LKR -> 1.0
            Currency.USD -> usdLkr
            Currency.USDT -> usdLkr * usdtUsd
            Currency.ETH -> usdLkr * ethUsd
        }

        return SuggestedRateInfo(
            rate = rate,
            fetchedAtMillis = lastFetched,
            isLiveOrCached = hasCached,
            sourceDescription = ageStr
        )
    }
}
