package com.example.snakegame.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Набор типографических стилей для приложения "Змейка".
 * Определяет стили текста, используемые в интерфейсе.
 */
val Typography = Typography(
    // Стиль для основного текста (например, для кнопок, описаний)
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default, // Используется шрифт по умолчанию
        fontWeight = FontWeight.Normal,  // Обычный вес шрифта
        fontSize = 16.sp,                // Размер шрифта 16 sp
        lineHeight = 24.sp,              // Высота строки 24 sp
        letterSpacing = 0.5.sp           // Межбуквенное расстояние 0.5 sp
    )
)