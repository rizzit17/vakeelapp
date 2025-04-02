package com.example.legalgpt

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Using the same color constants defined earlier
// val DarkBlueBackground = Color(0xFF0D1526)
// val GoldAccent = Color(0xFFB67F20)
// val White = Color.White
// val LightGray = Color(0xBBCCCCCC)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalGPTAppBar(onContactClick: () -> Unit) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Logo to the left of the title
                Image(
                    painter = painterResource(id = R.drawable.scale_white),
                    contentDescription = "Vakeel.ai Logo",
                    modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Vakeel",
                    color = GoldAccent,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                // Title with gold color
                Text(
                    text = ".ai",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        actions = {
            IconButton(onClick = onContactClick) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Contact Us",
                    tint = GoldAccent
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = DarkBlueBackground,
            titleContentColor = White,
            actionIconContentColor = GoldAccent
        )
    )
}