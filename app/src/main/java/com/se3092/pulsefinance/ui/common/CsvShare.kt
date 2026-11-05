package com.se3092.pulsefinance.ui.common

import android.content.Context
import android.content.Intent

/** Opens the share sheet with the CSV export (FR-DATA-1), so it can go to Drive, email or Sheets. */
fun shareCsv(context: Context, csvContent: String, title: String = "PulseFinance_Export.csv") {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/csv"
        putExtra(Intent.EXTRA_SUBJECT, title)
        putExtra(Intent.EXTRA_TEXT, csvContent)
    }
    context.startActivity(Intent.createChooser(shareIntent, "Export PulseFinance Data (CSV)"))
}
