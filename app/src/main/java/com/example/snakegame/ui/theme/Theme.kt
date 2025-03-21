package com.example.snakegame.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Светлая цветовая схема для приложения "Змейка".
 * Используется по умолчанию или когда система находится в светлом режиме.
 */
private val LightColors = lightColorScheme(
    primary = Color(0xFF4CAF50), // Основной цвет (зеленый)
    onPrimary = Color.White,     // Цвет текста на основном фоне
    secondary = Color(0xFF8BC34A), // Вторичный цвет (светло-зеленый)
    background = Color.White     // Цвет фона
)

/**
 * Темная цветовая схема для приложения "Змейка".
 * Используется, когда система находится в темном режиме.
 */
private val DarkColors = darkColorScheme(
    primary = Color(0xFF4CAF50),   // Основной цвет (зеленый)
    onPrimary = Color.Black,       // Цвет текста на основном фоне
    secondary = Color(0xFF8BC34A), // Вторичный цвет (светло-зеленый)
    background = Color(0xFF121212) // Цвет фона (темно-серый)
)

/**
 * Тема приложения "Змейка".
 * Определяет цветовую схему в зависимости от системных настроек или переданного параметра.
 *
 * @param darkTheme Флаг, указывающий, использовать ли темную тему. По умолчанию используется системное значение.
 * @param content Компонент Compose, который будет отображаться с заданной темой.
 */
@Composable
fun SnakeGameTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Выбор цветовой схемы в зависимости от параметра darkTheme
    val colors = if (darkTheme) DarkColors else LightColors

    // Применение выбранной цветовой схемы к компонентам Compose
    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}