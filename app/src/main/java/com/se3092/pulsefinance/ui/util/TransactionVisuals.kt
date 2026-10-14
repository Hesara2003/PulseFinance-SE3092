package com.se3092.pulsefinance.ui.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LaptopMac
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.Movie
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
import com.se3092.pulsefinance.ui.theme.FoodColor
import com.se3092.pulsefinance.ui.theme.NeoCardLow
import com.se3092.pulsefinance.ui.theme.NeoSecondaryContainer
import com.se3092.pulsefinance.ui.theme.NeoTextSecondary
import com.se3092.pulsefinance.ui.theme.RentColor
import com.se3092.pulsefinance.ui.theme.Teal
import com.se3092.pulsefinance.ui.theme.TransportColor

/** Icon, container fill and tint for one ledger row. */
data class TransactionVisual(
    val icon: ImageVector,
    val container: Color,
    val tint: Color
)

/** Consistent visuals for income sources and expense categories across every screen. */
object TransactionVisuals {

    fun forIncome(source: IncomeSource): TransactionVisual = when (source) {
        IncomeSource.SALARY -> TransactionVisual(Icons.Default.CorporateFare, Teal.copy(alpha = 0.16f), Teal)
        IncomeSource.FREELANCE -> TransactionVisual(Icons.Default.Terminal, Color(0xFFFFF4E0), Color(0xFFB45309))
        IncomeSource.ADSENSE -> TransactionVisual(Icons.Default.SmartDisplay, TransportColor.copy(alpha = 0.12f), Color(0xFF1D4ED8))
        IncomeSource.CRYPTO -> TransactionVisual(Icons.Default.Token, Color(0xFFEEF2FF), Color(0xFF4F46E5))
    }

    fun forCategory(category: ExpenseCategory): TransactionVisual = when (category) {
        ExpenseCategory.RENT -> TransactionVisual(Icons.Default.Home, NeoSecondaryContainer, NeoTextSecondary)
        ExpenseCategory.UTILITIES -> TransactionVisual(Icons.Default.Bolt, NeoSecondaryContainer, NeoTextSecondary)
        ExpenseCategory.SUBSCRIPTION -> TransactionVisual(Icons.Default.Subscriptions, RentColor.copy(alpha = 0.16f), RentColor)
        ExpenseCategory.FOOD_DINING -> TransactionVisual(Icons.Default.Restaurant, FoodColor.copy(alpha = 0.16f), FoodColor)
        ExpenseCategory.TRANSPORT -> TransactionVisual(Icons.Default.DirectionsCar, TransportColor.copy(alpha = 0.16f), TransportColor)
        ExpenseCategory.TECH_HARDWARE -> TransactionVisual(Icons.Default.LaptopMac, NeoCardLow, NeoTextSecondary)
        ExpenseCategory.ENTERTAINMENT -> TransactionVisual(Icons.Default.Movie, NeoCardLow, NeoTextSecondary)
        ExpenseCategory.OTHER -> TransactionVisual(Icons.Default.ShoppingBag, NeoCardLow, NeoTextSecondary)
    }

    /**
     * Icon for a one-tap preset: the merchant's own glyph when the title makes it obvious
     * (a cup for coffee, a taxi for PickMe), otherwise the category's icon.
     */
    fun forPreset(title: String, category: ExpenseCategory): TransactionVisual {
        val base = forCategory(category)
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
}
