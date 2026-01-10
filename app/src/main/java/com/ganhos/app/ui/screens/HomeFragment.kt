package com.ganhos.app.ui.screens

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ganhos.app.databinding.FragmentHomeBinding
import com.ganhos.app.ui.adapter.DayAdapter
import com.ganhos.app.ui.viewmodel.MainViewModel
import com.ganhos.app.utils.DateUtils
import com.ganhos.app.utils.formatCurrency
import com.ganhos.app.utils.toDoubleOrZero
import kotlinx.coroutines.launch
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var dayAdapter: DayAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        // Setup RecyclerView para dias
        dayAdapter = DayAdapter(
            services = emptyList(),
            onOffDayChanged = { dayKey, isOff ->
                viewModel.updateDayOffDay(dayKey, isOff)
            },
            onHoursChanged = { dayKey, hours ->
                viewModel.updateDayHours(dayKey, hours)
            },
            onServiceAmountChanged = { dayKey, serviceId, amount ->
                viewModel.updateDayService(dayKey, serviceId, amount)
            }
        )

        binding.daysRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dayAdapter
        }

        // Data picker - APENAS SEGUNDA-FEIRA
        binding.weekStartDatePicker.setOnClickListener {
            showMondayOnlyDatePicker()
        }

        // Goal input - listener corrigido
        binding.goalInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val goal = binding.goalInput.text.toString().toDoubleOrZero()
                if (goal > 0) {
                    val selectedDate = binding.weekStartDatePicker.tag as? Long
                        ?: System.currentTimeMillis()
                    viewModel.createWeek(selectedDate, goal)
                }
            }
        }

        // Defina data inicial para hoje (segunda-feira atual)
        val mondayOfThisWeek = DateUtils.getMonday()
        val mondayTime = Calendar.getInstance().apply {
            time = mondayOfThisWeek
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }.timeInMillis
        binding.weekStartDatePicker.tag = mondayTime
        updateDatePickerDisplay(mondayOfThisWeek)
    }

    private fun showMondayOnlyDatePicker() {
        val calendar = Calendar.getInstance()
        // Customizar DatePickerDialog para aceitar apenas segundas
        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth, 0, 0, 0)
                }

                // Verificar se é segunda-feira, senão ajustar
                val adjustedMonday = if (selectedCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                    selectedCalendar.time
                } else {
                    // Retornar a segunda-feira da semana
                    selectedCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                    selectedCalendar.time
                }

                binding.weekStartDatePicker.tag = adjustedMonday.time
                updateDatePickerDisplay(adjustedMonday)
                val goal = binding.goalInput.text.toString().toDoubleOrZero()
                if (goal > 0) {
                    viewModel.createWeek(adjustedMonday.time, goal)
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePicker.show()
    }

    private fun updateDatePickerDisplay(date: Date) {
        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
        binding.weekStartDatePicker.text = sdf.format(date)
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.services.collect { services ->
                dayAdapter = DayAdapter(
                    services = services,
                    onOffDayChanged = { dayKey, isOff ->
                        viewModel.updateDayOffDay(dayKey, isOff)
                    },
                    onHoursChanged = { dayKey, hours ->
                        viewModel.updateDayHours(dayKey, hours)
                    },
                    onServiceAmountChanged = { dayKey, serviceId, amount ->
                        viewModel.updateDayService(dayKey, serviceId, amount)
                    }
                )

                binding.daysRecyclerView.adapter = dayAdapter
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentWeek.collect { weekData ->
                weekData?.let { (week, days) ->
                    if (week != null) {
                        // Update cards
                        binding.summaryGoal.text = week.goal.formatCurrency()
                        val totalRealized = days.sumOf { it.realizedAmount }
                        val remaining = (week.goal - totalRealized).coerceAtLeast(0.0)
                        val totalHours = days.sumOf { it.workedHours }
                        val hourlyRate = if (totalHours > 0) totalRealized / totalHours else 0.0
                        val workDays = days.count { !it.isOffDay }

                        // Progress bar
                        val progress = if (week.goal > 0)
                            ((totalRealized / week.goal) * 100).toInt().coerceIn(0, 100)
                        else 0
                        binding.progressBar.progress = progress
                        binding.progressPercent.text = "$progress%"

                        // Update days
                        dayAdapter.setDays(
                            days.map { day ->
                                // TODO: carregar DayServiceEntity
                                Pair(day, emptyList())
                            }
                        )
                    }
                }
            }
        }
    }
}