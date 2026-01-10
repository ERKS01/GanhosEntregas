package com.ganhos.app.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val dayNameFormat = SimpleDateFormat("EEE", Locale("pt", "BR"))

    fun getMonday(date: Date = Date()): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        return calendar.time
    }

    fun formatDate(date: Date): String = dateFormat.format(date)

    fun getWeekKey(timestamp: Long): String {
        return formatDate(getMonday(Date(timestamp)))
    }

    fun getDayName(date: Date): String {
        return dayNameFormat.format(date).capitalize()
    }

    fun getDateForDayIndex(weekStartDate: Long, index: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = Date(weekStartDate)
        calendar.add(Calendar.DAY_OF_MONTH, index)
        return calendar.time
    }

    fun isMondayOfWeek(date: Date): Boolean {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY
    }

    fun getMondaysOnlyDatePicker(): Long? {
        // Retorna apenas segunda-feira
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        // Remove tempo
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        return calendar.timeInMillis
    }
}
