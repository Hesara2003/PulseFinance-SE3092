package com.se3092.pulsefinance.ui.screens.dashboard

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** FR-AUTH-9: deleting the account takes his confirmation. */
class AccountSecurityTest {

    @Test
    fun `an email account must type its password before it can be deleted`() {
        assertFalse(AccountSecurityState(usesPassword = true, passwordInput = "").canConfirmDelete)
        assertTrue(AccountSecurityState(usesPassword = true, passwordInput = "hunter2").canConfirmDelete)
    }

    @Test
    fun `a Google account confirms by picking the account, not a password`() {
        assertTrue(AccountSecurityState(usesPassword = false, passwordInput = "").canConfirmDelete)
    }

    @Test
    fun `it cannot be confirmed twice while deleting`() {
        assertFalse(AccountSecurityState(usesPassword = false, isDeleting = true).canConfirmDelete)
    }
}
