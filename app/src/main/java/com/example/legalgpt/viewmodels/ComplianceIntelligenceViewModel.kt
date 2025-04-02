package com.example.legalgpt.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.legalgpt.models.ComplianceIssue
import com.example.legalgpt.models.LegalAnalysisResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class ComplianceIntelligenceViewModel : ViewModel() {

    // State class to manage API state
    sealed class AnalysisState {
        object Loading : AnalysisState()
        data class Success(val data: List<ComplianceIssue>) : AnalysisState()
        data class Error(val message: String) : AnalysisState()
    }

    // Mutable state to track the API response state
    private val _analysisState = MutableStateFlow<AnalysisState>(AnalysisState.Loading)
    val analysisState: StateFlow<AnalysisState> = _analysisState

    // Function to analyze PDF document
    fun analyzeDocument(context: Context, pdfUri: Uri) {
        viewModelScope.launch {
            _analysisState.value = AnalysisState.Loading
            try {
                Log.d("ComplianceViewModel", "Reading PDF file...")
                val inputStream = context.contentResolver.openInputStream(pdfUri)
                val pdfBytes = inputStream?.readBytes()
                inputStream?.close()

                if (pdfBytes != null) {
                    Log.d("ComplianceViewModel", "PDF file read successfully, size: ${pdfBytes.size} bytes")

                    // Send file to API using Retrofit
                    val response = RetrofitClient.analyzeDocumentCompliance(pdfBytes)

                    Log.d("ComplianceViewModel", "API call made, response code: ${response.code()}")

                    if (response.isSuccessful) {
                        val complianceIssues: LegalAnalysisResponse? = response.body()
                        Log.d("gyhh",response.body().toString())
                        if (complianceIssues != null) {
                            val issuesList = complianceIssues.values.toList()

                            Log.d("ComplianceViewModel", "Received valid response with ${issuesList.size} issues")
                            _analysisState.value = AnalysisState.Success(issuesList)
                        } else {
                            _analysisState.value = AnalysisState.Error("Empty response from server")
                            Log.e("ComplianceViewModel", "Empty response from server")
                        }
                    } else {
                        _analysisState.value =
                            AnalysisState.Error("Failed with error code: ${response.code()} - ${response.message()}")
                        Log.e("ComplianceViewModel", "Failed with error code: ${response.code()}")
                    }
                } else {
                    _analysisState.value = AnalysisState.Error("Failed to read PDF file")
                    Log.e("ComplianceViewModel", "Failed to read PDF file")
                }
            } catch (e: IOException) {
                _analysisState.value = AnalysisState.Error("IO Exception: ${e.message}")
                Log.e("ComplianceViewModel", "IO Exception: ${e.message}")
            } catch (e: Exception) {
                _analysisState.value = AnalysisState.Error("Exception: ${e.message}")
                Log.e("ComplianceViewModel", "Exception: ${e.message}")
            }
        }
    }
}




