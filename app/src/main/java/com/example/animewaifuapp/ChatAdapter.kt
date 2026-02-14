package com.example.animewaifuapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.animewaifuapp.databinding.ItemMessageUserBinding
import com.example.animewaifuapp.databinding.ItemMessageWaifuBinding
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter(
    private val messages: List<ChatMessage>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    
    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_WAIFU = 2
    }
    
    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isUser) VIEW_TYPE_USER else VIEW_TYPE_WAIFU
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_USER) {
            val binding = ItemMessageUserBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            UserMessageViewHolder(binding)
        } else {
            val binding = ItemMessageWaifuBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            WaifuMessageViewHolder(binding)
        }
    }
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is UserMessageViewHolder) {
            holder.bind(message)
        } else if (holder is WaifuMessageViewHolder) {
            holder.bind(message)
        }
    }
    
    override fun getItemCount(): Int = messages.size
    
    class UserMessageViewHolder(
        private val binding: ItemMessageUserBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(message: ChatMessage) {
            binding.textMessage.text = message.text
            binding.textTime.text = formatTime(message.timestamp)
        }
    }
    
    class WaifuMessageViewHolder(
        private val binding: ItemMessageWaifuBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(message: ChatMessage) {
            binding.textMessage.text = message.text
            binding.textTime.text = formatTime(message.timestamp)
        }
    }
    
    companion object {
        private fun formatTime(timestamp: Long): String {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
    }
}
