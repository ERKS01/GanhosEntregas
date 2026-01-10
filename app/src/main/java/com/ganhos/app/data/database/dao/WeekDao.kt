package com.ganhos.app.data.database.dao

import androidx.room.*
import com.ganhos.app.data.database.entity.DayEntity
import com.ganhos.app.data.database.entity.DayServiceEntity
import com.ganhos.app.data.database.entity.WeekEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeekDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeek(week: WeekEntity)

    @Query("SELECT * FROM weeks WHERE weekKey = :weekKey")
    suspend fun getWeek(weekKey: String): WeekEntity?

    @Query("SELECT * FROM weeks ORDER BY weekKey DESC")
    fun getAllWeeks(): Flow<List<WeekEntity>>

    // Days
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDay(day: DayEntity)

    @Update
    suspend fun updateDay(day: DayEntity)

    @Query("SELECT * FROM days WHERE weekKey = :weekKey ORDER BY dayIndex ASC")
    suspend fun getDaysForWeek(weekKey: String): List<DayEntity>

    @Query("SELECT * FROM days WHERE dayKey = :dayKey")
    suspend fun getDay(dayKey: String): DayEntity?

    // Day Services
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDayService(dayService: DayServiceEntity)

    @Query("SELECT * FROM day_services WHERE dayKey = :dayKey")
    suspend fun getDayServices(dayKey: String): List<DayServiceEntity>

    @Query("DELETE FROM day_services WHERE dayKey = :dayKey AND serviceId = :serviceId")
    suspend fun deleteDayService(dayKey: String, serviceId: Int)
}