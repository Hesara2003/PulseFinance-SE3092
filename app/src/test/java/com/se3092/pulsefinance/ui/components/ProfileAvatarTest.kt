package com.se3092.pulsefinance.ui.components

import org.junit.Assert.assertEquals
import org.junit.Test

class ProfileAvatarTest {

    @Test
    fun `google photo is requested at the avatar size instead of 96px`() {
        val url = "https://lh3.googleusercontent.com/a/ACg8ocK123=s96-c"
        assertEquals("https://lh3.googleusercontent.com/a/ACg8ocK123=s144-c", googlePhotoAtSize(url, 144))
    }

    @Test
    fun `small avatars never ask for less than 96px`() {
        val url = "https://lh3.googleusercontent.com/a/ACg8ocK123=s96-c"
        assertEquals("https://lh3.googleusercontent.com/a/ACg8ocK123=s96-c", googlePhotoAtSize(url, 60))
    }

    @Test
    fun `google photo without a size suffix gets one`() {
        val url = "https://lh3.googleusercontent.com/a/ACg8ocK123"
        assertEquals("https://lh3.googleusercontent.com/a/ACg8ocK123=s120-c", googlePhotoAtSize(url, 120))
    }

    @Test
    fun `other photo urls are left alone`() {
        val url = "https://example.com/me.png"
        assertEquals(url, googlePhotoAtSize(url, 144))
    }
}
