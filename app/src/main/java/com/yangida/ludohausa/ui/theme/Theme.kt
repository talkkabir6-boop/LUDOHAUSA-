package com.yangida.ludohausa.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val LudoDarkBlue = Color(0xFF1565C0)
val LudoGold = Color(0xFFFFD700)

private val ColorScheme = lightColorScheme(
    primary = LudoDarkBlue,
    secondary = LudoGold,
    background = Color(0xFFE3F2FD)
)

@Composable
fun LudoHausaTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = ColorScheme, content = content)
}