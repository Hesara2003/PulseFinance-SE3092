package com.se3092.pulsefinance.data.local

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Process
import android.provider.Settings

object UsageStatsHelper {

    private val SUBSCRIPTION_PACKAGES = mapOf(
        "netflix" to "com.netflix.mediaclient",
        "spotify" to "com.spotify.music",
        "youtube" to "com.google.android.youtube",
        "prime" to "com.amazon.avod.thirdpartyclient",
        "disney" to "com.disney.disneyplus",
        "canva" to "com.canva.editor"
    )

    /** True when the subscription maps to an Android app whose usage we could read. */
    fun isAppLinked(subscriptionKey: String): Boolean = packageFor(subscriptionKey) != null

    private fun packageFor(subscriptionKey: String): String? =
        SUBSCRIPTION_PACKAGES.entries.firstOrNull { (k, _) ->
            subscriptionKey.contains(k, ignoreCase = true)
        }?.value

    fun hasUsagePermission(context: Context): Boolean {
        return try {
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager ?: return false
            val mode = appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
            mode == AppOpsManager.MODE_ALLOWED
        } catch (e: Exception) {
            false
        }
    }

    fun openUsageSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    /**
     * Returns days since the subscription's linked Android app was last opened,
     * or null if untracked, uninstalled, or permission is missing.
     */
    fun getLastOpenedDaysAgo(context: Context, subscriptionKey: String, now: Long = System.currentTimeMillis()): Int? {
        if (!hasUsagePermission(context)) return null
        val matchedPackage = packageFor(subscriptionKey) ?: return null

        return try {
            val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager ?: return null
            val lookbackMillis = 90L * 24 * 60 * 60 * 1000
            val stats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_BEST,
                now - lookbackMillis,
                now
            )
            val matchedStat = stats.filter { it.packageName == matchedPackage }.maxByOrNull { it.lastTimeUsed }
            val lastUsed = matchedStat?.lastTimeUsed ?: 0L
            if (lastUsed > 0L) {
                val diffDays = ((now - lastUsed) / (1000 * 60 * 60 * 24)).toInt()
                diffDays.coerceAtLeast(0)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
