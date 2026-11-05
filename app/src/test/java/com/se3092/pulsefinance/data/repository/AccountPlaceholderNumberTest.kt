package com.se3092.pulsefinance.data.repository

import com.se3092.pulsefinance.data.repository.AccountRepositoryImpl.Companion.withoutPlaceholderNumber
import com.se3092.pulsefinance.domain.model.Account
import com.se3092.pulsefinance.domain.model.AccountType
import org.junit.Assert.assertEquals
import org.junit.Test

class AccountPlaceholderNumberTest {

    private fun account(number: String) =
        Account(id = "a", userId = "u", name = "Primary Bank Account", type = AccountType.BANK_PRIMARY, accountNumber = number)

    @Test
    fun `made-up numbers from earlier builds are hidden`() {
        assertEquals("", account("8001452178").withoutPlaceholderNumber().accountNumber)
        assertEquals("", account("1002938472").withoutPlaceholderNumber().accountNumber)
    }

    @Test
    fun `a real account number is kept`() {
        assertEquals("1234567890", account("1234567890").withoutPlaceholderNumber().accountNumber)
    }
}
