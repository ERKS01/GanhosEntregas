package com.ganhos.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ganhos.app.data.database.AppDatabase
import com.ganhos.app.data.database.entity.DayEntity
import com.ganhos.app.data.database.entity.ServiceEntity
import com.ganhos.app.data.database.entity.WeekEntity
import com.ganhos.app.data.repository.AppRepository
import com.ganhos.app.utils.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository(AppDatabase.getDatabase(application))

    // UI State
    private val _services = MutableStateFlow<List<ServiceEntity>>(emptyList())
    val services: StateFlow<List<ServiceEntity>> = _services

    private val _currentWeek = MutableStateFlow<Pair<WeekEntity?, List<DayEntity>>?>(null)
    val currentWeek: StateFlow<Pair<WeekEntity?, List<DayEntity>>?> = _currentWeek

    private val _weekHistory = MutableStateFlow<List<WeekEntity>>(emptyList())
    val weekHistory: StateFlow<List<WeekEntity>> = _weekHistory

    init {
        viewModelScope.launch {
            repository.getAllServices().collect {
                _services.value = it
            }
        }

        viewModelScope.launch {
            repository.getAllWeeks().collect {
                _weekHistory.value = it
            }
        }
    }

    fun loadWeek(weekKey: String) {
        viewModelScope.launch {
            val weekData = repository.getWeekData(weekKey)
            _currentWeek.value = weekData
        }
    }

    fun createWeek(weekStartDate: Long, goal: Double) {
        viewModelScope.launch {
            repository.initializeWeek(weekStartDate, goal)
            val weekKey = DateUtils.getWeekKey(weekStartDate) // ✅ CHAMADA DIRETA
            loadWeek(weekKey)
        }
    }

    fun addService(name: String, logoPath: String) {
        viewModelScope.launch {
            repository.addService(name, logoPath)
        }
    }

    fun deleteService(service: ServiceEntity) {
        viewModelScope.launch {
            repository.deleteService(service)
        }
    }

    fun updateDayOffDay(dayKey: String, isOff: Boolean) {
        viewModelScope.launch {
            repository.updateDayOffDay(dayKey, isOff)
            val (week, _) = _currentWeek.value ?: return@launch
            week?.weekKey?.let {
                repository.calculateWeek(it)
                loadWeek(it)
            }
        }
    }

    fun updateDayHours(dayKey: String, hours: Double) {
        viewModelScope.launch {
            repository.updateDayHours(dayKey, hours)
            val (week, _) = _currentWeek.value ?: return@launch
            week?.weekKey?.let {
                repository.calculateWeek(it)
                loadWeek(it)
            }
        }
    }

    fun updateDayService(dayKey: String, serviceId: Int, amount: Double) {
        viewModelScope.launch {
            repository.updateDayService(dayKey, serviceId, amount)
            val (week, _) = _currentWeek.value ?: return@launch
            week?.weekKey?.let {
                repository.calculateWeek(it)
                loadWeek(it)
            }
        }
    }
}
