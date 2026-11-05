package com.se3092.pulsefinance.testing

import com.se3092.pulsefinance.domain.model.Account
import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.model.IncomeStream
import com.se3092.pulsefinance.domain.model.InvoiceStatus
import com.se3092.pulsefinance.domain.model.OnboardingProfile
import com.se3092.pulsefinance.domain.model.SavingsGoal
import com.se3092.pulsefinance.domain.model.SeasonalReserve
import com.se3092.pulsefinance.domain.repository.AccountRepository
import com.se3092.pulsefinance.domain.repository.AuthRepository
import com.se3092.pulsefinance.domain.repository.ExpenseRepository
import com.se3092.pulsefinance.domain.repository.GoalRepository
import com.se3092.pulsefinance.domain.repository.IncomeRepository
import com.se3092.pulsefinance.domain.repository.OnboardingRepository
import com.se3092.pulsefinance.domain.repository.SeasonalReserveRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/** Runs viewModelScope on a test dispatcher, so ViewModels can be driven without an Android main thread. */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(val dispatcher: TestDispatcher = UnconfinedTestDispatcher()) : TestWatcher() {
    override fun starting(description: Description) = Dispatchers.setMain(dispatcher)
    override fun finished(description: Description) = Dispatchers.resetMain()
}

const val TEST_USER = "kavindu-uid"

class FakeAuthRepository(signedIn: Boolean = true) : AuthRepository {
    private val user = MutableStateFlow(if (signedIn) TEST_USER else null)
    override val currentUserId: String? get() = user.value
    override val isUserAuthenticated: Boolean get() = user.value != null
    override val userEmail: String? = "kavindu@example.com"
    override val userDisplayName: String? = "Kavindu Silva"
    override val userPhotoUrl: String? = null
    override fun observeAuthState(): Flow<String?> = user
    override suspend fun signInWithEmail(email: String, password: String) = Result.success(TEST_USER)
    override suspend fun signUpWithEmail(email: String, password: String) = Result.success(TEST_USER)
    override suspend fun signInWithGoogle(idToken: String) = Result.success(TEST_USER)
    override suspend fun signOut() { user.value = null }
}

class FakeIncomeRepository(initial: List<IncomeStream> = emptyList()) : IncomeRepository {
    val items = MutableStateFlow(initial)
    val saved = mutableListOf<IncomeStream>()
    private var nextId = 1

    override fun getIncomeStreams(userId: String): Flow<List<IncomeStream>> = items

    override suspend fun saveIncomeStream(income: IncomeStream): Result<Unit> {
        val stored = if (income.id.isBlank()) income.copy(id = "inc-${nextId++}") else income
        saved += stored
        items.value = items.value.filterNot { it.id == stored.id } + stored
        return Result.success(Unit)
    }

    override suspend fun updateInvoiceStatus(incomeId: String, userId: String, status: InvoiceStatus, dateMillis: Long): Result<Unit> {
        items.value = items.value.map { if (it.id == incomeId) it.copy(status = status, dateReceivedOrExpected = dateMillis) else it }
        return Result.success(Unit)
    }

    override suspend fun deleteIncomeStream(incomeId: String, userId: String): Result<Unit> {
        items.value = items.value.filterNot { it.id == incomeId }
        return Result.success(Unit)
    }
}

class FakeExpenseRepository(initial: List<Expense> = emptyList()) : ExpenseRepository {
    val items = MutableStateFlow(initial)
    val saved = mutableListOf<Expense>()
    private var nextId = 1

    override fun getExpenses(userId: String): Flow<List<Expense>> = items

    override suspend fun saveExpense(expense: Expense): Result<Unit> {
        val stored = if (expense.id.isBlank()) expense.copy(id = "exp-${nextId++}") else expense
        saved += stored
        items.value = items.value.filterNot { it.id == stored.id } + stored
        return Result.success(Unit)
    }

    override suspend fun deleteExpense(expenseId: String, userId: String): Result<Unit> {
        items.value = items.value.filterNot { it.id == expenseId }
        return Result.success(Unit)
    }
}

class FakeAccountRepository(initial: List<Account> = emptyList()) : AccountRepository {
    val items = MutableStateFlow(initial)
    val freezeBreaks = mutableListOf<Account>()

    override fun getAccounts(userId: String): Flow<List<Account>> = items
    override suspend fun saveAccount(account: Account): Result<Unit> {
        val stored = if (account.id.isBlank()) account.copy(id = "acc-${items.value.size + 1}") else account
        items.value = items.value.filterNot { it.id == stored.id } + stored
        return Result.success(Unit)
    }
    override suspend fun deleteAccount(userId: String, accountId: String): Result<Unit> {
        items.value = items.value.filterNot { it.id == accountId }
        return Result.success(Unit)
    }
    override suspend fun ensureDefaultAccounts(userId: String): Result<Boolean> = Result.success(false)
    override suspend fun freezeAccount(account: Account, untilMillis: Long): Result<Unit> {
        items.value = items.value.map { if (it.id == account.id) it.copy(isFrozen = true, frozenUntilMillis = untilMillis) else it }
        return Result.success(Unit)
    }
    override suspend fun unfreezeAccount(account: Account): Result<Unit> {
        items.value = items.value.map { if (it.id == account.id) it.copy(isFrozen = false, frozenUntilMillis = null) else it }
        return Result.success(Unit)
    }
    override suspend fun recordFreezeBreak(account: Account): Result<Unit> {
        freezeBreaks += account
        items.value = items.value.map { if (it.id == account.id) it.copy(freezeBreakCount = it.freezeBreakCount + 1) else it }
        return Result.success(Unit)
    }
}

class FakeGoalRepository(initial: SavingsGoal? = null) : GoalRepository {
    val goal = MutableStateFlow(initial)
    override fun getPrimaryGoal(userId: String): Flow<SavingsGoal?> = goal
    override suspend fun saveGoal(goal: SavingsGoal): Result<Unit> {
        this.goal.value = goal
        return Result.success(Unit)
    }
    override suspend fun updateSavedAmount(goalId: String, userId: String, newAmountLkr: Double): Result<Unit> {
        goal.value = goal.value?.copy(currentSavedLkr = newAmountLkr)
        return Result.success(Unit)
    }
}

class FakeSeasonalReserveRepository : SeasonalReserveRepository {
    private val reserves = MutableStateFlow<Map<String, SeasonalReserve>>(emptyMap())
    override fun getReserves(userId: String): Flow<Map<String, SeasonalReserve>> = reserves
    override fun setAside(userId: String, reserve: SeasonalReserve) { reserves.value = reserves.value + (reserve.peakKey to reserve) }
    override fun release(userId: String, peakKey: String) { reserves.value = reserves.value - peakKey }
}

class FakeOnboardingRepository : OnboardingRepository {
    private val profile = MutableStateFlow<OnboardingProfile?>(null)
    override fun saveProfile(userId: String, profile: OnboardingProfile) { this.profile.value = profile }
    override fun getProfile(userId: String): Flow<OnboardingProfile?> = profile
    override fun saveRunway(userId: String, months: Int, savedLkr: Double) {
        profile.value = profile.value?.copy(runwayMonths = months, runwaySavedLkr = savedLkr)
            ?: OnboardingProfile(null, emptyMap(), emptyList(), 0L, months, savedLkr)
    }
}
