package com.se3092.pulsefinance.ui.screens.dashboard

import com.se3092.pulsefinance.domain.model.Account
import com.se3092.pulsefinance.domain.model.AccountType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** FR-ACC-8 and FR-ACC-9: adding, editing and setting the balance of Kavindu's accounts. */
class AccountEditorStateTest {

    @Test
    fun `a new account starts as a primary bank account with the matching name`() {
        val state = AccountEditorState.forNewAccount()

        assertTrue(state.isNew)
        assertEquals("Primary Bank Account", state.name)
        assertNull(state.validationError)
    }

    @Test
    fun `adding his wallet becomes a Binance account with a balance`() {
        val account = AccountEditorState.forNewAccount()
            .withType(AccountType.CRYPTO_EXCHANGE)
            .copy(balanceInput = "83,000")
            .toAccount("uid")!!

        assertEquals("Binance / Crypto Wallet", account.name)
        assertEquals(AccountType.CRYPTO_EXCHANGE, account.type)
        assertEquals(83_000.0, account.balanceLkr, 0.01)
        assertEquals("uid", account.userId)
        assertEquals("", account.id) // the repository assigns the id
    }

    @Test
    fun `a name he chose is kept when the type changes`() {
        val state = AccountEditorState.forNewAccount().copy(name = "Sampath Savings").withType(AccountType.BANK_SECONDARY)

        assertEquals("Sampath Savings", state.name)
        assertTrue(state.isAutoDebitSource)
    }

    @Test
    fun `a blank balance means zero and a negative balance is allowed`() {
        assertEquals(0.0, AccountEditorState(balanceInput = "").balanceLkr!!, 0.0)
        assertEquals(-12_500.0, AccountEditorState(balanceInput = "-12,500").balanceLkr!!, 0.0)
    }

    @Test
    fun `bad input is explained instead of saved`() {
        assertNotNull(AccountEditorState(name = "  ").validationError)
        assertNotNull(AccountEditorState(balanceInput = "lots").validationError)
        assertNotNull(AccountEditorState(numberInput = "1".repeat(35)).validationError)
        assertNull(AccountEditorState(name = "  ").toAccount("uid"))
    }

    @Test
    fun `cash has no account number and cannot be an auto-debit source`() {
        val account = AccountEditorState(
            type = AccountType.CASH,
            name = "Cash",
            numberInput = "12345678",
            isAutoDebitSource = true
        ).toAccount("uid")!!

        assertEquals("", account.accountNumber)
        assertFalse(account.isAutoDebitSource)
    }

    @Test
    fun `editing keeps the freeze and the Freeze Break count he did not touch`() {
        val frozen = Account(
            id = "acc_secondary_bank", userId = "uid", name = "Secondary Account (Auto-Debits)",
            type = AccountType.BANK_SECONDARY, balanceLkr = 0.0, isAutoDebitSource = true,
            isFrozen = true, frozenUntilMillis = 1_800_000_000_000L, freezeBreakCount = 2
        )

        val saved = AccountEditorState.editing(frozen).copy(balanceInput = "42,000").toAccount("uid")!!

        assertEquals("acc_secondary_bank", saved.id)
        assertEquals(42_000.0, saved.balanceLkr, 0.01)
        assertTrue(saved.isFrozen)
        assertEquals(1_800_000_000_000L, saved.frozenUntilMillis)
        assertEquals(2, saved.freezeBreakCount)
    }

    @Test
    fun `editing shows the balance as he would type it`() {
        assertEquals("", AccountEditorState.editing(Account(name = "Cash", type = AccountType.CASH)).balanceInput)
        assertEquals("8000", AccountEditorState.editing(Account(name = "Cash", type = AccountType.CASH, balanceLkr = 8_000.0)).balanceInput)
        assertEquals("1234.50", AccountEditorState.editing(Account(name = "Cash", type = AccountType.CASH, balanceLkr = 1_234.5)).balanceInput)
    }
}
