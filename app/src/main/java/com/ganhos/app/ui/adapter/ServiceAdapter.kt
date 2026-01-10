package com.ganhos.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ganhos.app.data.database.entity.ServiceEntity
import com.ganhos.app.databinding.ItemServiceBinding
import com.bumptech.glide.Glide

class ServiceAdapter(
    private val onDelete: (ServiceEntity) -> Unit
) : RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {

    private var services: List<ServiceEntity> = emptyList()

    fun setServices(newServices: List<ServiceEntity>) {
        services = newServices
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val binding = ItemServiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ServiceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        holder.bind(services[position])
    }

    override fun getItemCount() = services.size

    inner class ServiceViewHolder(private val binding: ItemServiceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(service: ServiceEntity) {
            binding.serviceName.text = service.name

            // Carregar imagem (corrigida com escala apropriada)
            Glide.with(binding.root)
                .load(service.logoPath)
                .override(200, 200) // Escala fixa
                .centerCrop()
                .into(binding.serviceImage)

            binding.deleteBtn.setOnClickListener {
                onDelete(service)
            }
        }
    }
}
