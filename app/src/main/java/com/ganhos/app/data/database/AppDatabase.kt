package com.ganhos.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ganhos.app.data.database.dao.ServiceDao
import com.ganhos.app.data.database.dao.WeekDao
import com.ganhos.app.data.database.entity.*

@Database(
    entities = [
        ServiceEntity::class,
        WeekEntity::class,
        DayEntity::class,
        DayServiceEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun serviceDao(): ServiceDao
    abstract fun weekDao(): WeekDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ganhos_database"
                ).build().also { instance = it }
            }
        }
    }
}