package com.se3092.pulsefinance.notifications

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.se3092.pulsefinance.MainActivity
import com.se3092.pulsefinance.R
import com.se3092.pulsefinance.domain.model.Periods
import com.se3092.pulsefinance.domain.repository.AuthRepository
import com.se3092.pulsefinance.domain.repository.IncomeRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ReminderEntryPoint {
    fun authRepository(): AuthRepository
    fun incomeRepository(): IncomeRepository
    fun reminderSettings(): ReminderSettings
}

/**
 * Once a day, tells Kavindu if an invoice has gone overdue (FR-NTF-1). It reads the records cached on
 * the phone, sends nothing when signed out, when the permission is off, or when nothing is overdue,
 * and never more than one notification a day.
 */
class InvoiceReminderWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    @SuppressLint("MissingPermission") // checked through ReminderSettings.isPermissionGranted() below
    override suspend fun doWork(): Result {
        val entry = EntryPointAccessors.fromApplication(context, ReminderEntryPoint::class.java)
        val settings = entry.reminderSettings()
        val userId = entry.authRepository().currentUserId ?: return Result.success()
        if (!settings.enabled.value || !settings.isPermissionGranted()) return Result.success()

        val now = System.currentTimeMillis()
        if (!shouldNotifyToday(settings.lastNotifiedDayStart(), now)) return Result.success()

        // The cache answers at once; if the listener never produces a first list, try again tomorrow.
        val incomes = withTimeoutOrNull(FIRST_LIST_TIMEOUT_MILLIS) { entry.incomeRepository().getIncomeStreams(userId).first() }
            ?: return Result.retry()
        val reminder = overdueReminder(incomes, now) ?: return Result.success()

        ensureChannel(context)
        val open = PendingIntent.getActivity(
            context, 0, Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_invoice)
            .setContentTitle(reminder.title)
            .setContentText(reminder.text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(reminder.text))
            .setContentIntent(open)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
        settings.markNotified(Periods.startOfDay(now))
        return Result.success()
    }

    private fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(CHANNEL_ID, "Invoice reminders", NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = "A nudge when an invoice you're waiting on goes overdue"
        }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private companion object {
        const val CHANNEL_ID = "invoice_reminders"
        const val NOTIFICATION_ID = 4101
        const val FIRST_LIST_TIMEOUT_MILLIS = 15_000L
    }
}
