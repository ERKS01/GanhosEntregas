package com.ganhos.app.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.ganhos.app.R
import com.ganhos.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            // Encontrar o NavHostFragment usando supportFragmentManager
            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.navHostFragment) as? NavHostFragment

            if (navHostFragment != null) {
                val navController = navHostFragment.navController
                binding.bottomNav.setupWithNavController(navController)
            } else {
                // Log para debug
                android.util.Log.e("MainActivity", "NavHostFragment não encontrado!")
            }
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Erro ao configurar navegação: ${e.message}")
            e.printStackTrace()
        }
    }
}