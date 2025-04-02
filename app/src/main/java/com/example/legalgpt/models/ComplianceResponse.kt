package com.example.legalgpt.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable




//data class ComplianceResponse(
//    val complianceIssues: Map<String, ClauseResponse>
//)

typealias LegalAnalysisResponse = Map<String, ComplianceIssue>

@Serializable
data class ComplianceIssue(
    val Clause: String,
    @SerialName("Legal Rule") val LegalRule: String,
    val Reason: String,
    val Violates: String
)

