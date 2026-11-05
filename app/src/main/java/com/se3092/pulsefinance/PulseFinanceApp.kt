package com.se3092.pulsefinance

import android.app.Application
import com.se3092.pulsefinance.notifications.ReminderSettings
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class PulseFinanceApp : Application() {

    @Inject
    lateinit var reminderSettings: ReminderSettings

    override fun onCreate() {
        super.onCreate()
        // Keeps the daily overdue-invoice check scheduled while he has reminders on (FR-NTF-1).
        reminderSettings.ensureScheduled()
    }
}
