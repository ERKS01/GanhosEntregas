package com.ganhos.app.ui.screens

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.core.content.ContextCompat
import com.ganhos.app.R
import com.ganhos.app.databinding.FragmentHomeBinding
import com.ganhos.app.ui.viewmodel.HomeViewModel
import com.ganhos.app.ui.adapter.DayAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private var dayAdapter: DayAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupObservers()
        setupClickListeners()
    }

    private fun setupUI() {
        binding.daysRecycler.layoutManager = LinearLayoutManager(requireContext())
        viewModel.createWeek()
    }

    private fun setupClickListeners() {
        // Botão para Serviços
        binding.toServicesButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_servicesFragment)
        }

        // Botão para Histórico
        binding.toHistoryButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_historyFragment)
        }

        // Botão para Mudar Data
        binding.changeDateButton.setOnClickListener {
            showDatePicker()
        }

        // TextWatcher para Meta
        binding.weekGoalInput?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateGoalColor()
            }
        })

        binding.weekGoalInput?.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val newGoal = binding.weekGoalInput?.text.toString().toDoubleOrNull() ?: 0.0
                viewModel.setCurrentWeekStartDate(newGoal)
            }
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentWeekDays.collect { days ->
                if (dayAdapter == null) {
                    dayAdapter = DayAdapter(
                        days = days.toMutableList(),
                        onServiceChange = { day, services ->
                            viewModel.updateServiceAmount(day, services)
                        },
                        onOffDayChange = { day ->
                            viewModel.updateDayOffDay(day)
                        }
                    )
                    binding.daysRecycler.adapter = dayAdapter
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.services.collect { services ->
                dayAdapter?.notifyDataSetChanged()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.weekGoal.collect { goal ->
                binding.weekGoalInput?.setText(goal.toString())
                updateGoalColor()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.weekSummary.collect { summary ->
                val totalRealized = summary.totalRealized
                val goal = summary.goal
                val remaining = if (goal > totalRealized) goal - totalRealized else 0.0
                val totalHours = summary.totalHours
                val workDays = summary.workDays

                binding.summaryRealized.text = totalRealized.formatCurrency()
                binding.summaryRemaining.text = remaining.formatCurrency()
                binding.summaryPerHour.text = if (totalHours > 0) (totalRealized / totalHours).formatCurrency() else "R$ 0,00"
                binding.summaryWorkDays.text = "${workDays} dias"
                binding.summaryTotalHours.text = String.format("%.1f h", totalHours)

                if (totalRealized >= goal) {
                    binding.summaryRealized.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                    binding.summaryRemaining.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
                } else {
                    binding.summaryRealized.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
                    binding.summaryRemaining.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
                }
            }
        }
    }

    private fun updateGoalColor() {
        val goal = binding.weekGoalInput?.text.toString().toDoubleOrNull() ?: 0.0
        binding.weekGoalInput?.setTextColor(
            if (goal > 0) ContextCompat.getColor(requireContext(), R.color.green)
            else ContextCompat.getColor(requireContext(), R.color.gray)
        )
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                viewModel.setCurrentWeekStartDate(calendar.timeInMillis.toDouble())
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// Extension function para formatação
fun Double.formatCurrency(): String {
    return String.format("R$ %.2f", this).replace(".", ",")
}