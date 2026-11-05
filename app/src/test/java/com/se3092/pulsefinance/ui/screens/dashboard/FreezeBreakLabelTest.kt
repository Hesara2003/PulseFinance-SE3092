package com.se3092.pulsefinance.ui.screens.dashboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** FR-ACC-5: Freeze Breaks appear in the weekly summary only once there is one to show. */
class FreezeBreakLabelTest {

    @Test
    fun `no label until a freeze has been broken`() {
        assertNull(freezeBreakLabel(0))
    }

    @Test
    fun `one break reads in the singular and several in the plural`() {
        assertEquals("1 Freeze Break so far", freezeBreakLabel(1))
        assertEquals("3 Freeze Breaks so far", freezeBreakLabel(3))
    }
}
