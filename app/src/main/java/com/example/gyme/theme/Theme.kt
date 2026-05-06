package com.example.gyme.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

@Composable
fun GymeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary   = GymePrimary,
            secondary = GymePrimaryLight,
            background = GymeBackground,
            surface    = GymeDarkSurface
        ),
        typography = Typography,
        content    = content
    )
}