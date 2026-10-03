package com.se3092.pulsefinance.domain.model

enum class IncomeSource(val displayName: String) {
    SALARY("Primary Salary"),
    FREELANCE("Freelance Projects"),
    ADSENSE("Google AdSense"),
    CRYPTO("Crypto Trading")
}

enum class InvoiceStatus(val displayName: String) {
    RECEIVED("Received"),
    PENDING_INVOICE("Invoiced (Pending)"),
    OVERDUE("Overdue")
}

enum class ExpenseCategory(val displayName: String, val isTypicallyCommitted: Boolean) {
    RENT("Rent", true),
    UTILITIES("Utilities & Bills", true),
    SUBSCRIPTION("Subscriptions / Auto-Debits", true),
    FOOD_DINING("Food & Casual Dining", false),
    TRANSPORT("Transport & PickMe", false),
    TECH_HARDWARE("Tech & Equipment", false),
    ENTERTAINMENT("Entertainment & Leisure", false),
    OTHER("Other Discretionary", false)
}

enum class AccountType(val displayName: String) {
    BANK_PRIMARY("Primary Bank Account"),
    BANK_SECONDARY("Secondary Account (Auto-Debits)"),
    CASH("Cash in Hand"),
    CRYPTO_EXCHANGE("Binance / Crypto Wallet")
}

enum class Currency(val symbol: String) {
    LKR("LKR"),
    USD("$"),
    USDT("USDT"),
    ETH("ETH")
}
