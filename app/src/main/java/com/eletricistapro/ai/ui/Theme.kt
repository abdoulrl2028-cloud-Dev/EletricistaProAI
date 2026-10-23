package com.eletricistapro.ai.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Ink = Color(0xFF0B1F33)
private val Amber = Color(0xFFB45309)
private val Paper = Color(0xFFF3F6FA)

private val Colors = lightColorScheme(
    primary = Ink,
    onPrimary = Color.White,
    secondary = Amber,
    onSecondary = Color.White,
    background = Paper,
    surface = Color.White,
    onBackground = Ink,
    onSurface = Ink,
)

@Composable
fun EletricistaTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Colors, content = content)
}
