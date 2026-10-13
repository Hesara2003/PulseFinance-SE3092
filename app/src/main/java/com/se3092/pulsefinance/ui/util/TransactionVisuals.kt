package com.se3092.pulsefinance.ui.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LaptopMac
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.MovieFilter
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.se3092.pulsefinance.domain.model.ExpenseCategory
import com.se3092.pulsefinance.domain.model.IncomeSource
import com.se3092.pulsefinance.ui.theme.CryptoColor
import com.se3092.pulsefinance.ui.theme.CyanAccent
import com.se3092.pulsefinance.ui.theme.FoodColor
import com.se3092.pulsefinance.ui.theme.NeoAmber
import com.se3092.pulsefinance.ui.theme.NeoGreen
import com.se3092.pulsefinance.ui.theme.NeoGreenDark
import com.se3092.pulsefinance.ui.theme.NeoTextSecondary
import com.se3092.pulsefinance.ui.theme.RentColor
import com.se3092.pulsefinance.ui.theme.TechColor
import com.se3092.pulsefinance.ui.theme.TransportColor
import com.se3092.pulsefinance.ui.theme.VioletAccent

/** Icon + accent colour + short label for one ledger row. */
data class TransactionVisual(
    val icon: ImageVector,
    val tint: Color,
    val label: String
) {
    val container: Color get() = tint.copy(alpha = 0.12f)
}

/** Consistent visuals for income sources and expense categories across every screen. */
object TransactionVisuals {

    fun forCategory(category: ExpenseCategory): TransactionVisual = when (category) {
        ExpenseCategory.RENT -> TransactionVisual(Icons.Default.Home, RentColor, "Rent")
        ExpenseCategory.UTILITIES -> TransactionVisual(Icons.Default.Bolt, CyanAccent, "Bills")
        ExpenseCategory.SUBSCRIPTION -> TransactionVisual(Icons.Default.Autorenew, VioletAccent, "Subscription")
        ExpenseCategory.FOOD_DINING -> TransactionVisual(Icons.Default.Restaurant, FoodColor, "Dining")
        ExpenseCategory.TRANSPORT -> TransactionVisual(Icons.Default.LocalTaxi, TransportColor, "Transport")
        ExpenseCategory.TECH_HARDWARE -> TransactionVisual(Icons.Default.LaptopMac, TechColor, "Tech")
        ExpenseCategory.ENTERTAINMENT -> TransactionVisual(Icons.Default.MovieFilter, NeoAmber, "Leisure")
        ExpenseCategory.OTHER -> TransactionVisual(Icons.Default.MoreHoriz, NeoTextSecondary, "Other")
    }

    fun forIncome(source: IncomeSource): TransactionVisual = when (source) {
        IncomeSource.SALARY -> TransactionVisual(Icons.Default.AccountBalance, NeoGreenDark, "Salary")
        IncomeSource.FREELANCE -> TransactionVisual(Icons.Default.Work, NeoGreen, "Freelance")
        IncomeSource.ADSENSE -> TransactionVisual(Icons.Default.Campaign, TransportColor, "AdSense")
        IncomeSource.CRYPTO -> TransactionVisual(Icons.Default.CurrencyBitcoin, CryptoColor, "Crypto")
    }
}
