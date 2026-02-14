package com.example.animewaifuapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.animewaifuapp.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySettingsBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        loadSettings()
        setupClickListeners()
    }
    
    private fun loadSettings() {
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val apiKey = prefs.getString("gemini_api_key", "") ?: ""
        
        if (apiKey.isNotEmpty()) {
            binding.editTextApiKey.setText(apiKey)
        }
    }
    
    private fun setupClickListeners() {
        binding.buttonSave.setOnClickListener {
            saveSettings()
        }
        
        binding.textGetApiKey.setOnClickListener {
            // Открываем инструкции по получению API ключа
            Toast.makeText(
                this,
                "Получите API ключ на: https://makersuite.google.com/app/apikey",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    
    private fun saveSettings() {
        val apiKey = binding.editTextApiKey.text.toString().trim()
        
        if (apiKey.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, введите API ключ", Toast.LENGTH_SHORT).show()
            return
        }
        
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        prefs.edit().putString("gemini_api_key", apiKey).apply()
        
        Toast.makeText(this, "Настройки сохранены! ✓", Toast.LENGTH_SHORT).show()
        finish()
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
