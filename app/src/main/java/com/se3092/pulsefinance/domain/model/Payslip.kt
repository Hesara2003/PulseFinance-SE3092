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
)
