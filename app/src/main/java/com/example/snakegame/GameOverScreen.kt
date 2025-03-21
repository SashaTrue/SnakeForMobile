package com.example.snakegame

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
/**
 * Экран окончания игры приложения "Змейка".
 * Отображает сообщение об окончании игры, финальный счет и кнопки для повторной игры или возврата в меню.
 *
 * @param score Финальный счет игры, отображаемый на экране.
 * @param onTryAgain Обратный вызов, вызываемый при нажатии кнопки "Попробовать снова" для начала новой игры.
 * @param onBackToMenu Обратный вызов, вызываемый при нажатии кнопки "Вернуться в меню" для возврата на начальный экран.
 */
fun GameOverScreen(
    score: Int,
    onTryAgain: () -> Unit,
    onBackToMenu: () -> Unit
) {
    // Вертикальная компоновка для размещения всех элементов экрана окончания игры
    Column(
        modifier = Modifier
            .fillMaxSize() // Занимает всю доступную площадь экрана
            .padding(16.dp), // Внутренние отступы по всему периметру
        horizontalAlignment = Alignment.CenterHorizontally, // Центрирование по горизонтали
        verticalArrangement = Arrangement.Center // Центрирование по вертикали
    ) {
        // Текст с сообщением об окончании игры
        Text(
            text = "Игра окончена!",
            fontSize = 32.sp, // Увеличенный размер шрифта для акцента
            fontWeight = FontWeight.Bold, // Жирный шрифт для выделения
            textAlign = TextAlign.Center // Центрирование текста
        )

        // Пространство между сообщением и счетом
        Spacer(modifier = Modifier.height(32.dp))

        // Текст с финальным счетом игрока
        Text(
            text = "Ваш счет: $score",
            fontSize = 24.sp, // Размер шрифта для читаемости
            textAlign = TextAlign.Center // Центрирование текста
        )

        // Пространство перед кнопками управления
        Spacer(modifier = Modifier.height(64.dp))

        // Кнопка для повторного запуска игры
        Button(
            onClick = onTryAgain, // Действие при нажатии кнопки
            modifier = Modifier
                .fillMaxWidth() // Занимает максимальную ширину с учетом отступов
                .padding(horizontal = 32.dp, vertical = 8.dp) // Отступы для эстетики
        ) {
            Text(
                text = "Попробовать снова",
                fontSize = 18.sp // Размер шрифта текста кнопки
            )
        }

        // Кнопка для возврата в главное меню
        Button(
            onClick = onBackToMenu, // Действие при нажатии кнопки
            modifier = Modifier
                .fillMaxWidth() // Занимает максимальную ширину с учетом отступов
                .padding(horizontal = 32.dp, vertical = 8.dp) // Отступы для эстетики
        ) {
            Text(
                text = "Вернуться в меню",
                fontSize = 18.sp // Размер шрифта текста кнопки
            )
        }
    }
}