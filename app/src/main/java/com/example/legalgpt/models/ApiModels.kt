package com.example.legalgpt.models

import com.google.gson.annotations.SerializedName

// API response models
data class AnalysisResponse(
    val good_clauses: List<GoodClause>,
    val recommendations: List<Recommendation>,
    val risk_clauses: List<RiskClause>
)

data class GoodClause(
    val clause: String,
    val reason: String
)

data class Recommendation(
    val clause: String,
    val reason: String,
    val suggested_rewrite: String
)

data class RiskClause(
    val clause: String,
    val risk: String
)

// UI models
data class StrengthPoint(
    val clause: String,
    val reason: String
)

data class RiskPoint(
    val clause: String,
    val risk: String
)

data class RecommendationPoint(
    val clause: String,
    val reason: String,
    val suggestedRewrite: String
)

data class ContractResponse(
    val message: String,
    val contract: String,
    val pdf_url: String?
)




// Chat state for UI
data class ChatState(
    val messages: List<ChatMessage> = emptyList(),
    val messagesRemaining: Int = 10,
    val messagesSent: Int = 0,
    val status: String = "in_progress",
    val user1Messages: Int = 0,
    val user2Messages: Int = 0,
    val verdict: Verdict? = null
)

// Individual chat message
data class ChatMessage(
    val speaker: String,
    val message: String
)

// API request
data class ChatRequest(
    val negotiation_id: String,
    val speaker: String,
    val message: String
)

// API response
data class ChatResponse(
    val messages_remaining: Int,
    val messages_sent: Int,
    val negotiation_id: String,
    val status: String,
    val user1_messages: Int,
    val user2_messages: Int,
    val messages: List<ChatMessage>? = null,
    val verdict: Verdict? = null
)

// Verdict in the final response
data class Verdict(
    val compromise: String,
    val summary: String
)


// Model for OCR result
data class OcrResult(val text: String)

// Model for API request
data class OCRApiRequest(
    val legal_text: String // Changed from extractedText to legal_text
)


// Model for API response
data class OCRApiResponse(
    val good_clausess: List<String>, // Changed to list of strings
    val bad_clausess: List<String>   // Changed to list of strings
)





