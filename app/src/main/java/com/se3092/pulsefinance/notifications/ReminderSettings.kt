package com.se3092.pulsefinance.notifications

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/** Whether he has asked for overdue-invoice reminders (FR-NTF-1), and the daily check that sends them. */
@Singleton
class ReminderSettings @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private val _enabled = MutableStateFlow(prefs.getBoolean(KEY_ENABLED, false))
    val enabled: StateFlow<Boolean> = _enabled.asStateFlow()

    private val _declined = MutableStateFlow(prefs.getBoolean(KEY_DECLINED, false))
    /** He said no thanks to the prompt; it isn't shown again. */
    val declined: StateFlow<Boolean> = _declined.asStateFlow()

    /** Android 13+ needs the notification permission granted at runtime; earlier versions don't. */
    fun isPermissionGranted(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

    fun enable() {
        prefs.edit().putBoolean(KEY_ENABLED, true).putBoolean(KEY_DECLINED, false).apply()
        _enabled.value = true
        _declined.value = false
        schedule()
    }

    fun decline() {
        prefs.edit().putBoolean(KEY_DECLINED, true).apply()
        _declined.value = true
    }

    fun disable() {
        prefs.edit().putBoolean(KEY_ENABLED, false).apply()
        _enabled.value = false
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

    /** Called at app start: keeps the daily check scheduled for as long as reminders are on. */
    fun ensureScheduled() {
        if (_enabled.value) schedule()
    }

    fun lastNotifiedDayStart(): Long = prefs.getLong(KEY_LAST_NOTIFIED, 0L)

    fun markNotified(dayStart: Long) {
        prefs.edit().putLong(KEY_LAST_NOTIFIED, dayStart).apply()
    }

    private fun schedule() {
        val request = PeriodicWorkRequestBuilder<InvoiceReminderWorker>(1, TimeUnit.DAYS)
            .setConstraints(Constraints.NONE)
            .build()
        // KEEP: don't restart the 24-hour clock every time the app opens.
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request)
    }

    private companion object {
        const val PREFS = "pulse_reminders"
        const val KEY_ENABLED = "overdue_reminders_enabled"
        const val KEY_DECLINED = "overdue_reminders_declined"
        const val KEY_LAST_NOTIFIED = "last_notified_day"
        const val WORK_NAME = "overdue_invoice_reminder"
    }
}
