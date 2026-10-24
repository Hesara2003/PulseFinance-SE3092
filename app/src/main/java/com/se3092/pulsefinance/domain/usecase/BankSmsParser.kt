package com.se3092.pulsefinance.domain.usecase

import com.se3092.pulsefinance.domain.model.ExpenseCategory
import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Singleton

data class ParsedBankSms(
    val amountLkr: Double,
    val merchant: String,
    val cardLastFour: String?,
    val bankName: String = "",
    val suggestedCategory: ExpenseCategory,
    val isCommitted: Boolean
)

@Singleton
class BankSmsParser @Inject constructor() {

    // Regex for LKR amounts: "LKR 2,400.00", "LKR2400", "Rs. 1,450", "Rs 850.00"
    private val amountPattern = Pattern.compile(
        "(?:LKR|RS\\.?)\\s*([0-9]{1,3}(?:,[0-9]{3})+(?:\\.[0-9]{1,2})?|[0-9]+(?:\\.[0-9]{1,2})?)",
        Pattern.CASE_INSENSITIVE
    )

    // Regex for card last four digits: "ending 4521", "card ending with 4521", "ending in 4521"
    private val cardPattern = Pattern.compile(
        "(?:ending|ending with|ending in|card|a/c)\\s*[:#]?\\s*([0-9]{4})",
        Pattern.CASE_INSENSITIVE
    )

    // Regex for merchant: "at UBER EATS", "at PICKME on", "to JAVA LOUNGE"
    private val merchantPattern = Pattern.compile(
        "(?:at|to)\\s+([A-Za-z0-9&'._\\- ]+?)(?:\\s+(?:on|ref|dated|avl|bal|val|time|with|using|via|card)|\\.|,|$)",
        Pattern.CASE_INSENSITIVE
    )

    fun parse(smsBody: String, sender: String = ""): ParsedBankSms? {
        val trimmed = smsBody.trim()
        if (trimmed.isBlank()) return null
        val fullText = "$sender $trimmed"

        // 1. Amount
        val amountMatcher = amountPattern.matcher(trimmed)
        if (!amountMatcher.find()) return null
        val amountStr = amountMatcher.group(1)?.replace(",", "") ?: return null
        val amount = amountStr.toDoubleOrNull() ?: return null

        // 2. Card last-four digits
        val cardMatcher = cardPattern.matcher(trimmed)
        val cardLastFour = if (cardMatcher.find()) cardMatcher.group(1) else null

        // 3. Merchant
        val merchantMatcher = merchantPattern.matcher(trimmed)
        val rawMerchant = if (merchantMatcher.find()) {
            merchantMatcher.group(1)?.trim()?.replace(Regex("\\s+"), " ") ?: "Bank Debit"
        } else {
            "Card Purchase"
        }

        val bankName = when {
            fullText.contains("combank", ignoreCase = true) || fullText.contains("commercial", ignoreCase = true) -> "Commercial Bank"
            fullText.contains("sampath", ignoreCase = true) -> "Sampath Bank"
            fullText.contains("hnb", ignoreCase = true) || fullText.contains("hatton", ignoreCase = true) -> "Hatton National Bank"
            fullText.contains("ntb", ignoreCase = true) || fullText.contains("nations", ignoreCase = true) -> "Nations Trust"
            else -> ""
        }

        // 4. Categorization
        val (category, isCommitted) = categorizeMerchant(rawMerchant)

        return ParsedBankSms(
            amountLkr = amount,
            merchant = rawMerchant,
            cardLastFour = cardLastFour,
            bankName = bankName,
            suggestedCategory = category,
            isCommitted = isCommitted
        )
    }

    private fun categorizeMerchant(merchant: String): Pair<ExpenseCategory, Boolean> {
        val lower = merchant.lowercase()
        return when {
            lower.contains("uber") || lower.contains("cafe") || lower.contains("coffee") ||
            lower.contains("food") || lower.contains("restaurant") || lower.contains("barista") ||
            lower.contains("java lounge") || lower.contains("kfc") || lower.contains("mcdonald") ->
                ExpenseCategory.FOOD_DINING to false

            lower.contains("pickme") || lower.contains("taxi") || lower.contains("transport") ||
            lower.contains("fuel") || lower.contains("petrol") || lower.contains("shed") ->
                ExpenseCategory.TRANSPORT to false

            lower.contains("rent") || lower.contains("apartment") || lower.contains("housing") ->
                ExpenseCategory.RENT to true

            lower.contains("dialog") || lower.contains("mobitel") || lower.contains("slt") ||
            lower.contains("fiber") || lower.contains("ceb") || lower.contains("water") ||
            lower.contains("electricity") || lower.contains("broadband") ->
                ExpenseCategory.UTILITIES to true

            lower.contains("gym") || lower.contains("fitness") || lower.contains("netflix") ||
            lower.contains("spotify") || lower.contains("prime") || lower.contains("youtube") ->
                ExpenseCategory.SUBSCRIPTION to false

            lower.contains("apple") || lower.contains("google") || lower.contains("aws") ||
            lower.contains("github") || lower.contains("digitalocean") || lower.contains("jetbrains") ->
                ExpenseCategory.SUBSCRIPTION to false

            else -> ExpenseCategory.OTHER to false
        }
    }
}
