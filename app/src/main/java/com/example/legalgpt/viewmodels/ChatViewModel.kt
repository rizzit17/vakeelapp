package com.example.legalgpt.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.legalgpt.models.ChatMessage
import com.example.legalgpt.models.ChatRequest
import com.example.legalgpt.models.ChatState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    var messageText by mutableStateOf("")
        internal set

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState

    private val _chatState = MutableStateFlow(ChatState())
    val chatState: StateFlow<ChatState> = _chatState

    private val apiService = RetrofitClient.chatApiService
    private var negotiationId: String = ""

    // 🔹 Make currentSpeaker observable
    private val _currentSpeaker = MutableStateFlow("user1")
    val currentSpeaker: StateFlow<String> = _currentSpeaker

    fun sendMessage() {
        if (messageText.isBlank() || _chatState.value.messages.size >= 10) return

        viewModelScope.launch {
            try {
                val request = ChatRequest(
                    negotiation_id = negotiationId,
                    speaker = _currentSpeaker.value,
                    message = messageText
                )

                val newMessage = ChatMessage(speaker = _currentSpeaker.value, message = messageText)
                _chatState.update { it.copy(messages = it.messages + newMessage) }

                // 🔹 Toggle speaker BEFORE clearing input field
                _currentSpeaker.value = if (_currentSpeaker.value == "user1") "user2" else "user1"

                // Clear input field
                messageText = ""

                _errorState.value = null

                val response = apiService.sendMessage(request)
                if (response.isSuccessful) {
                    val chatResponse = response.body()
                    chatResponse?.let {
                        negotiationId = it.negotiation_id
                        _chatState.update { currentState ->
                            currentState.copy(
                                messagesRemaining = it.messages_remaining,
                                messagesSent = it.messages_sent,
                                status = it.status,
                                user1Messages = it.user1_messages,
                                user2Messages = it.user2_messages,
                                verdict = it.verdict
                            )
                        }

                        if (_chatState.value.messages.size >= 10 || it.messages_remaining <= 0) {
                            _chatState.update { it.copy(status = "completed") }
                        }
                    } ?: run {
                        _errorState.value = "Received empty response from server"
                    }
                } else {
                    _errorState.value = "API error: ${response.code()} - ${response.message()}"
                }
            } catch (e: Exception) {
                _errorState.value = e.message ?: "An unknown error occurred"
            }
        }
    }

    fun clearError() {
        _errorState.value = null
    }
}
