package com.ganhos.app.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "days",
    foreignKeys = [
        ForeignKey(
            entity = WeekEntity::class,
            parentColumns = ["weekKey"],
            childColumns = ["weekKey"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DayEntity(
    @PrimaryKey
    val dayKey: String, // Format: YYYY-MM-DD
    val weekKey: String,
    val dayIndex: Int, // 0 = segunda, 6 = domingo
    val isOffDay: Boolean = false,
    val realizedAmount: Double = 0.0,
    val previewAmount: Double = 0.0,
    val workedHours: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis()
)