package com.se3092.pulsefinance.domain.model

/**
 * Represents a monthly Sri Lankan employment payslip.
 * Implements FR-LK-1 (Gross salary, employee EPF 8%, APIT progressive tax, Take-Home net salary)
 * and FR-LK-3 (Accumulating EPF Balance: employee 8% + employer 12% = 20% of gross).
 */
data class Payslip(
    val id: String = "",
    val userId: String = "",
    val monthMillis: Long = System.currentTimeMillis(),
    val grossSalaryLkr: Double = 0.0,
    val employeeEpfLkr: Double = grossSalaryLkr * 0.08,
    val apitTaxLkr: Double = 0.0,
    val otherDeductionsLkr: Double = 0.0,
    val takeHomeSalaryLkr: Double = (grossSalaryLkr - employeeEpfLkr - apitTaxLkr - otherDeductionsLkr).coerceAtLeast(0.0),
    val employerEpfLkr: Double = grossSalaryLkr * 0.12,
    val employerEtfLkr: Double = grossSalaryLkr * 0.03
) {
    /** Total monthly EPF wealth building (8% employee deduction + 12% employer contribution). */
    val monthlyTotalEpfLkr: Double get() = employeeEpfLkr + employerEpfLkr

    /** Total social security & retirement additions (EPF 20% + ETF 3%). */
    val monthlyTotalRetirementLkr: Double get() = monthlyTotalEpfLkr + employerEtfLkr
}

/**
 * Progressive tax band under the Sri Lankan Inland Revenue Act (APIT).
 * Implements FR-LK-2 (Data-driven bands from Firestore/config, not hard-coded).
 */
data class ApitTaxBand(
    val lowerLimitLkr: Double,
    val upperLimitLkr: Double,
    val taxRate: Double
) {
    companion object {
        /**
         * Monthly APIT bands bundled with the app. Only used until the Firestore config
         * document (`config/apitBands`) has been read at least once.
         */
        val DEFAULTS = listOf(
            ApitTaxBand(0.0, 100_000.0, 0.00),         // First 100k tax-free
            ApitTaxBand(100_000.0, 141_667.0, 0.06),   // Next ~41.6k @ 6%
            ApitTaxBand(141_667.0, 183_333.0, 0.12),   // Next ~41.6k @ 12%
            ApitTaxBand(183_333.0, 225_000.0, 0.18),   // Next ~41.6k @ 18%
            ApitTaxBand(225_000.0, 266_667.0, 0.24),   // Next ~41.6k @ 24%
            ApitTaxBand(266_667.0, 308_333.0, 0.30),   // Next ~41.6k @ 30%
            ApitTaxBand(308_333.0, Double.MAX_VALUE, 0.36) // Balance @ 36%
        )

        /**
         * True when [bands] can be used for tax: non-empty, starting at 0, contiguous,
         * each band non-empty and every rate between 0 and 1.
         */
        fun isValidSchedule(bands: List<ApitTaxBand>): Boolean {
            if (bands.isEmpty() || bands.first().lowerLimitLkr != 0.0) return false
            if (bands.any { it.upperLimitLkr <= it.lowerLimitLkr || it.taxRate !in 0.0..1.0 }) return false
            return bands.zipWithNext().all { (a, b) -> a.upperLimitLkr == b.lowerLimitLkr }
        }
    }
}
