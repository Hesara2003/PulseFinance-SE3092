package com.se3092.pulsefinance.ui.common

import com.se3092.pulsefinance.domain.model.DAY_MILLIS
import com.se3092.pulsefinance.domain.model.Periods
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs

fun lkr(amount: Double, decimals: Int = 0): String = "LKR ${"%,.${decimals}f".format(Locale.US, amount)}"

/** "+ LKR 1,200" / "- LKR 850" for signed cash-flow amounts. */
fun signedLkr(amount: Double): String = "${if (amount < 0) "-" else "+"} ${lkr(abs(amount))}"

/** Compact amount for small badges: 850, 1.4k, 34k, 1.2M. */
fun compactAmount(amount: Double): String = when {
    abs(amount) >= 1_000_000 -> "%.1fM".format(Locale.US, amount / 1_000_000)
    abs(amount) >= 10_000 -> "%.0fk".format(Locale.US, amount / 1_000)
    abs(amount) >= 1_000 -> "%.1fk".format(Locale.US, amount / 1_000)
    else -> "%.0f".format(Locale.US, amount)
}

fun formatDate(millis: Long, pattern: String = "dd MMM yyyy"): String =
    SimpleDateFormat(pattern, Locale.getDefault()).format(Date(millis))

/** "Today 11:24", "Yesterday", "21 Apr" — how a ledger reads naturally. */
fun relativeDayLabel(millis: Long, now: Long = System.currentTimeMillis()): String {
    val today = Periods.startOfDay(now)
    return when {
        millis >= today -> "Today ${formatDate(millis, "HH:mm")}"
        millis >= today - DAY_MILLIS -> "Yesterday"
        else -> formatDate(millis, "dd MMM")
    }
}

fun greetingFor(now: Long = System.currentTimeMillis()): String =
    when (Calendar.getInstance().apply { timeInMillis = now }.get(Calendar.HOUR_OF_DAY)) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        else -> "Good evening"
    }

/** First name from the Google display name, otherwise the part of the email before "@". */
fun displayNameFor(displayName: String?, email: String?): String {
    val fromProfile = displayName?.trim()?.split(" ")?.firstOrNull()?.takeIf { it.isNotBlank() }
    if (fromProfile != null) return fromProfile
    val local = email?.substringBefore("@")?.split('.', '_', '-')?.firstOrNull()?.takeIf { it.isNotBlank() }
    return local?.replaceFirstChar { it.uppercase() } ?: "there"
}

fun initialsFor(displayName: String?, email: String?): String {
    val words = displayName?.trim()?.split(" ")?.filter { it.isNotBlank() }.orEmpty()
    if (words.isNotEmpty()) return words.take(2).joinToString("") { it.first().uppercase() }
    return email?.firstOrNull()?.uppercase() ?: "?"
}
