package com.ganhos.app.ui.screens

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ganhos.app.databinding.FragmentServicesBinding
import com.ganhos.app.ui.adapter.ServiceAdapter
import com.ganhos.app.ui.viewmodel.MainViewModel
import com.ganhos.app.utils.Constants
import com.ganhos.app.utils.compressImage
import kotlinx.coroutines.launch
import java.io.File

class ServicesFragment : Fragment() {

    private lateinit var binding: FragmentServicesBinding
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var serviceAdapter: ServiceAdapter

    // Selecionar imagem da galeria
    private var selectedImageUri: Uri? = null
    private var selectedImagePath: String? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            // Copiar para cache e comprimir
            val inputStream = requireContext().contentResolver.openInputStream(it)
            val cacheFile = File(requireContext().cacheDir, "service_${System.currentTimeMillis()}.jpg")
            inputStream?.copyTo(cacheFile.outputStream())

            // Comprimir imagem
            val compressedFile = cacheFile.compressImage(
                maxWidth = Constants.IMAGE_MAX_WIDTH,
                maxHeight = Constants.IMAGE_MAX_HEIGHT
            )

            selectedImagePath = compressedFile.absolutePath
            // Mostrar preview (se implementado)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentServicesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        serviceAdapter = ServiceAdapter { service ->
            viewModel.deleteService(service)
        }

        binding.servicesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = serviceAdapter
        }

        binding.addServiceBtn.setOnClickListener {
            showAddServiceDialog()
        }
    }

    private fun showAddServiceDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(com.ganhos.app.R.layout.dialog_add_service, null)

        val serviceName = dialogView.findViewById<EditText>(com.ganhos.app.R.id.serviceName)
        val selectImageBtn = dialogView.findViewById<View>(com.ganhos.app.R.id.selectImageBtn)

        selectImageBtn.setOnClickListener {
            // Abrir galeria para seleção de imagem
            pickImageLauncher.launch("image/*")
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Adicionar Empresa")
            .setView(dialogView)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val name = serviceName.text.toString().trim()
                if (name.isNotEmpty() && selectedImagePath != null) {
                    viewModel.addService(name, selectedImagePath!!)
                    selectedImagePath = null
                    selectedImageUri = null
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.services.collect { services ->
                serviceAdapter.setServices(services)
            }
        }
    }
}