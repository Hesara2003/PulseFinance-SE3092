package com.se3092.pulsefinance.domain.usecase

import com.se3092.pulsefinance.domain.model.ApitTaxBand
import com.se3092.pulsefinance.domain.model.Payslip
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.min

@Singleton
class CalculatePayslipUseCase @Inject constructor() {

    /**
     * Calculates monthly APIT progressive tax on the given Gross Salary.
     * The tax-free threshold is whatever the first band says, so a revised schedule
     * in Firestore changes it without an app update (FR-LK-2).
     */
    fun calculateApit(grossSalaryLkr: Double, bands: List<ApitTaxBand> = ApitTaxBand.DEFAULTS): Double {
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
        bands: List<ApitTaxBand> = ApitTaxBand.DEFAULTS
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
