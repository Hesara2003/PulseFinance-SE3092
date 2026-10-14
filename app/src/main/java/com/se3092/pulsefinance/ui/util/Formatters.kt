package com.se3092.pulsefinance.ui.util

import com.se3092.pulsefinance.domain.model.Currency
import com.se3092.pulsefinance.domain.model.UserProfile
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs

/** Single source for currency, date and name formatting across all screens. */
object Formatters {

    /** "LKR 34,000" or with decimals "LKR 34,000.00". */
    fun lkr(amount: Double, decimals: Int = 0): String =
        "LKR ${String.format(Locale.US, "%,.${decimals}f", amount)}"

    /** Signed amount for ledgers, e.g. "+ LKR 13,725" / "- LKR 8,500". */
    fun signedLkr(amount: Double, isPositive: Boolean, decimals: Int = 0): String =
        "${if (isPositive) "+" else "-"} ${lkr(abs(amount), decimals)}"

    /** Compact amounts for tight spaces: 950 → "950", 16,500 → "16.5k", 1,250,000 → "1.25M". */
    fun compact(amount: Double): String {
        val value = abs(amount)
        val sign = if (amount < 0) "-" else ""
        return when {
            value >= 1_000_000 -> "$sign${trimZeros(String.format(Locale.US, "%.2f", value / 1_000_000))}M"
            value >= 1_000 -> "$sign${trimZeros(String.format(Locale.US, "%.1f", value / 1_000))}k"
            else -> "$sign${String.format(Locale.US, "%.0f", value)}"
        }
    }

    fun lkrCompact(amount: Double): String = "LKR ${compact(amount)}"

    /** Amount in its original currency, e.g. "$45.00", "USDT 20.00", "LKR 45,000". */
    fun original(amount: Double, currency: Currency): String = when (currency) {
        Currency.LKR -> lkr(amount)
        Currency.USD -> "$${String.format(Locale.US, "%,.2f", amount)}"
        Currency.USDT -> "USDT ${String.format(Locale.US, "%,.2f", amount)}"
        Currency.ETH -> "ETH ${String.format(Locale.US, "%,.4f", amount)}"
    }

    fun percent(ratio: Float): String = "${(ratio * 100).toInt()}%"

    fun percentOneDecimal(ratio: Float): String = String.format(Locale.US, "%.1f%%", ratio * 100)

    fun days(value: Float): String = String.format(Locale.US, "%.2f", value)

    /** "Today 11:24 AM", "Yesterday", "Tomorrow", "Apr 21", or "Apr 21, 2025" for other years. */
    fun relativeDay(millis: Long, nowMillis: Long = System.currentTimeMillis()): String {
        val dayDelta = dayIndex(millis) - dayIndex(nowMillis)
        return when (dayDelta) {
            0L -> "Today ${SimpleDateFormat("h:mm a", Locale.US).format(Date(millis))}"
            -1L -> "Yesterday"
            1L -> "Tomorrow"
            else -> shortDate(millis, nowMillis)
        }
    }

    fun shortDate(millis: Long, nowMillis: Long = System.currentTimeMillis()): String {
        val sameYear = year(millis) == year(nowMillis)
        val pattern = if (sameYear) "MMM d" else "MMM d, yyyy"
        return SimpleDateFormat(pattern, Locale.US).format(Date(millis))
    }

    fun monthYear(millis: Long): String = SimpleDateFormat("MMMM yyyy", Locale.US).format(Date(millis))

    /** "1st", "2nd", "23rd" — for "Auto-debited on the 5th". */
    fun dayOfMonthOrdinal(millis: Long): String {
        val day = Calendar.getInstance().apply { timeInMillis = millis }.get(Calendar.DAY_OF_MONTH)
        val suffix = if (day in 11..13) "th" else when (day % 10) {
            1 -> "st"; 2 -> "nd"; 3 -> "rd"; else -> "th"
        }
        return "$day$suffix"
    }

    fun daysBetween(fromMillis: Long, toMillis: Long): Long = dayIndex(toMillis) - dayIndex(fromMillis)

    /** "Kavindu" from a display name, or a capitalised email local part as fallback. */
    fun firstName(profile: UserProfile?): String {
        val name = profile?.displayName?.trim().orEmpty()
        if (name.isNotBlank()) return name.split(" ").first()
        val local = profile?.email?.substringBefore("@").orEmpty()
        if (local.isBlank()) return "there"
        return local.split('.', '_', '-').first().replaceFirstChar { it.uppercase() }
    }

    fun fullName(profile: UserProfile?): String {
        val name = profile?.displayName?.trim().orEmpty()
        return name.ifBlank { firstName(profile) }
    }

    fun initials(profile: UserProfile?): String {
        val parts = fullName(profile).split(" ").filter { it.isNotBlank() }
        return parts.take(2).joinToString("") { it.first().uppercase() }.ifBlank { "?" }
    }

    /** "Good morning" / "Good afternoon" / "Good evening" by local time. */
    fun greeting(nowMillis: Long = System.currentTimeMillis()): String {
        val hour = Calendar.getInstance().apply { timeInMillis = nowMillis }.get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            else -> "Good evening"
        }
    }

    private fun trimZeros(text: String): String = text.trimEnd('0').trimEnd('.')

    private fun year(millis: Long): Int = Calendar.getInstance().apply { timeInMillis = millis }.get(Calendar.YEAR)

    private fun dayIndex(millis: Long): Long {
        val cal = Calendar.getInstance().apply { timeInMillis = millis }
        val offset = cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET)
        return Math.floorDiv(millis + offset, 24L * 60 * 60 * 1000)
    }
}

fun formatDate(millis: Long, pattern: String = "dd MMM yyyy"): String =
    SimpleDateFormat(pattern, Locale.getDefault()).format(Date(millis))
