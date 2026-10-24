package com.se3092.pulsefinance.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Test

class CalculatePayslipUseCaseTest {

    private val useCase = CalculatePayslipUseCase()

    @Test
    fun `calculates zero APIT tax for gross salary below 100,000 threshold`() {
        val gross = 85_000.0
        val apit = useCase.calculateApit(gross)
        assertEquals(0.0, apit, 0.01)

        val payslip = useCase.calculate(gross)
        assertEquals(85_000.0 * 0.08, payslip.employeeEpfLkr, 0.01) // 6,800
        assertEquals(0.0, payslip.apitTaxLkr, 0.01)
        assertEquals(85_000.0 - 6_800.0, payslip.takeHomeSalaryLkr, 0.01) // 78,200
        assertEquals(85_000.0 * 0.12, payslip.employerEpfLkr, 0.01) // 10,200
        assertEquals(85_000.0 * 0.03, payslip.employerEtfLkr, 0.01) // 2,550
    }

    @Test
    fun `calculates APIT progressive tax for 150,000 gross salary`() {
        val gross = 150_000.0
        // Band 1: 0 - 100,000 @ 0% -> 0
        // Band 2: 100,000 - 141,667 @ 6% -> 41,667 * 0.06 = 2,500.02
        // Band 3: 141,667 - 150,000 @ 12% -> 8,333 * 0.12 = 999.96
        // Total APIT: 2,500.02 + 999.96 = 3,499.98 ~ 3,500
        val apit = useCase.calculateApit(gross)
        assertEquals(3500.0, apit, 10.0)

        val payslip = useCase.calculate(gross)
        val epfEmployee = gross * 0.08 // 12,000
        assertEquals(12_000.0, payslip.employeeEpfLkr, 0.01)
        assertEquals(gross - epfEmployee - apit, payslip.takeHomeSalaryLkr, 10.0)
    }

    @Test
    fun `supports custom overrides for freelance or special tax agreements`() {
        val payslip = useCase.calculate(
            grossSalaryLkr = 200_000.0,
            customEmployeeEpf = 0.0,
            customApitTax = 5_000.0,
            otherDeductions = 10_000.0
        )

        assertEquals(0.0, payslip.employeeEpfLkr, 0.01)
        assertEquals(5_000.0, payslip.apitTaxLkr, 0.01)
        assertEquals(10_000.0, payslip.otherDeductionsLkr, 0.01)
        assertEquals(185_000.0, payslip.takeHomeSalaryLkr, 0.01)
    }
}
