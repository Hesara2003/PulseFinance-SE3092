package com.se3092.pulsefinance.ui.screens.setup

import com.se3092.pulsefinance.domain.model.ExpenseCategory
import com.se3092.pulsefinance.domain.model.IncomeSource
import com.se3092.pulsefinance.domain.model.IncomeVariability
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** FR-AUTH-4 / FR-AUTH-5: what first-run setup captures. */
class SetupUiStateTest {

    private val now = 1700000000000L

    @Test
    fun `captures salary, sources with variability and complete commitments only`() {
        val state = SetupUiState(
            salaryInput = "185,000",
            sources = mapOf(
                IncomeSource.SALARY to IncomeVariability.LOW,
                IncomeSource.ADSENSE to IncomeVariability.MODERATE,
                IncomeSource.CRYPTO to IncomeVariability.VOLATILE
            ),
            commitments = listOf(
                CommitmentDraft(0, "Rent", "34,000", ExpenseCategory.RENT),
                CommitmentDraft(1, "Gym membership", "6500", ExpenseCategory.SUBSCRIPTION),
                CommitmentDraft(2, "Half-typed", "", ExpenseCategory.UTILITIES)
            )
        )

        val profile = state.toProfile(now)

        assertEquals(185_000.0, profile.monthlySalaryLkr!!, 0.01)
        assertEquals(IncomeVariability.VOLATILE, profile.incomeSources[IncomeSource.CRYPTO])
        assertEquals(listOf("Rent", "Gym membership"), profile.fixedCommitments.map { it.title })
        assertEquals(40_500.0, state.commitmentsTotalLkr, 0.01)
    }

    @Test
    fun `a salary always counts as an active income source`() {
        val profile = SetupUiState(salaryInput = "150000", sources = mapOf(IncomeSource.FREELANCE to IncomeVariability.MODERATE))
            .toProfile(now)
        assertEquals(IncomeVariability.LOW, profile.incomeSources[IncomeSource.SALARY])
    }

    @Test
    fun `blank salary is allowed but text is not`() {
        assertNull(SetupUiState(salaryInput = "").stepError)
        assertNull(SetupUiState(salaryInput = "").toProfile(now).monthlySalaryLkr)
        assertNotNull(SetupUiState(salaryInput = "a lot").stepError)
    }

    @Test
    fun `sources step needs at least one source`() {
        assertNotNull(SetupUiState(step = 1, sources = emptyMap()).stepError)
    }

    @Test
    fun `commitments become committed expenses on the primary account`() {
        val expenses = SetupUiState(commitments = listOf(CommitmentDraft(0, "Rent", "34000", ExpenseCategory.RENT)))
            .toProfile(now)
            .commitmentExpenses("uid", now)

        val rent = expenses.single()
        assertTrue(rent.isCommitted)
        assertEquals(ExpenseCategory.RENT, rent.category)
        assertEquals(34_000.0, rent.amountLkr, 0.01)
        assertEquals("acc_primary_bank", rent.paymentAccountId)
        assertEquals(now, rent.timestamp)
    }

    // ---- FR-AUTH-6: emergency buffer ----

    private val rentAndBroadband = listOf(
        CommitmentDraft(0, "Rent", "34,000", ExpenseCategory.RENT),
        CommitmentDraft(1, "Fiber Broadband", "4800", ExpenseCategory.UTILITIES)
    )

    @Test
    fun `the buffer starts at three months and is saved with the profile`() {
        val state = SetupUiState(commitments = rentAndBroadband)

        assertEquals(3, state.runwayMonths)
        assertEquals(116_400.0, state.runwayTargetLkr, 0.01)
        assertEquals(3, state.toProfile(now).runwayMonths)
        assertEquals(0.0, state.toProfile(now).runwaySavedLkr, 0.0)
    }

    @Test
    fun `a chosen number of months and an amount already set aside are captured`() {
        val state = SetupUiState(commitments = rentAndBroadband, runwayMonths = 6, runwaySavedInput = "11,200")

        val profile = state.toProfile(now)

        assertEquals(6, profile.runwayMonths)
        assertEquals(11_200.0, profile.runwaySavedLkr, 0.01)
        assertEquals(232_800.0, state.runwayTargetLkr, 0.01)
    }

    @Test
    fun `the amount set aside is optional but must be a number on the last step`() {
        assertNull(SetupUiState(step = 2, runwaySavedInput = "").stepError)
        assertNull(SetupUiState(step = 2, runwaySavedInput = "5,000").stepError)
        assertNotNull(SetupUiState(step = 2, runwaySavedInput = "some").stepError)
        assertNotNull(SetupUiState(step = 2, runwaySavedInput = "-100").stepError)
    }
}
