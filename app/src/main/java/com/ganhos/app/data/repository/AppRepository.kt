package com.ganhos.app.data.repository

import com.ganhos.app.data.database.AppDatabase
import com.ganhos.app.data.database.entity.*
import com.ganhos.app.utils.DateUtils
import kotlinx.coroutines.flow.Flow

class AppRepository(database: AppDatabase) {
    private val serviceDao = database.serviceDao()
    private val weekDao = database.weekDao()

    // === SERVICES ===
    fun getAllServices(): Flow<List<ServiceEntity>> = serviceDao.getAllServices()

    suspend fun addService(name: String, logoPath: String) {
        serviceDao.insertService(
            ServiceEntity(name = name, logoPath = logoPath)
        )
    }

    suspend fun deleteService(service: ServiceEntity) {
        serviceDao.deleteService(service)
    }

    // === WEEKS ===
    suspend fun initializeWeek(weekStartDate: Long, goal: Double) {
        val weekKey = DateUtils.getWeekKey(weekStartDate)

        // Salvar semana
        weekDao.insertWeek(
            WeekEntity(weekKey = weekKey, goal = goal)
        )

        // Criar 7 dias
        repeat(7) { index ->
            val dayDate = DateUtils.getDateForDayIndex(weekStartDate, index)
            val dayKey = DateUtils.formatDate(dayDate)

            weekDao.insertDay(
                DayEntity(
                    dayKey = dayKey,
                    weekKey = weekKey,
                    dayIndex = index,
                    isOffDay = false,
                    realizedAmount = 0.0,
                    previewAmount = goal / 7.0,
                    workedHours = 0.0
                )
            )
        }
    }

    suspend fun getWeekData(weekKey: String): Pair<WeekEntity?, List<DayEntity>>? {
        val week = weekDao.getWeek(weekKey) ?: return null
        val days = weekDao.getDaysForWeek(weekKey)
        return Pair(week, days)
    }

    fun getAllWeeks(): Flow<List<WeekEntity>> = weekDao.getAllWeeks()

    // === DAYS ===
    suspend fun updateDayOffDay(dayKey: String, isOff: Boolean) {
        val day = weekDao.getDay(dayKey) ?: return
        weekDao.updateDay(day.copy(isOffDay = isOff))
    }

    suspend fun updateDayHours(dayKey: String, hours: Double) {
        val day = weekDao.getDay(dayKey) ?: return
        weekDao.updateDay(day.copy(workedHours = hours))
    }

    // === DAY SERVICES ===
    suspend fun updateDayService(dayKey: String, serviceId: Int, amount: Double) {
        weekDao.insertDayService(
            DayServiceEntity(dayKey = dayKey, serviceId = serviceId, amount = amount)
        )
    }

    suspend fun getDayServices(dayKey: String): List<DayServiceEntity> {
        return weekDao.getDayServices(dayKey)
    }

    // === CALCULATIONS ===
    suspend fun calculateWeek(weekKey: String) {
        val (week, days) = getWeekData(weekKey) ?: return
        if (week == null) return

        val goal = week.goal
        val offDaysCount = days.count { it.isOffDay }
        val workDays = 7 - offDaysCount

        // Calcular realizado total
        var totalRealized = 0.0
        var totalHours = 0.0
        var remainingGoal = goal

        days.forEachIndexed { index, day ->
            val dayServices = getDayServices(day.dayKey)
            val dayRealized = dayServices.sumOf { it.amount }

            totalRealized += dayRealized
            totalHours += day.workedHours

            // Calcular preview dinâmico
            val preview = if (day.isOffDay) {
                0.0
            } else {
                val remainingDays = days.drop(index)
                    .count { !it.isOffDay }
                if (remainingDays > 0) (remainingGoal - dayRealized) / remainingDays else 0.0
            }

            // Atualizar dia com preview recalculado
            weekDao.updateDay(
                day.copy(
                    realizedAmount = dayRealized,
                    previewAmount = preview
                )
            )

            remainingGoal -= dayRealized
        }

        // Atualizar semana com total
        weekDao.insertWeek(week.copy(updatedAt = System.currentTimeMillis()))
    }
}