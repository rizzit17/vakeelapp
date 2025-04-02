package com.example.legalgpt.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.legalgpt.models.AnalysisResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class RiskDetectionViewModel : ViewModel() {
    private val _analysisState = MutableStateFlow<AnalysisState>(AnalysisState.Idle)
    val analysisState: StateFlow<AnalysisState> = _analysisState

    fun analyzeDocument(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                _analysisState.value = AnalysisState.Loading

                // Get file from URI
                val file = getFileFromUri(context, uri)
                if (file == null) {
                    _analysisState.value = AnalysisState.Error("Failed to process file")
                    return@launch
                }

                // Create multipart request
                val requestFile = file.asRequestBody("application/pdf".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

                // Make API call
                val response = RetrofitClient.legalGptApi.analyzeDocument(body)
                Log.d("bye",response.toString())

                if (response.isSuccessful) {
                    response.body()?.let { result ->
                        _analysisState.value = AnalysisState.Success(result)
                    } ?: run {
                        _analysisState.value = AnalysisState.Error("Empty response")
                    }
                } else {
                    _analysisState.value = AnalysisState.Error("API error: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("RiskDetectionVM", "Error analyzing document", e)
                _analysisState.value = AnalysisState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun getFileFromUri(context: Context, uri: Uri): File? {
        return try {
            Log.d("RiskDetectionVM", "Starting to process file from URI: $uri")
            val inputStream = context.contentResolver.openInputStream(uri)
            if (inputStream == null) {
                Log.e("RiskDetectionVM", "Failed to open input stream")
                return null
            }

            val fileName = uri.lastPathSegment ?: "document.pdf"
            Log.d("RiskDetectionVM", "File name: $fileName")
            val tempFile = File(context.cacheDir, "upload_$fileName")

            FileOutputStream(tempFile).use { outputStream ->
                val bytesCopied = inputStream.copyTo(outputStream)
                Log.d("RiskDetectionVM", "Bytes copied: $bytesCopied")
                inputStream.close()
            }

            Log.d("RiskDetectionVM", "File created successfully: ${tempFile.absolutePath}")
            tempFile
        } catch (e: Exception) {
            Log.e("RiskDetectionVM", "Error getting file from URI", e)
            null
        }
    }


    sealed class AnalysisState {
        object Idle : AnalysisState()
        object Loading : AnalysisState()
        data class Success(val data: AnalysisResponse) : AnalysisState()
        data class Error(val message: String) : AnalysisState()
    }
}
