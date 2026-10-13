package com.se3092.pulsefinance.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.LocalTaxi
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.se3092.pulsefinance.domain.model.ExpenseCategory
import com.se3092.pulsefinance.domain.model.IncomeSource
import com.se3092.pulsefinance.ui.theme.NeoCardLow
import com.se3092.pulsefinance.ui.theme.NeoErrorContainer
import com.se3092.pulsefinance.ui.theme.NeoGreen
import com.se3092.pulsefinance.ui.theme.NeoGreenDark
import com.se3092.pulsefinance.ui.theme.NeoRose
import com.se3092.pulsefinance.ui.theme.NeoSecondaryContainer
import com.se3092.pulsefinance.ui.theme.NeoTextSecondary

data class TransactionVisual(val icon: ImageVector, val container: Color, val tint: Color)

fun incomeSourceVisual(source: IncomeSource): TransactionVisual = when (source) {
    IncomeSource.SALARY -> TransactionVisual(Icons.Default.CorporateFare, NeoGreen.copy(alpha = 0.15f), NeoGreenDark)
    IncomeSource.FREELANCE -> TransactionVisual(Icons.Default.Terminal, Color(0xFFFFF4E0), Color(0xFFB45309))
    IncomeSource.ADSENSE -> TransactionVisual(Icons.Default.SmartDisplay, Color(0xFFDAE2FD), Color(0xFF131B2E))
    IncomeSource.CRYPTO -> TransactionVisual(Icons.Default.Token, Color(0xFFEEF2FF), Color(0xFF4F46E5))
}

fun expenseCategoryVisual(category: ExpenseCategory): TransactionVisual = when (category) {
    ExpenseCategory.RENT -> TransactionVisual(Icons.Default.Home, NeoSecondaryContainer, NeoTextSecondary)
    ExpenseCategory.UTILITIES -> TransactionVisual(Icons.Default.Bolt, NeoSecondaryContainer, NeoTextSecondary)
    ExpenseCategory.SUBSCRIPTION -> TransactionVisual(Icons.Default.Subscriptions, NeoErrorContainer, NeoRose)
    ExpenseCategory.FOOD_DINING -> TransactionVisual(Icons.Default.Restaurant, NeoErrorContainer, NeoRose)
    ExpenseCategory.TRANSPORT -> TransactionVisual(Icons.Default.DirectionsCar, NeoGreen.copy(alpha = 0.15f), NeoGreenDark)
    ExpenseCategory.TECH_HARDWARE -> TransactionVisual(Icons.Default.LaptopMac, NeoCardLow, NeoTextSecondary)
    ExpenseCategory.ENTERTAINMENT -> TransactionVisual(Icons.Default.Movie, NeoCardLow, NeoTextSecondary)
    ExpenseCategory.OTHER -> TransactionVisual(Icons.Default.ShoppingBag, NeoCardLow, NeoTextSecondary)
}

fun transactionVisual(item: TransactionItem): TransactionVisual =
    item.incomeSource?.let(::incomeSourceVisual)
        ?: item.expenseCategory?.let(::expenseCategoryVisual)
        ?: TransactionVisual(Icons.Default.Payments, NeoCardLow, NeoTextSecondary)

/**
 * Icon for a one-tap preset: the merchant's own glyph when the title makes it obvious
 * (a cup for coffee, a taxi for PickMe), otherwise the category's icon.
 */
fun presetVisual(title: String, category: ExpenseCategory): TransactionVisual {
    val base = expenseCategoryVisual(category)
    val t = title.lowercase()
    val icon = when {
        listOf("coffee", "latte", "cafe", "café", "tea").any { it in t } -> Icons.Default.LocalCafe
        listOf("ubereats", "uber eats", "pickme food", "delivery").any { it in t } -> Icons.Default.DeliveryDining
        listOf("pickme", "uber", "taxi", "tuk", "ride").any { it in t } -> Icons.Default.LocalTaxi
        listOf("grocer", "keells", "cargills", "supermarket").any { it in t } -> Icons.Default.LocalGroceryStore
        listOf("gym", "fitness").any { it in t } -> Icons.Default.FitnessCenter
        else -> base.icon
    }
    return base.copy(icon = icon)
}
