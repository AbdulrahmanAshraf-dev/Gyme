package com.example.gyme.core.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gyme.theme.GymeCardBg
import com.example.gyme.theme.GymePrimary
import com.example.gyme.theme.GymeTextPrimary
import com.example.gyme.theme.GymeTextSecondary

@Composable
fun GymeStatCard(
    title: String,
    value: String,
    subtext: String,
    icon: ImageVector,
    iconColor: Color,
    iconBgColor: Color,
    isPositive: Boolean? = null,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = GymeCardBg),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(20.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GymeTextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(value, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = GymeTextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                val trendColor = when (isPositive) {
                    true -> GymePrimary
                    false -> Color(0xFFEF4444)
                    else -> GymeTextSecondary
                }
                Text(subtext, fontSize = 14.sp, color = trendColor)
            }
            Surface(
                shape = CircleShape,
                color = iconBgColor,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
