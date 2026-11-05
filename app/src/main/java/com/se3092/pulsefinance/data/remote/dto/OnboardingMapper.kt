package com.se3092.pulsefinance.data.remote.dto

import com.se3092.pulsefinance.domain.model.ExpenseCategory
import com.se3092.pulsefinance.domain.model.FixedCommitment
import com.se3092.pulsefinance.domain.model.IncomeSource
import com.se3092.pulsefinance.domain.model.IncomeVariability
import com.se3092.pulsefinance.domain.model.MAX_RUNWAY_MONTHS
import com.se3092.pulsefinance.domain.model.MIN_RUNWAY_MONTHS
import com.se3092.pulsefinance.domain.model.OnboardingProfile

/**
 * The `onboarding` map on `users/{uid}`. It is a plain map rather than a DTO class, so reading
 * it is tolerant: a missing or unrecognised field falls back to a sensible value instead of
 * failing, because the map is also written in pieces (the runway is updated on its own).
 */
object OnboardingMapper {

    fun toMap(profile: OnboardingProfile): Map<String, Any?> = buildMap {
        put("monthlySalaryLkr", profile.monthlySalaryLkr)
        put("incomeSources", profile.incomeSources.map { (source, variability) -> source.name to variability.name }.toMap())
        put("fixedCommitments", profile.fixedCommitments.map {
            mapOf("title" to it.title, "amountLkr" to it.amountLkr, "category" to it.category.name)
        })
        put("completedAt", profile.completedAt)
        profile.runwayMonths?.let { put("runwayMonths", it) }
        put("runwaySavedLkr", profile.runwaySavedLkr)
    }

    /** Only the runway fields, for a merge write that leaves the rest of the profile untouched. */
    fun runwayFields(months: Int, savedLkr: Double): Map<String, Any?> =
        mapOf("runwayMonths" to months, "runwaySavedLkr" to savedLkr)

    fun fromMap(raw: Map<*, *>): OnboardingProfile {
        val sources = (raw["incomeSources"] as? Map<*, *>).orEmpty().mapNotNull { (key, value) ->
            val source = runCatching { IncomeSource.valueOf(key as String) }.getOrNull() ?: return@mapNotNull null
            val variability = runCatching { IncomeVariability.valueOf(value as String) }.getOrNull()
                ?: OnboardingProfile.defaultVariability(source)
            source to variability
        }.toMap()

        val commitments = (raw["fixedCommitments"] as? List<*>).orEmpty().mapNotNull { item ->
            val entry = item as? Map<*, *> ?: return@mapNotNull null
            val title = entry["title"] as? String ?: return@mapNotNull null
            val amount = (entry["amountLkr"] as? Number)?.toDouble() ?: return@mapNotNull null
            val category = runCatching { ExpenseCategory.valueOf(entry["category"] as String) }.getOrDefault(ExpenseCategory.OTHER)
            FixedCommitment(title, amount, category)
        }

        return OnboardingProfile(
            monthlySalaryLkr = (raw["monthlySalaryLkr"] as? Number)?.toDouble(),
            incomeSources = sources,
            fixedCommitments = commitments,
            completedAt = (raw["completedAt"] as? Number)?.toLong() ?: 0L,
            runwayMonths = (raw["runwayMonths"] as? Number)?.toInt()?.takeIf { it in MIN_RUNWAY_MONTHS..MAX_RUNWAY_MONTHS },
            runwaySavedLkr = ((raw["runwaySavedLkr"] as? Number)?.toDouble() ?: 0.0).coerceAtLeast(0.0)
        )
    }
}
