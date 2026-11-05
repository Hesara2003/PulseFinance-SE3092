package com.se3092.pulsefinance.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LaptopMac
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Token
import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.se3092.pulsefinance.R
import com.se3092.pulsefinance.domain.model.ExpenseCategory
import com.se3092.pulsefinance.domain.model.IncomeSource
import com.se3092.pulsefinance.ui.theme.FoodColor
import com.se3092.pulsefinance.ui.theme.NeoCardLow
import com.se3092.pulsefinance.ui.theme.RentColor
import com.se3092.pulsefinance.ui.theme.Teal
import com.se3092.pulsefinance.ui.theme.TransportColor
import com.se3092.pulsefinance.ui.theme.NeoSecondaryContainer
import com.se3092.pulsefinance.ui.theme.NeoTextSecondary

data class TransactionVisual(val icon: ImageVector, val container: Color, val tint: Color)

fun incomeSourceVisual(source: IncomeSource): TransactionVisual = when (source) {
    IncomeSource.SALARY -> TransactionVisual(Icons.Default.CorporateFare, Teal.copy(alpha = 0.16f), Teal)
    IncomeSource.FREELANCE -> TransactionVisual(Icons.Default.Terminal, Color(0xFFFFF4E0), Color(0xFFB45309))
    IncomeSource.ADSENSE -> TransactionVisual(Icons.Default.SmartDisplay, TransportColor.copy(alpha = 0.12f), Color(0xFF1D4ED8))
    IncomeSource.CRYPTO -> TransactionVisual(Icons.Default.Token, Color(0xFFEEF2FF), Color(0xFF4F46E5))
}

fun expenseCategoryVisual(category: ExpenseCategory): TransactionVisual = when (category) {
    ExpenseCategory.RENT -> TransactionVisual(Icons.Default.Home, NeoSecondaryContainer, NeoTextSecondary)
    ExpenseCategory.UTILITIES -> TransactionVisual(Icons.Default.Bolt, NeoSecondaryContainer, NeoTextSecondary)
    ExpenseCategory.SUBSCRIPTION -> TransactionVisual(Icons.Default.Subscriptions, RentColor.copy(alpha = 0.16f), RentColor)
    ExpenseCategory.FOOD_DINING -> TransactionVisual(Icons.Default.Restaurant, FoodColor.copy(alpha = 0.16f), FoodColor)
    ExpenseCategory.TRANSPORT -> TransactionVisual(Icons.Default.DirectionsCar, TransportColor.copy(alpha = 0.16f), TransportColor)
    ExpenseCategory.TECH_HARDWARE -> TransactionVisual(Icons.Default.LaptopMac, NeoCardLow, NeoTextSecondary)
    ExpenseCategory.ENTERTAINMENT -> TransactionVisual(Icons.Default.Movie, NeoCardLow, NeoTextSecondary)
    ExpenseCategory.OTHER -> TransactionVisual(Icons.Default.ShoppingBag, NeoCardLow, NeoTextSecondary)
}

fun transactionVisual(item: TransactionItem): TransactionVisual =
    item.incomeSource?.let(::incomeSourceVisual)
        ?: item.expenseCategory?.let(::expenseCategoryVisual)
        ?: TransactionVisual(Icons.Default.Payments, NeoCardLow, NeoTextSecondary)

/**
 * Illustrated icon for a one-tap preset, drawn in the style of the Storyset illustrations:
 * the merchant's own picture when the title makes it obvious (a cup for coffee, a taxi for
 * PickMe), otherwise the category's.
 */
@DrawableRes
fun presetIllustration(title: String, category: ExpenseCategory): Int {
    val t = title.lowercase()
    fun has(vararg words: String) = words.any { it in t }
    return when {
        has("coffee", "latte", "cafe", "café", "tea") -> R.drawable.ic_preset_coffee
        has("ubereats", "uber eats", "pickme food", "delivery") -> R.drawable.ic_preset_delivery
        has("pickme", "uber", "taxi", "tuk", "ride", "bus", "train") -> R.drawable.ic_preset_ride
        has("grocer", "keells", "cargills", "supermarket", "arpico") -> R.drawable.ic_preset_groceries
        has("gym", "fitness") -> R.drawable.ic_preset_gym
        has("broadband", "fiber", "fibre", "wifi", "internet", "slt") -> R.drawable.ic_preset_wifi
        has("dialog", "mobitel", "airtel", "hutch", "reload", "data", "phone", "mobile") -> R.drawable.ic_preset_phone
        has("vps", "cloud", "digitalocean", "aws", "github", "claude", "chatgpt", "openai", "figma", "notion") ->
            R.drawable.ic_preset_tech
        has("netflix", "spotify", "youtube", "prime", "disney") -> R.drawable.ic_preset_subscription
        has("rent", "boarding", "annex") -> R.drawable.ic_preset_house
        else -> when (category) {
            ExpenseCategory.RENT -> R.drawable.ic_preset_house
            ExpenseCategory.UTILITIES -> R.drawable.ic_preset_bills
            ExpenseCategory.SUBSCRIPTION -> R.drawable.ic_preset_subscription
            ExpenseCategory.FOOD_DINING -> R.drawable.ic_preset_food
            ExpenseCategory.TRANSPORT -> R.drawable.ic_preset_ride
            ExpenseCategory.TECH_HARDWARE -> R.drawable.ic_preset_tech
            ExpenseCategory.ENTERTAINMENT -> R.drawable.ic_preset_fun
            ExpenseCategory.OTHER -> R.drawable.ic_preset_shopping
        }
    }
}

/** Illustrated icon for an income source, in the same style as [presetIllustration]. */
@DrawableRes
fun incomeSourceIllustration(source: IncomeSource): Int = when (source) {
    IncomeSource.SALARY -> R.drawable.ic_preset_salary
    IncomeSource.FREELANCE -> R.drawable.ic_preset_tech
    IncomeSource.ADSENSE -> R.drawable.ic_preset_adsense
    IncomeSource.CRYPTO -> R.drawable.ic_preset_crypto
}

/** Illustrated icon for a ledger row: the income source's, or the expense's as for presets. */
@DrawableRes
fun transactionIllustration(item: TransactionItem): Int =
    item.incomeSource?.let(::incomeSourceIllustration)
        ?: presetIllustration(item.title, item.expenseCategory ?: ExpenseCategory.OTHER)
