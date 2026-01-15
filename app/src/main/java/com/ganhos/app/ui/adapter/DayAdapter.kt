package com.ganhos.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.recyclerview.widget.RecyclerView
import com.ganhos.app.R
import com.ganhos.app.data.database.entity.DayEntity
import com.ganhos.app.data.database.entity.ServiceEntity
import com.ganhos.app.databinding.DayItemBinding
import com.ganhos.app.utils.formatCurrency
import com.ganhos.app.utils.limitDecimals
import com.ganhos.app.utils.toDoubleOrZero
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DayAdapter(
    private val days: MutableList<DayEntity>,
    private val onServiceChange: (DayEntity, List<ServiceEntity>) -> Unit,
    private val onOffDayChange: (DayEntity) -> Unit
) : RecyclerView.Adapter<DayAdapter.DayViewHolder>() {

    inner class DayViewHolder(private val binding: DayItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(entity: DayEntity) {
            val services = entity.services ?: emptyList()

            // Dia da semana
            val dateFormat = SimpleDateFormat("EEEE", Locale("pt", "BR"))
            val date = Date(entity.date)
            binding.dayNameText.text = dateFormat.format(date).replaceFirstChar { it.uppercase() }

            // Total do dia
            val dayTotal = services.sumOf { it.amount }.limitDecimals()
            binding.dayValueText.text = dayTotal.formatCurrency()

            // Checkbox Folga
            binding.isOffDay.apply {
                isChecked = entity.isOffDay
                setOnCheckedChangeListener(null) // Remove listener anterior
                setOnCheckedChangeListener { _, isChecked ->
                    entity.isOffDay = isChecked
                    onOffDayChange(entity)
                }
            }

            // Container de serviços
            binding.servicesContainer.removeAllViews()
            for (service in services) {
                addServiceInput(
                    binding.servicesContainer,
                    service,
                    entity,
                    services
                )
            }

            // Botão para adicionar novo serviço
            binding.servicesContainer.addView(
                createAddServiceButton(entity, services)
            )
        }

        private fun addServiceInput(
            container: LinearLayout,
            service: ServiceEntity,
            day: DayEntity,
            currentServices: List<ServiceEntity>
        ) {
            val itemView = LayoutInflater.from(container.context)
                .inflate(R.layout.service_input_item, container, false)

            val nameInput = itemView.findViewById<EditText>(R.id.serviceNameInput)
            val amountInput = itemView.findViewById<EditText>(R.id.serviceAmountInput)
            val hoursInput = itemView.findViewById<EditText>(R.id.serviceHoursInput)
            val deleteBtn = itemView.findViewById<TextView>(R.id.deleteServiceBtn)

            nameInput.setText(service.name)
            amountInput.setText(service.amount.toString())
            hoursInput.setText(service.hours.toString())

            deleteBtn.setOnClickListener {
                container.removeView(itemView)
                val updated = currentServices.filter { it.id != service.id }
                onServiceChange(day, updated)
            }

            nameInput.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    service.name = nameInput.text.toString()
                    updateServices(day, currentServices)
                }
            }

            amountInput.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    val newAmount = amountInput.text.toString().toDoubleOrZero().limitDecimals()
                    service.amount = newAmount
                    binding.dayValueText.text = currentServices.sumOf { it.amount }.formatCurrency()
                    updateServices(day, currentServices)
                }
            }

            hoursInput.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    service.hours = hoursInput.text.toString().toDoubleOrZero().limitDecimals()
                    updateServices(day, currentServices)
                }
            }

            container.addView(itemView)
        }

        private fun createAddServiceButton(
            day: DayEntity,
            currentServices: List<ServiceEntity>
        ): TextView {
            return TextView(binding.root.context).apply {
                text = "+ Adicionar Serviço"
                textSize = 14f
                setPadding(16, 16, 16, 16)
                setOnClickListener {
                    val newService = ServiceEntity(
                        id = 0,
                        name = "Novo Serviço",
                        amount = 0.0,
                        hours = 0.0
                    )
                    val updated = currentServices.toMutableList().apply { add(newService) }
                    onServiceChange(day, updated)
                    // Rebind para mostrar o novo serviço
                    bind(day.apply { services = updated })
                }
            }
        }

        private fun updateServices(day: DayEntity, services: List<ServiceEntity>) {
            onServiceChange(day, services)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val binding = DayItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bind(days[position])
    }

    override fun getItemCount() = days.size
}