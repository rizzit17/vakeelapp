package com.example.legalgpt.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.legalgpt.R
import com.example.legalgpt.models.ComplianceIssue
import com.example.legalgpt.viewmodels.ComplianceIntelligenceViewModel

// Same color scheme as previous screens
val DarkBlueBackground = Color(0xFF0D1526)
val GoldAccent = Color(0xFFB67F20)
val White = Color.White
val LightGray = Color(0xBBCCCCCC)
val DarkCardBackground = Color(0xFF1A2538)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComplianceIntelligenceScreen(
    navController: NavController,
    viewModel: ComplianceIntelligenceViewModel = viewModel()
) {
    val context = LocalContext.current
    val analysisState by viewModel.analysisState.collectAsState()
    var selectedPdfUri by remember { mutableStateOf<Uri?>(null) }

    // Directly store ComplianceIssue list
    val complianceIssues = remember { mutableStateListOf<ComplianceIssue>() }

    // Handle API response
    LaunchedEffect(analysisState) {
        when (val state = analysisState) {
            is ComplianceIntelligenceViewModel.AnalysisState.Success -> {
                complianceIssues.clear()
                complianceIssues.addAll(state.data)
            }
            is ComplianceIntelligenceViewModel.AnalysisState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            }
            else -> {} // Do nothing for Loading or Initial states
        }
    }

    // PDF picker launcher
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedPdfUri = it
            viewModel.analyzeDocument(context, it)
        }
    }

    // Only show loading when we have a selected file AND are in loading state
    val isAnalyzing = selectedPdfUri != null &&
            analysisState is ComplianceIntelligenceViewModel.AnalysisState.Loading

    // Only show analysis complete when we have successful results
    val analysisComplete = analysisState is ComplianceIntelligenceViewModel.AnalysisState.Success &&
            complianceIssues.isNotEmpty()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlueBackground)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Compliance Intelligence",
                            color = White,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(20.dp)
            ) {
                // Header section
                Text(
                    text = "Contract Analysis,",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = White
                )
                Text(
                    text = "Compliance Intelligence",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = GoldAccent,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Subtitle
                Text(
                    text = "Upload a contract or legal document for Compliance check",
                    fontSize = 16.sp,
                    color = LightGray,
                    lineHeight = 24.sp,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                if (!analysisComplete) {
                    // Upload card
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2538)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Icon with circular background
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(GoldAccent.copy(alpha = 0.2f))
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_document),
                                    contentDescription = "Document",
                                    tint = GoldAccent,
                                    modifier = Modifier.size(48.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            if (selectedPdfUri != null) {
                                Text(
                                    "Selected file: ${selectedPdfUri.toString().substringAfterLast("/")}",
                                    color = White,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }

                            Button(
                                onClick = { pdfPickerLauncher.launch("application/pdf") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                enabled = !isAnalyzing,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GoldAccent,
                                    disabledContainerColor = GoldAccent.copy(alpha = 0.5f)
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(
                                    text = if (selectedPdfUri == null) "Select PDF Document"
                                    else "Select Different Document",
                                    color = DarkBlueBackground,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Only show loading indicator when we're actually analyzing
                            if (isAnalyzing) {
                                Spacer(modifier = Modifier.height(24.dp))
                                CircularProgressIndicator(
                                    color = GoldAccent,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Analyzing document for legal risks...",
                                    color = LightGray
                                )
                            }
                        }
                    }
                } else {
                    // Show results in a LazyColumn
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            Text(
                                "Compliance Issues",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = White,
                                modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
                            )
                        }

                        items(complianceIssues) { issue ->
                            ComplianceIssueCard(issue)
                        }

                        // New Analysis button at bottom
                        item {
                            Button(
                                onClick = { pdfPickerLauncher.launch("application/pdf") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .padding(vertical = 8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GoldAccent
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(
                                    "Analyze New Document",
                                    color = DarkBlueBackground,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

// Display each compliance issue in a card with improved design
@Composable
fun ComplianceIssueCard(issue: ComplianceIssue) {
    val violationColor = when (issue.Violates) {
        "YES" -> Color(0xFFE57373) // Red for violation
        "NO" -> Color(0xFF81C784) // Green for no violation
        else -> Color.Gray // Neutral color for unknown
    }

    val iconRes = when (issue.Violates) {
        "YES" -> R.drawable.ic_shield // Assuming you have this icon
        "NO" -> R.drawable.ic_check // Assuming you have this icon
        else -> R.drawable.ic_document // Fallback icon
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (issue.Violates == "YES") Color(0xFF492A2A) else Color(0xFF2A4938)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(violationColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = if (issue.Violates == "YES") "Violation" else "Compliant",
                        tint = violationColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = if (issue.Violates == "YES") "Violation" else "Compliant",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
            }

            Text(
                "Clause:",
                fontWeight = FontWeight.Bold,
                color = White.copy(alpha = 0.8f),
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                issue.Clause ?: "No Clause Provided",
                color = White,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                "Legal Rule:",
                fontWeight = FontWeight.Bold,
                color = White.copy(alpha = 0.8f),
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                issue.LegalRule ?: "No Legal Rule Provided",
                color = White,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                "Reason:",
                fontWeight = FontWeight.Bold,
                color = White.copy(alpha = 0.8f),
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                issue.Reason ?: "No Reason Provided",
                color = White
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Violation status in a card
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2A3548)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Violates:",
                        fontWeight = FontWeight.Medium,
                        color = White,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = issue.Violates ?: "Unknown",
                        fontWeight = FontWeight.Bold,
                        color = violationColor
                    )
                }
            }
        }
    }
}