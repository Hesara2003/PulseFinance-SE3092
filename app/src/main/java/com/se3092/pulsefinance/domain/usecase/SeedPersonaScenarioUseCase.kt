package com.se3092.pulsefinance.domain.usecase

import android.util.Log
import com.se3092.pulsefinance.domain.model.Currency
import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.model.ExpenseCategory
import com.se3092.pulsefinance.domain.model.IncomeSource
import com.se3092.pulsefinance.domain.model.IncomeStream
import com.se3092.pulsefinance.domain.model.InvoiceStatus
import com.se3092.pulsefinance.domain.model.SavingsGoal
import com.se3092.pulsefinance.domain.repository.AccountRepository
import com.se3092.pulsefinance.domain.repository.AuthRepository
import com.se3092.pulsefinance.domain.repository.ExpenseRepository
import com.se3092.pulsefinance.domain.repository.GoalRepository
import com.se3092.pulsefinance.domain.repository.IncomeRepository
import java.util.Calendar
import javax.inject.Inject

/**
 * Seeds Kavindu Silva's baseline scenario exactly once per demo account.
 *
 * Previously the dashboard re-seeded on every open, which reset the goal's saved amount and
 * resurrected deleted expenses. The "seeded" flag now lives on the Firestore user document.
 * Regular users only get a default empty account.
 */
class SeedPersonaScenarioUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val accountRepository: AccountRepository,
    private val expenseRepository: ExpenseRepository,
    private val incomeRepository: IncomeRepository,
    private val goalRepository: GoalRepository
) {

    suspend operator fun invoke(userId: String, email: String?) {
        if (!email.equals(DEMO_PERSONA_EMAIL, ignoreCase = true)) {
            accountRepository.ensureDefaultAccount(userId)
            return
        }

        val alreadySeeded = try {
            authRepository.hasSeededScenario(userId)
        } catch (e: Exception) {
            // Can't confirm (offline) — skip rather than risk overwriting real progress.
            Log.w(TAG, "Could not read seed flag; skipping persona seeding", e)
            true
        }
        if (alreadySeeded) return

        seed(userId)
        authRepository.markScenarioSeeded(userId)
    }

    private suspend fun seed(userId: String) {
        val now = System.currentTimeMillis()
        val monthStart = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        // Keeps "this month" seed items inside the current month even on the 1st or 2nd.
        fun daysAgoThisMonth(days: Int): Long = maxOf(monthStart + HOUR_MILLIS, now - days * DAY_MILLIS)

        accountRepository.initializeDefaultAccountsForKavindu(userId)

        goalRepository.saveGoal(
            SavingsGoal(
                id = "macbook_pro_m4_primary",
                userId = userId,
                title = "MacBook Pro M4",
                targetAmountLkr = 490000.0,
                currentSavedLkr = 11200.0
            )
        )

        val oneOffExpenses = listOf(
            Expense(
                id = "exp_rent_initial", userId = userId, title = "Apartment Rent",
                amountLkr = 34000.0, category = ExpenseCategory.RENT, isCommitted = true,
                paymentAccountId = "acc_primary_bank", paymentAccountName = "Commercial Bank",
                timestamp = daysAgoThisMonth(9)
            ),
            Expense(
                id = "exp_dining_initial", userId = userId, title = "Coffee & Casual Dining (Month)",
                amountLkr = 24000.0, category = ExpenseCategory.FOOD_DINING, isCommitted = false,
                paymentAccountId = "acc_primary_bank", paymentAccountName = "Commercial Bank",
                timestamp = daysAgoThisMonth(2)
            ),
            Expense(
                id = "exp_pickme_initial", userId = userId, title = "PickMe Rides",
                amountLkr = 8500.0, category = ExpenseCategory.TRANSPORT, isCommitted = false,
                paymentAccountId = "acc_primary_bank", paymentAccountName = "Commercial Bank",
                timestamp = daysAgoThisMonth(1)
            )
        )
        oneOffExpenses.forEach { expenseRepository.addExpense(it) }

        // Three months of recurring debits so DetectSubscriptionsUseCase has a real cadence to find.
        data class Recurring(
            val slug: String, val title: String, val amount: Double,
            val category: ExpenseCategory, val accountId: String, val accountName: String
        )
        listOf(
            Recurring("gym", "Fitness First Gym", 8500.0, ExpenseCategory.SUBSCRIPTION,
                "acc_secondary_bank", "Secondary Bank"),
            Recurring("vps", "Cloud VPS DigitalOcean", 3200.0, ExpenseCategory.SUBSCRIPTION,
                "acc_secondary_bank", "Secondary Bank"),
            Recurring("ai", "Claude Pro", 6100.0, ExpenseCategory.SUBSCRIPTION,
                "acc_primary_bank", "Commercial Bank"),
            Recurring("fiber", "Dialog Fiber Broadband", 4800.0, ExpenseCategory.UTILITIES,
                "acc_primary_bank", "Commercial Bank")
        ).forEachIndexed { index, sub ->
            repeat(3) { monthsAgo ->
                expenseRepository.addExpense(
                    Expense(
                        id = "exp_sub_${sub.slug}_$monthsAgo",
                        userId = userId,
                        title = sub.title,
                        amountLkr = sub.amount,
                        category = sub.category,
                        isCommitted = true,
                        paymentAccountId = sub.accountId,
                        paymentAccountName = sub.accountName,
                        timestamp = if (monthsAgo == 0) {
                            daysAgoThisMonth(index)
                        } else {
                            now - (monthsAgo * 30L + index) * DAY_MILLIS - HOUR_MILLIS
                        }
                    )
                )
            }
        }

        listOf(
            IncomeStream(
                id = "inc_salary_initial", userId = userId, source = IncomeSource.SALARY,
                title = "Monthly Software Engineer Salary", clientOrPlatform = "Startup Colombo (Base + Bonus)",
                originalAmount = 130000.0, currency = Currency.LKR, status = InvoiceStatus.RECEIVED,
                isRecurringMonthly = true, dateReceivedOrExpected = daysAgoThisMonth(10)
            ),
            IncomeStream(
                id = "inc_freelance_overdue", userId = userId, source = IncomeSource.FREELANCE,
                title = "React & WP Build Milestone 2", clientOrPlatform = "Local SME Retail",
                originalAmount = 45000.0, currency = Currency.LKR, status = InvoiceStatus.OVERDUE,
                dateReceivedOrExpected = daysAgoThisMonth(6)
            ),
            IncomeStream(
                id = "inc_adsense_initial", userId = userId, source = IncomeSource.ADSENSE,
                title = "Tech Blog AdSense Revenue", clientOrPlatform = "Google AdSense",
                originalAmount = 45.0, currency = Currency.USD, exchangeRateToLkr = 305.0,
                amountInLkr = 13725.0, status = InvoiceStatus.RECEIVED,
                dateReceivedOrExpected = daysAgoThisMonth(3)
            ),
            IncomeStream(
                id = "inc_crypto_yield", userId = userId, source = IncomeSource.CRYPTO,
                title = "USDT Flexible Staking Yield", clientOrPlatform = "Binance Earn",
                originalAmount = 20.0, currency = Currency.USDT, exchangeRateToLkr = 306.5,
                amountInLkr = 6130.0, status = InvoiceStatus.PENDING_INVOICE,
                dateReceivedOrExpected = now + 4 * DAY_MILLIS
            )
        ).forEach { incomeRepository.addIncomeStream(it) }
    }

    companion object {
        const val DEMO_PERSONA_EMAIL = "kavindu.silva@pulsemail.io"
        private const val TAG = "SeedPersonaScenario"
        private const val HOUR_MILLIS = 60L * 60 * 1000
        private const val DAY_MILLIS = 24 * HOUR_MILLIS
    }
}
