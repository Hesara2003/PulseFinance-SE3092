package com.se3092.pulsefinance.domain.usecase

import com.se3092.pulsefinance.domain.model.ApitTaxBand
import com.se3092.pulsefinance.domain.model.Payslip
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max
import kotlin.math.min

@Singleton
class CalculatePayslipUseCase @Inject constructor() {

    /** Default Inland Revenue APIT Monthly Tax Bands (Sri Lanka). */
    val defaultTaxBands = listOf(
        ApitTaxBand(0.0, 100_000.0, 0.00),         // First 100k tax-free
        ApitTaxBand(100_000.0, 141_667.0, 0.06),   // Next ~41.6k @ 6%
        ApitTaxBand(141_667.0, 183_333.0, 0.12),   // Next ~41.6k @ 12%
        ApitTaxBand(183_333.0, 225_000.0, 0.18),   // Next ~41.6k @ 18%
        ApitTaxBand(225_000.0, 266_667.0, 0.24),   // Next ~41.6k @ 24%
        ApitTaxBand(266_667.0, 308_333.0, 0.30),   // Next ~41.6k @ 30%
        ApitTaxBand(308_333.0, Double.MAX_VALUE, 0.36) // Balance @ 36%
    )

    /**
     * Calculates monthly APIT progressive tax on the given Gross Salary.
     */
    fun calculateApit(grossSalaryLkr: Double, bands: List<ApitTaxBand> = defaultTaxBands): Double {
        if (grossSalaryLkr <= 100_000.0) return 0.0
        var totalTax = 0.0
        for (band in bands) {
            if (grossSalaryLkr > band.lowerLimitLkr) {
                val taxableAmountInBand = min(grossSalaryLkr, band.upperLimitLkr) - band.lowerLimitLkr
                if (taxableAmountInBand > 0) {
                    totalTax += taxableAmountInBand * band.taxRate
                }
            }
        }
        return totalTax
    }

    /**
     * Builds a comprehensive Payslip breakdown from Gross Salary and optional custom overrides.
     */
    fun calculate(
        grossSalaryLkr: Double,
        customEmployeeEpf: Double? = null,
        customApitTax: Double? = null,
        otherDeductions: Double = 0.0,
        bands: List<ApitTaxBand> = defaultTaxBands
    ): Payslip {
        val employeeEpf = customEmployeeEpf ?: (grossSalaryLkr * 0.08)
        val apit = customApitTax ?: calculateApit(grossSalaryLkr, bands)
        val employerEpf = grossSalaryLkr * 0.12
        val employerEtf = grossSalaryLkr * 0.03
        val takeHome = (grossSalaryLkr - employeeEpf - apit - otherDeductions).coerceAtLeast(0.0)

        return Payslip(
            grossSalaryLkr = grossSalaryLkr,
            employeeEpfLkr = employeeEpf,
            apitTaxLkr = apit,
            otherDeductionsLkr = otherDeductions,
            takeHomeSalaryLkr = takeHome,
            employerEpfLkr = employerEpf,
            employerEtfLkr = employerEtf
        )
    }
}
