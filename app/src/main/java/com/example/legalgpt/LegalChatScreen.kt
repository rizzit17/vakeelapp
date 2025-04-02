package com.example.legalgpt

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.legalgpt.models.ChatMessage
import com.example.legalgpt.models.Verdict
import com.example.legalgpt.viewmodels.ChatViewModel

// Using the color theme from HomeScreen

private val DarkCardBackground = Color(0xFF1A2538)

@Composable
fun LegalChatScreen() {
    val viewModel = viewModel<ChatViewModel>()
    val chatState by viewModel.chatState.collectAsState()
    val errorState by viewModel.errorState.collectAsState()
    val currentSpeaker by viewModel.currentSpeaker.collectAsState()

    // Create a state for the LazyColumn to control scrolling
    val listState = rememberLazyListState()

    // Auto-scroll to bottom whenever a new message is added
    LaunchedEffect(chatState.messages.size) {
        if (chatState.messages.isNotEmpty()) {
            listState.animateScrollToItem(chatState.messages.size - 1)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlueBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // Header Section with themed colors
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column {
                    Text(
                        text = "Legal",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = White
                    )
                    Text(
                        text = "Negotiation",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GoldAccent
                    )
                }
            }

            // Subtitle
            Text(
                text = "Let AI  help negotiate your legal conversation with AI-powered insights (Enter 5 messages from each user)",
                fontSize = 16.sp,
                color = LightGray,
                lineHeight = 24.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Error Banner
            errorState?.let { ErrorBanner(it, { viewModel.clearError() }) }

            // Chat Messages - Always visible
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCardBackground),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    items(chatState.messages) { message ->
                        ChatBubble(message)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Loading Indicator
            if (chatState.messages.size >= 10 && chatState.status != "completed") {
                LoadingIndicator()
            }

            // Verdict Section - No longer takes up the entire screen
            if (chatState.status == "completed") {
                chatState.verdict?.let { VerdictSection(it) }
            }

            // Message Input Field - Always available as long as we're under 10 messages
            if (chatState.messages.size < 10) {
                MessageInputField(
                    value = viewModel.messageText,
                    onValueChange = { viewModel.messageText = it },
                    onSendClick = {
                        viewModel.sendMessage()
                    },
                    isEnabled = viewModel.messageText.isNotEmpty(),
                    placeholder = "Type ${currentSpeaker}'s message..."
                )
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser1 = message.speaker == "user1"
    val bubbleColor = if (isUser1) GoldAccent.copy(alpha = 0.8f) else Color(0xFF2A3B52)
    val textColor = if (isUser1) Color.Black else White

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isUser1) Arrangement.Start else Arrangement.End
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser1) 4.dp else 16.dp,
                        bottomEnd = if (isUser1) 16.dp else 4.dp
                    )
                )
                .background(bubbleColor)
                .padding(12.dp)
        ) {
            Text(
                text = message.message,
                color = textColor,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun ErrorBanner(errorMessage: String, onDismiss: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF42202A)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "Error",
                tint = Color(0xFFFF6B6B)
            )
            Text(
                text = errorMessage,
                fontSize = 14.sp,
                color = White,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            )
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = White
                )
            }
        }
    }
}

@Composable
fun LoadingIndicator() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = GoldAccent
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Analyzing conversation...",
            fontSize = 16.sp,
            color = GoldAccent
        )
    }
}

@Composable
fun MessageInputField(
    value: String,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit,
    isEnabled: Boolean,
    placeholder: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCardBackground),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                placeholder = {
                    Text(
                        text = placeholder,
                        color = LightGray
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { if (isEnabled) onSendClick() }),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedTextColor = White,
                    focusedTextColor = White,
                    cursorColor = GoldAccent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            IconButton(
                onClick = onSendClick,
                enabled = isEnabled,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        color = if (isEnabled) GoldAccent else DarkCardBackground.copy(alpha = 0.5f)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    tint = if (isEnabled) Color.Black else LightGray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun VerdictSection(verdict: Verdict) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCardBackground),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Gold accent background for title
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(GoldAccent.copy(alpha = 0.2f))
                    .padding(12.dp)
            ) {
                Text(
                    text = "VERDICT",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldAccent
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Summary",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = GoldAccent
            )
            Text(
                text = verdict.summary,
                fontSize = 14.sp,
                color = White,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Compromise",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = GoldAccent
            )
            Text(
                text = verdict.compromise,
                fontSize = 14.sp,
                color = White,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}