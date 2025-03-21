package com.example.snakegame

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
/**
 * Начальный экран приложения "Змейка".
 * Отображает логотип игры, название и кнопки для начала игры, перехода к настройкам и выхода.
 *
 * @param onStartGame Обратный вызов, вызываемый при нажатии кнопки "Начать игру".
 * @param onSettings Обратный вызов, вызываемый при нажатии кнопки "Настройки".
 * @param onExit Обратный вызов, вызываемый при нажатии кнопки "Выход".
 */
fun StartScreen(
    onStartGame: () -> Unit,
    onSettings: () -> Unit,
    onExit: () -> Unit
) {
    // Вертикальная компоновка для размещения всех элементов начального экрана
    Column(
        modifier = Modifier
            .fillMaxSize() // Занимает всю доступную площадь экрана
            .padding(16.dp), // Внутренние отступы по всему периметру
        horizontalAlignment = Alignment.CenterHorizontally, // Выравнивание по центру по горизонтали
        verticalArrangement = Arrangement.Center // Выравнивание по центру по вертикали
    ) {
        // Логотип игры (в данном случае используется эмодзи вместо изображения)
        Text(
            text = "🐍",
            fontSize = 100.sp, // Большой размер шрифта для логотипа
            modifier = Modifier.padding(16.dp) // Отступы вокруг логотипа
        )

        // Пространство между логотипом и названием
        Spacer(modifier = Modifier.height(32.dp))

        // Название игры
        Text(
            text = "Змейка",
            fontSize = 32.sp, // Размер шрифта для названия
            fontWeight = FontWeight.Bold // Жирный шрифт для акцента
        )

        // Пространство перед кнопками
        Spacer(modifier = Modifier.height(64.dp))

        // Кнопка "Начать игру"
        Button(
            onClick = onStartGame, // Действие при нажатии
            modifier = Modifier
                .fillMaxWidth() // Занимает всю ширину с учетом отступов
                .padding(horizontal = 32.dp, vertical = 8.dp) // Отступы вокруг кнопки
        ) {
            Text(
                text = "Начать игру",
                fontSize = 18.sp // Размер шрифта текста кнопки
            )
        }

        // Кнопка "Настройки"
        Button(
            onClick = onSettings,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Настройки",
                fontSize = 18.sp
            )
        }

        // Кнопка "Выход"
        Button(
            onClick = onExit,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Выход",
                fontSize = 18.sp
            )
        }
    }
}