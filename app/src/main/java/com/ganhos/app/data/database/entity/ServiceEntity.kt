package com.ganhos.app.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val logoPath: String, // Caminho da imagem da galeria
    val createdAt: Long = System.currentTimeMillis()
)