package com.example.legalgpt

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.legalgpt.ui.screens.ComplianceIntelligenceScreen
import com.example.legalgpt.viewmodels.OcrViewModel

/*import com.example.legalgpt.ui.screens.ComplianceIntelligenceScreen*/




@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = "welcome",
        modifier = modifier
    ) {
        composable("welcome") {
            WelcomeScreen(navController)
        }
        composable("home") {
            HomeScreen(navController)
        }
        composable("risk_detection") {
            RiskDetectionScreen(navController)
        }
        composable("compliance_intelligence") {
            ComplianceIntelligenceScreen(navController)
        }
        composable("contract_generation") {
            ContractGenerationScreen(navController)
        }

        composable("contact") {
            ContactUsPage()
        }
        composable("legal_chat") {
            LegalChatScreen()
        }
        composable("scan_document") {
            OcrScreen(viewModel = OcrViewModel())
        }
    }
}
