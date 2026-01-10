package com.ganhos.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ganhos.app.data.database.entity.WeekEntity
import com.ganhos.app.databinding.ItemHistoryBinding
import com.ganhos.app.utils.formatCurrency
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private var weeks: List<WeekEntity> = emptyList()

    fun setWeeks(newWeeks: List<WeekEntity>) {
        weeks = newWeeks.sortedByDescending { it.weekKey }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(weeks[position])
    }

    override fun getItemCount() = weeks.size

    inner class HistoryViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(week: WeekEntity) {
            // TODO: Implementar com dados da semana
            binding.weekRange.text = "Semana: ${week.weekKey}"
            binding.goalValue.text = week.goal.formatCurrency()
        }
    }
}
