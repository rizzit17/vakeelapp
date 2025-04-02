package com.example.legalgpt

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.legalgpt.models.RecommendationPoint
import com.example.legalgpt.models.RiskPoint
import com.example.legalgpt.models.StrengthPoint
import com.example.legalgpt.ui.theme.LegalGPTTheme
import com.example.legalgpt.viewmodels.RiskDetectionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiskDetectionScreen(navController: NavController, viewModel: RiskDetectionViewModel = viewModel()) {
    val context = LocalContext.current
    val analysisState by viewModel.analysisState.collectAsState()
    var selectedPdfUri by remember { mutableStateOf<Uri?>(null) }

    // Store data points
    val strengthPoints = remember { mutableStateListOf<StrengthPoint>() }
    val riskPoints = remember { mutableStateListOf<RiskPoint>() }
    val recommendationPoints = remember { mutableStateListOf<RecommendationPoint>() }

    LaunchedEffect(analysisState) {
        when (val state = analysisState) {
            is RiskDetectionViewModel.AnalysisState.Success -> {
                strengthPoints.clear()
                riskPoints.clear()
                recommendationPoints.clear()

                state.data.good_clauses.forEach { clause ->
                    strengthPoints.add(StrengthPoint(clause.clause, clause.reason))
                }

                state.data.risk_clauses.forEach { clause ->
                    riskPoints.add(RiskPoint(clause.clause, clause.risk))
                }

                state.data.recommendations.forEach { rec ->
                    recommendationPoints.add(RecommendationPoint(rec.clause, rec.reason, rec.suggested_rewrite))
                }
            }
            is RiskDetectionViewModel.AnalysisState.Error -> {
                Log.d("RiskDetectionVM", state.message)
            }
            else -> {}
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

    var isAnalyzing = analysisState is RiskDetectionViewModel.AnalysisState.Loading
    var analysisComplete = analysisState is RiskDetectionViewModel.AnalysisState.Success

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
                            "Risk Detection",
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
                    text = "Risk Detection",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = GoldAccent,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Subtitle
                Text(
                    text = "Upload a contract or legal document for AI-powered risk analysis",
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
                                    "Select PDF Document",
                                    color = DarkBlueBackground,
                                    fontWeight = FontWeight.Bold
                                )
                            }

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
                    // Analysis results
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Strengths section
                        item {
                            Text(
                                "Strengths",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = White,
                                modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
                            )

                            if (strengthPoints.isEmpty()) {
                                Text(
                                    "No strengths identified",
                                    color = LightGray,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }

                            strengthPoints.forEach { point ->
                                ResultCard(
                                    title = "Strength",
                                    clause = point.clause,
                                    details = point.reason,
                                    cardColor = Color(0xFF2A4938),
                                    iconColor = Color(0xFF4CAF50)
                                )
                            }
                        }

                        // Risks section
                        item {
                            Text(
                                "Risks",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = White,
                                modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
                            )

                            if (riskPoints.isEmpty()) {
                                Text(
                                    "No risks identified",
                                    color = LightGray,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }

                            riskPoints.forEach { point ->
                                ResultCard(
                                    title = "Risk",
                                    clause = point.clause,
                                    details = point.risk,
                                    cardColor = Color(0xFF492A2A),
                                    iconColor = Color(0xFFE57373)
                                )
                            }
                        }

                        // Recommendations section
                        item {
                            Text(
                                "Recommendations",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = White,
                                modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
                            )

                            if (recommendationPoints.isEmpty()) {
                                Text(
                                    "No recommendations available",
                                    color = LightGray,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }

                            recommendationPoints.forEach { point ->
                                RecommendationCard(
                                    clause = point.clause,
                                    reason = point.reason,
                                    suggestedRewrite = point.suggestedRewrite
                                )
                            }
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

@Composable
fun ResultCard(
    title: String,
    clause: String,
    details: String,
    cardColor: Color,
    iconColor: Color
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
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
                        .background(iconColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = if (title == "Strength") painterResource(id = R.drawable.ic_check) else painterResource(id = R.drawable.ic_shield),
                        contentDescription = title,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = title,
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
                clause,
                color = White,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                if (title == "Strength") "Reason:" else "Risk:",
                fontWeight = FontWeight.Bold,
                color = White.copy(alpha = 0.8f),
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                details,
                color = White
            )
        }
    }
}

@Composable
fun RecommendationCard(
    clause: String,
    reason: String,
    suggestedRewrite: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF494A2A)),
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
                        .background(Color(0xFFFFEB3B).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_document),
                        contentDescription = "Recommendation",
                        tint = Color(0xFFFFEB3B),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Recommendation",
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
                clause,
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
                reason,
                color = White,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                "Suggested Rewrite:",
                fontWeight = FontWeight.Bold,
                color = White.copy(alpha = 0.8f),
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2A3548)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    suggestedRewrite,
                    color = GoldAccent,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewResultCard() {
    LegalGPTTheme { // Ensure you are using your app's theme
        ResultCard(
            title = "Strength",
            clause = "This clause ensures the agreement is enforceable under current laws.",
            details = "This clause is beneficial and does not pose any legal risks.",
            cardColor = Color(0xFF1A2538), // Dark background color
            iconColor = GoldAccent // Icon color, using your defined accent color
        )
    }
}
