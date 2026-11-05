package com.se3092.pulsefinance.notifications

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/** Backs the Income screen's "remind me when an invoice goes overdue" prompt (FR-NTF-1). */
@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val settings: ReminderSettings
) : ViewModel() {
    val enabled: StateFlow<Boolean> = settings.enabled
    val declined: StateFlow<Boolean> = settings.declined

    fun isPermissionGranted(): Boolean = settings.isPermissionGranted()
    fun enable() = settings.enable()
    fun decline() = settings.decline()
}
