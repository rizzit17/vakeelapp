package com.example.legalgpt

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.navigation.NavController
import com.example.legalgpt.models.ContractResponse
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContractGenerationScreen(navController: NavController) {
    var contract_type by remember { mutableStateOf("noncompete") }
    var party_a by remember { mutableStateOf("Sarthak") }
    var party_b by remember { mutableStateOf("Bhumit") }
    var duration by remember { mutableStateOf("2 months") }
    var clause_query by remember { mutableStateOf("termination") }
    var jurisdiction by remember { mutableStateOf("New Delhi") }

    var property_address by remember { mutableStateOf("") }
    var position by remember { mutableStateOf("") }
    var goods_description by remember { mutableStateOf("") }
    var scope by remember { mutableStateOf("") }

    var contractResponse by remember { mutableStateOf<ContractResponse?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

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
                            "Contract Generator",
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
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header section
                Text(
                    text = "Generate Custom,",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = White
                )
                Text(
                    text = "Legal Contracts",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = GoldAccent,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Subtitle
                Text(
                    text = "Fill the form to generate a legally formatted, Indian-law compliant contract",
                    fontSize = 16.sp,
                    color = LightGray,
                    lineHeight = 24.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Form Card
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2538)),
                    modifier = Modifier
                        .fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Contract type dropdown
                        Text(
                            "Contract Type",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = LightGray
                        )
                        StyledDropdownField(
                            value = contract_type,
                            onValueChange = { contract_type = it },
                            options = listOf("nda", "contractor", "sla", "partnership",
                                "sales", "employment", "lease", "mou", "noncompete")
                        )

                        // Regular input fields
                        StyledInputField(label = "Party A", value = party_a, onValueChange = { party_a = it })
                        StyledInputField(label = "Party B", value = party_b, onValueChange = { party_b = it })
                        StyledInputField(label = "Duration (e.g., 12 months, 2 years)", value = duration, onValueChange = { duration = it })
                        StyledInputField(label = "Clause Topic (e.g., Confidentiality, Termination)", value = clause_query, onValueChange = { clause_query = it })
                        StyledInputField(label = "Jurisdiction", value = jurisdiction, onValueChange = { jurisdiction = it })

                        // Conditional fields based on contract type
                        when (contract_type) {
                            "lease" -> StyledInputField(label = "Property Address", value = property_address, onValueChange = { property_address = it })
                            "employment" -> StyledInputField(label = "Position", value = position, onValueChange = { position = it })
                            "sales" -> StyledInputField(label = "Goods Description", value = goods_description, onValueChange = { goods_description = it })
                            "noncompete" -> StyledInputField(label = "Scope of Restriction", value = scope, onValueChange = { scope = it })
                        }

                        // Generate button
                        Button(
                            onClick = {
                                isLoading = true
                                error = null

                                val requestBody = buildContractRequest(
                                    contract_type, party_a, party_b, duration, clause_query,
                                    jurisdiction, property_address, position, goods_description, scope
                                )

                                coroutineScope.launch {
                                    try {
                                        val response = RetrofitClient.generateContract(requestBody)
                                        if (response.isSuccessful) {
                                            contractResponse = response.body()
                                        } else {
                                            error = "Error: ${response.code()} - ${response.message()}"
                                        }
                                    } catch (e: Exception) {
                                        error = e.message ?: "Unknown error occurred"
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldAccent,
                                disabledContainerColor = GoldAccent.copy(alpha = 0.5f)
                            ),
                            enabled = !isLoading && party_a.isNotBlank() && party_b.isNotBlank()
                                    && duration.isNotBlank() && clause_query.isNotBlank(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = DarkBlueBackground,
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    "Generate Contract",
                                    color = DarkBlueBackground,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Error display
                error?.let {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF492A2A)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_shield),
                                contentDescription = "Error",
                                tint = Color(0xFFE57373),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = it,
                                color = Color(0xFFE57373)
                            )
                        }
                    }
                }

                // Contract response
                contractResponse?.let { response ->
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2538)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp)
                        ) {
                            // Header with icon
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(GoldAccent.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_document),
                                        contentDescription = "Document",
                                        tint = GoldAccent,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Text(
                                    text = "Generated Contract",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = White
                                )
                            }

                            // Status message
                            Text(
                                text = response.message,
                                color = LightGray,
                                modifier = Modifier.padding(bottom = 20.dp)
                            )

                            // View PDF button
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(response.pdf_url))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GoldAccent
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_document),
                                        contentDescription = "Document",
                                        tint = DarkBlueBackground
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "View PDF",
                                        color = DarkBlueBackground,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Bottom spacing
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun StyledInputField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = LightGray,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF2A3548),
                unfocusedContainerColor = Color(0xFF2A3548),
                disabledContainerColor = Color(0xFF2A3548),
                focusedTextColor = White,
                unfocusedTextColor = White,
                cursorColor = GoldAccent,
                focusedIndicatorColor = GoldAccent,
                unfocusedIndicatorColor = Color(0xFF3A4559)
            )
        )
    }
}

@Composable
fun StyledDropdownField(value: String, onValueChange: (String) -> Unit, options: List<String>) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            shape = RoundedCornerShape(12.dp),
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Dropdown Arrow",
                    tint = GoldAccent
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF2A3548),
                unfocusedContainerColor = Color(0xFF2A3548),
                disabledContainerColor = Color(0xFF2A3548),
                focusedTextColor = White,
                unfocusedTextColor = White,
                cursorColor = GoldAccent,
                focusedIndicatorColor = GoldAccent,
                unfocusedIndicatorColor = Color(0xFF3A4559)
            )
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { expanded = !expanded }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(Color(0xFF2A3548))
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    },
                    text = {
                        Text(
                            text = option,
                            color = White
                        )
                    },
                    colors = MenuDefaults.itemColors(
                        textColor = White
                    )
                )
            }
        }
    }
}

fun buildContractRequest(
    contract_type: String,
    party_a: String,
    party_b: String,
    duration: String,
    clause_query: String,
    jurisdiction: String,
    property_address: String? = null,
    position: String? = null,
    goods_description: String? = null,
    scope: String? = null
): ContractRequest {
    return ContractRequest(
        contract_type = contract_type,
        party_a = party_a,
        party_b = party_b,
        duration = duration,
        clause_query = clause_query,
        jurisdiction = jurisdiction,
        property_address = property_address,
        position = position,
        goods_description = goods_description,
        scope = scope
    )
}

data class ContractRequest(
    val contract_type: String,
    val party_a: String,
    val party_b: String,
    val duration: String,
    val clause_query: String,
    val jurisdiction: String,
    val property_address: String? = null,
    val position: String? = null,
    val goods_description: String? = null,
    val scope: String? = null
)