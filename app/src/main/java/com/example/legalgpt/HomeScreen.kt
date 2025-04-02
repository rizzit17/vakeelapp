package com.example.legalgpt

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.legalgpt.ui.theme.LegalGPTTheme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock



@Composable
fun HomeScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlueBackground)
    ) {
        // Making the entire content scrollable by placing everything in the LazyColumn
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Section is now part of the LazyColumn
            item {
                Spacer(modifier = Modifier.height(8.dp))

                // Title with themed colors
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column {
                        Text(
                            text = "Legal Assistance,",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = White
                        )
                        Text(
                            text = "Simplified",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GoldAccent
                        )
                    }
                }

                // Subtitle
                Text(
                    text = "Your AI-powered legal companion for smart risk management and compliance",
                    fontSize = 16.sp,
                    color = LightGray,
                    lineHeight = 24.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Feature cards remain as individual items
            item {
                FeatureCard(
                    title = "AI-Powered Risk Detection",
                    description = "Identify potential legal risks instantly",
                    icon = painterResource(id = R.drawable.ic_shield),
                    onClick = { navController.navigate("risk_detection") }
                )
            }
            item {
                FeatureCard(
                    title = "Compliance Intelligence",
                    description = "Stay compliant with updated legal rules",
                    icon = painterResource(id = R.drawable.ic_check),
                    onClick = { navController.navigate("compliance_intelligence") }
                )
            }
            item {
                FeatureCard(
                    title = "Instant Contract Generation",
                    description = "Generate contracts instantly",
                    icon = painterResource(id = R.drawable.ic_document),
                    onClick = {
                        try {
                            navController.navigate("contract_generation")
                            Log.d("Navigation", "Successfully navigated to contract_generation")
                        } catch (e: Exception) {
                            Log.e("Navigation", "Error navigating to contract_generation: ${e.message}")
                            e.printStackTrace()
                        }
                    }
                )
            }
            item {
                FeatureCard(
                    title = "Legal Conversation",
                    description = "Analyze your legal conversation",
                    icon = painterResource(id = R.drawable.ic_chat),
                    onClick = { navController.navigate("legal_chat") }
                )
            }
            item {
                FeatureCard(
                    title = "Document OCR",
                    description = "Scan your documents and check for good/bad clauses",
                    icon = painterResource(id = R.drawable.scan),
                    onClick = { navController.navigate("scan_document") }
                )
            }
        }
    }
}
@Composable
fun FeatureCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.painter.Painter,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    // Dark card background with slight transparency
    val cardBackgroundColor = Color(0xFF1A2538)

    val textColor = if (enabled) {
        White
    } else {
        White.copy(alpha = 0.5f)
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circular background for icon with gold accent
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(GoldAccent.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = LightGray
                )
            }

            if (!enabled) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF2A3548)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = LightGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
