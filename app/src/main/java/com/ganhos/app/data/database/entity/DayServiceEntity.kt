package com.ganhos.app.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "day_services",
    primaryKeys = ["dayKey", "serviceId"],
    foreignKeys = [
        ForeignKey(
            entity = DayEntity::class,
            parentColumns = ["dayKey"],
            childColumns = ["dayKey"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ServiceEntity::class,
            parentColumns = ["id"],
            childColumns = ["serviceId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DayServiceEntity(
    val dayKey: String,
    val serviceId: Int,
    val amount: Double = 0.0
)