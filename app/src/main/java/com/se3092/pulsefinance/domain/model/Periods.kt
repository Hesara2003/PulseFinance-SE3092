package com.se3092.pulsefinance.domain.model

import java.util.Calendar

const val DAY_MILLIS = 24L * 60 * 60 * 1000

/** Half-open time window [startMillis, endMillis). */
data class Period(val startMillis: Long, val endMillis: Long) {
    operator fun contains(millis: Long): Boolean = millis in startMillis until endMillis
}

object Periods {
    fun startOfDay(millis: Long): Long = Calendar.getInstance().apply {
        timeInMillis = millis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    /** Calendar month containing [millis], shifted by [offset] months (negative = past). */
    fun month(millis: Long, offset: Int = 0): Period {
        val start = Calendar.getInstance().apply {
            timeInMillis = startOfDay(millis)
            set(Calendar.DAY_OF_MONTH, 1)
            add(Calendar.MONTH, offset)
        }
        val end = (start.clone() as Calendar).apply { add(Calendar.MONTH, 1) }
        return Period(start.timeInMillis, end.timeInMillis)
    }

    /** Monday-to-Sunday week containing [millis], shifted by [offset] weeks. */
    fun week(millis: Long, offset: Int = 0): Period {
        val start = Calendar.getInstance().apply {
            timeInMillis = startOfDay(millis)
            firstDayOfWeek = Calendar.MONDAY
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            if (timeInMillis > startOfDay(millis)) add(Calendar.WEEK_OF_YEAR, -1)
            add(Calendar.WEEK_OF_YEAR, offset)
        }
        return Period(start.timeInMillis, start.timeInMillis + 7 * DAY_MILLIS)
    }

    /** 24-hour day containing [millis], shifted by [offset] days. */
    fun day(millis: Long, offset: Int = 0): Period {
        val start = startOfDay(millis) + offset * DAY_MILLIS
        return Period(start, start + DAY_MILLIS)
    }

    /** Calendar year containing [millis], shifted by [offset] years. */
    fun year(millis: Long, offset: Int = 0): Period {
        val start = Calendar.getInstance().apply {
            timeInMillis = startOfDay(millis)
            set(Calendar.DAY_OF_YEAR, 1)
            add(Calendar.YEAR, offset)
        }
        val end = (start.clone() as Calendar).apply { add(Calendar.YEAR, 1) }
        return Period(start.timeInMillis, end.timeInMillis)
    }

    fun daysRemainingInMonth(now: Long): Int {
        val calendar = Calendar.getInstance().apply { timeInMillis = now }
        val maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        return (maxDays - calendar.get(Calendar.DAY_OF_MONTH) + 1).coerceAtLeast(1)
    }
}
