package com.example.animewaifuapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.animewaifuapp.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var geminiClient: GeminiClient
    private val messages = mutableListOf<ChatMessage>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        
        setupRecyclerView()
        setupClickListeners()
        initializeGeminiClient()
        showWelcomeMessage()
    }
    
    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(messages)
        binding.recyclerViewChat.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = chatAdapter
        }
    }
    
    private fun setupClickListeners() {
        binding.buttonSend.setOnClickListener {
            val userMessage = binding.editTextMessage.text.toString().trim()
            if (userMessage.isNotEmpty()) {
                sendMessage(userMessage)
            }
        }
        
        binding.waifuImage.setOnClickListener {
            animateWaifu()
            showRandomReaction()
        }
    }
    
    private fun initializeGeminiClient() {
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val apiKey = prefs.getString("gemini_api_key", "") ?: ""
        
        if (apiKey.isEmpty()) {
            showApiKeyDialog()
        } else {
            geminiClient = GeminiClient(apiKey)
        }
    }
    
    private fun showWelcomeMessage() {
        val welcomeMessage = ChatMessage(
            text = "Привет! Я твоя аниме вайфу! 💖\n\nЯ здесь, чтобы поговорить с тобой о чём угодно. Спроси меня что-нибудь или просто поболтай! (◕‿◕✿)",
            isUser = false,
            timestamp = System.currentTimeMillis()
        )
        addMessage(welcomeMessage)
    }
    
    private fun sendMessage(text: String) {
        val userMessage = ChatMessage(
            text = text,
            isUser = true,
            timestamp = System.currentTimeMillis()
        )
        addMessage(userMessage)
        binding.editTextMessage.text?.clear()
        
        if (!::geminiClient.isInitialized) {
            showApiKeyDialog()
            return
        }
        
        showTypingIndicator()
        animateWaifu()
        
        lifecycleScope.launch {
            try {
                val response = geminiClient.sendMessage(text)
                hideTypingIndicator()
                
                val waifuMessage = ChatMessage(
                    text = response,
                    isUser = false,
                    timestamp = System.currentTimeMillis()
                )
                addMessage(waifuMessage)
                
            } catch (e: Exception) {
                hideTypingIndicator()
                Toast.makeText(
                    this@MainActivity,
                    "Ошибка: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    
    private fun addMessage(message: ChatMessage) {
        messages.add(message)
        chatAdapter.notifyItemInserted(messages.size - 1)
        binding.recyclerViewChat.smoothScrollToPosition(messages.size - 1)
    }
    
    private fun showTypingIndicator() {
        binding.typingIndicator.visibility = View.VISIBLE
    }
    
    private fun hideTypingIndicator() {
        binding.typingIndicator.visibility = View.GONE
    }
    
    private fun animateWaifu() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.bounce)
        binding.waifuImage.startAnimation(animation)
    }
    
    private fun showRandomReaction() {
        val reactions = listOf(
            "Кья! (⁄ ⁄>⁄ ▽ ⁄<⁄ ⁄)",
            "Хихи~ ✧◝(⁰▿⁰)◜✧",
            "Н-не щекочи! (≧◡≦)",
            "Мяу~ (=^･ω･^=)",
            "Давай поговорим! (｡♥‿♥｡)"
        )
        
        val reactionMessage = ChatMessage(
            text = reactions.random(),
            isUser = false,
            timestamp = System.currentTimeMillis()
        )
        addMessage(reactionMessage)
    }
    
    private fun showApiKeyDialog() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
        Toast.makeText(
            this,
            "Пожалуйста, введите API ключ Gemini в настройках",
            Toast.LENGTH_LONG
        ).show()
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            R.id.action_clear_chat -> {
                messages.clear()
                chatAdapter.notifyDataSetChanged()
                showWelcomeMessage()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
