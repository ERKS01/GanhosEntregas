package com.ganhos.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ganhos.app.data.database.entity.DayEntity
import com.ganhos.app.data.database.entity.DayServiceEntity
import com.ganhos.app.data.database.entity.ServiceEntity
import com.ganhos.app.databinding.ItemDayBinding
import com.ganhos.app.utils.DateUtils
import com.ganhos.app.utils.formatCurrency
import java.util.Date

class DayAdapter(
    private val services: List<ServiceEntity>,
    private val onOffDayChanged: (dayKey: String, isOff: Boolean) -> Unit,
    private val onHoursChanged: (dayKey: String, hours: Double) -> Unit,
    private val onServiceAmountChanged: (dayKey: String, serviceId: Int, amount: Double) -> Unit
) : RecyclerView.Adapter<DayAdapter.DayViewHolder>() {

    private var days: List<Pair<DayEntity, List<DayServiceEntity>>> = emptyList()

    fun setDays(newDays: List<Pair<DayEntity, List<DayServiceEntity>>>) {
        days = newDays
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val binding = ItemDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val (day, dayServices) = days[position]
        holder.bind(day, dayServices, services)
    }

    override fun getItemCount() = days.size

    inner class DayViewHolder(private val binding: ItemDayBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(day: DayEntity, dayServices: List<DayServiceEntity>, services: List<ServiceEntity>) {
            val date = Date()
            val dayDate = try {
                java.text.SimpleDateFormat("yyyy-MM-dd").parse(day.dayKey)
            } catch (e: Exception) {
                Date()
            }

            // Header
            binding.dayName.text = DateUtils.getDayName(dayDate ?: Date())
            binding.dayDate.text = day.dayKey

            // Checkbox de folga
            binding.offDayCheckbox.isChecked = day.isOffDay
            binding.offDayCheckbox.setOnCheckedChangeListener { _, isChecked ->
                onOffDayChanged(day.dayKey, isChecked)
            }
        }

        private fun addServiceInput(
            service: ServiceEntity,
            amount: Double,
            onAmountChanged: (Double) -> Unit
        ) {
            // Aqui você deve adicionar um layout customizado com EditText
            // Por enquanto, você cria dinamicamente ou cria layout XML separado
        }
    }
}