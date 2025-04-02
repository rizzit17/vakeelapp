package com.example.legalgpt.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.legalgpt.models.OCRApiRequest
import com.example.legalgpt.models.OCRApiResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Response

class OcrViewModel : ViewModel() {
    private val apiService = RetrofitClient.apiService

    private val _uiState = MutableStateFlow(OcrUiState())
    val uiState: StateFlow<OcrUiState> = _uiState.asStateFlow()

    fun updateExtractedText(text: String) {
        _uiState.update { it.copy(
            extractedText = text,
            isLoading = false
        ) }
    }

    fun processTextWithApi(editableText: String) {
        if (editableText.isBlank()) return

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                // Create request with the new format
                val request = OCRApiRequest(legal_text = editableText)
                val response = apiService.processText(request)

                _uiState.update { it.copy(
                    apiResponse = response,
                    isLoading = false,
                    error = null
                ) }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = "Error: ${e.message}"
                ) }
            }
        }
    }

    fun resetState() {
        _uiState.update { OcrUiState() }
    }
}

data class OcrUiState(
    val extractedText: String = "",
    val apiResponse: Response<OCRApiResponse>? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
