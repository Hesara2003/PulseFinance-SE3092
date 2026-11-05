package com.se3092.pulsefinance.ui.common

import com.se3092.pulsefinance.R
import org.junit.Assert.assertEquals
import org.junit.Test

class GoalIllustrationTest {

    @Test
    fun `a gadget goal gets the laptop`() {
        assertEquals(R.drawable.ic_preset_tech, goalIllustration("MacBook Pro M4"))
        assertEquals(R.drawable.ic_preset_tech, goalIllustration("New iPhone"))
    }

    @Test
    fun `a home or vehicle goal gets its own picture`() {
        assertEquals(R.drawable.ic_preset_house, goalIllustration("Apartment deposit"))
        assertEquals(R.drawable.ic_preset_ride, goalIllustration("Scooter"))
    }

    @Test
    fun `any other goal gets the piggy bank`() {
        assertEquals(R.drawable.illustration_piggy_bank, goalIllustration("Trip to Japan"))
        assertEquals(R.drawable.illustration_piggy_bank, goalIllustration(""))
    }

    @Test
    fun `words only match at the start, so a scar is not a car`() {
        assertEquals(R.drawable.illustration_piggy_bank, goalIllustration("Scar treatment"))
    }
}
