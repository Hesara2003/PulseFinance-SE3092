package com.se3092.pulsefinance.data.remote.dto

import com.se3092.pulsefinance.domain.model.ExpenseCategory
import com.se3092.pulsefinance.domain.model.FixedCommitment
import com.se3092.pulsefinance.domain.model.IncomeSource
import com.se3092.pulsefinance.domain.model.IncomeVariability
import com.se3092.pulsefinance.domain.model.OnboardingProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** The `onboarding` map on Kavindu's user document (FR-AUTH-4, FR-AUTH-5, FR-AUTH-6). */
class OnboardingMapperTest {

    private val kavindu = OnboardingProfile(
        monthlySalaryLkr = 126_400.0,
        incomeSources = mapOf(
            IncomeSource.SALARY to IncomeVariability.LOW,
            IncomeSource.ADSENSE to IncomeVariability.MODERATE,
            IncomeSource.CRYPTO to IncomeVariability.VOLATILE
        ),
        fixedCommitments = listOf(
            FixedCommitment("Rent", 34_000.0, ExpenseCategory.RENT),
            FixedCommitment("Fiber Broadband", 4_800.0, ExpenseCategory.UTILITIES)
        ),
        completedAt = 1_730_750_000_000L,
        runwayMonths = 3,
        runwaySavedLkr = 11_200.0
    )

    @Test
    fun `a profile survives being written and read back, runway included`() {
        assertEquals(kavindu, OnboardingMapper.fromMap(OnboardingMapper.toMap(kavindu)))
    }

    @Test
    fun `a profile without a chosen buffer writes no runway months`() {
        val map = OnboardingMapper.toMap(kavindu.copy(runwayMonths = null))

        assertFalse("runwayMonths" in map)
        assertNull(OnboardingMapper.fromMap(map).runwayMonths)
    }

    @Test
    fun `an account set up before the buffer existed reads as no buffer chosen`() {
        val old = mapOf(
            "monthlySalaryLkr" to 126_400.0,
            "incomeSources" to mapOf("SALARY" to "LOW"),
            "fixedCommitments" to listOf(mapOf("title" to "Rent", "amountLkr" to 34_000.0, "category" to "RENT")),
            "completedAt" to 1_730_750_000_000L
        )

        val profile = OnboardingMapper.fromMap(old)

        assertNull(profile.runwayMonths)
        assertEquals(0.0, profile.runwaySavedLkr, 0.0)
        assertEquals(34_000.0, profile.fixedCommitments.single().amountLkr, 0.0)
    }

    @Test
    fun `setting only the buffer produces a map that holds just the runway fields`() {
        val fields = OnboardingMapper.runwayFields(months = 4, savedLkr = 25_000.0)

        assertEquals(mapOf("runwayMonths" to 4, "runwaySavedLkr" to 25_000.0), fields)

        // Read back on a user who skipped setup: only the buffer exists, and that's enough.
        val profile = OnboardingMapper.fromMap(fields)
        assertEquals(4, profile.runwayMonths)
        assertEquals(25_000.0, profile.runwaySavedLkr, 0.0)
        assertTrue(profile.fixedCommitments.isEmpty())
    }

    @Test
    fun `values outside one to six months or below zero are not trusted`() {
        val profile = OnboardingMapper.fromMap(mapOf("runwayMonths" to 9L, "runwaySavedLkr" to -500.0))

        assertNull(profile.runwayMonths)
        assertEquals(0.0, profile.runwaySavedLkr, 0.0)
    }

    @Test
    fun `unknown sources and half-written commitments are skipped instead of failing`() {
        val profile = OnboardingMapper.fromMap(
            mapOf(
                "incomeSources" to mapOf("SALARY" to "LOW", "LOTTERY" to "LOW", "CRYPTO" to "SOMETIMES"),
                "fixedCommitments" to listOf(
                    mapOf("title" to "Rent", "amountLkr" to 34_000L, "category" to "RENT"),
                    mapOf("title" to "No amount", "category" to "UTILITIES"),
                    mapOf("title" to "Odd category", "amountLkr" to 1_000.0, "category" to "SPACE_TRAVEL")
                )
            )
        )

        assertEquals(setOf(IncomeSource.SALARY, IncomeSource.CRYPTO), profile.incomeSources.keys)
        // An unreadable variability falls back to the sensible default for that source.
        assertEquals(IncomeVariability.VOLATILE, profile.incomeSources[IncomeSource.CRYPTO])
        assertEquals(listOf("Rent", "Odd category"), profile.fixedCommitments.map { it.title })
        assertEquals(ExpenseCategory.OTHER, profile.fixedCommitments.last().category)
    }
}
