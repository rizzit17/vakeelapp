package com.example.legalgpt

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ContactUsPage() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlueBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // Header Section
            Spacer(modifier = Modifier.height(8.dp))

            // Title with themed colors
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column {
                    Text(
                        text = "Contact",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = White
                    )
                    Text(
                        text = "Us",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GoldAccent
                    )
                }
            }

            // Subtitle
            Text(
                text = "Get in touch with our legal experts",
                fontSize = 16.sp,
                color = LightGray,
                lineHeight = 24.sp,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Contact Cards
            ContactCard(
                icon = Icons.Default.Phone,
                title = "Call Us",
                info = "+91 909384993",
                description = "Available: Never"
            )

            Spacer(modifier = Modifier.height(16.dp))

            ContactCard(
                icon = Icons.Default.Email,
                title = "Email Us",
                info = "contact@vakeel.ai",
                description = "We typically never respond"
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Additional contact info card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2538)),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Our Office",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "R-block/G-Block(girls hostel)",
                        fontSize = 14.sp,
                        color = LightGray
                    )

                    Text(
                        text = "VIT Vellore",
                        fontSize = 14.sp,
                        color = LightGray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Business Hours: 9am-5pm, Monday-Friday",
                        fontSize = 14.sp,
                        color = LightGray
                    )
                }
            }
        }
    }
}

@Composable
fun ContactCard(
    icon: ImageVector,
    title: String,
    info: String,
    description: String
) {
    // Dark card background with slight transparency (same as FeatureCard)
    val cardBackgroundColor = Color(0xFF1A2538)

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
        modifier = Modifier.fillMaxWidth(),
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
                    imageVector = icon,
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
                    color = White
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = info,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = GoldAccent
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = LightGray
                )
            }
        }
    }
}