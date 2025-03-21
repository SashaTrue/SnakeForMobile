package com.example.snakegame

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
/**
 * Экран настроек приложения "Змейка".
 * Предоставляет пользователю интерфейс для изменения уровня сложности игры,
 * включения/выключения звука и вибрации, а также возврата на предыдущий экран.
 *
 * @param onBack Обратный вызов, вызываемый при нажатии кнопки "Назад" для возврата на предыдущий экран.
 * @param currentDifficulty Текущий уровень сложности игры, передаваемый извне.
 *                          По умолчанию используется [GameDifficulty.NORMAL].
 * @param onDifficultyChanged Обратный вызов, вызываемый при выборе нового уровня сложности,
 *                            передающий выбранное значение типа [GameDifficulty].
 */
fun SettingsScreen(
    onBack: () -> Unit,
    currentDifficulty: GameDifficulty = GameDifficulty.NORMAL,
    onDifficultyChanged: (GameDifficulty) -> Unit
) {
    // Локальное состояние для отслеживания текущего уровня сложности
    var difficultyLevel by remember { mutableStateOf(currentDifficulty) }
    // Локальное состояние для управления включением/выключением звука
    var soundEnabled by remember { mutableStateOf(true) }
    // Локальное состояние для управления включением/выключением вибрации
    var vibrationEnabled by remember { mutableStateOf(true) }
    // Локальное состояние для отображения/скрытия информации об уровнях сложности
    var showDifficultyInfo by remember { mutableStateOf(false) }

    // Основной контейнер экрана с градиентным фоном и отступами
    Box(
        modifier = Modifier
            .fillMaxSize() // Занимает всю доступную площадь экрана
            .background(
                Brush.verticalGradient( // Градиентный фон от светло-зеленого к темно-зеленому
                    colors = listOf(
                        Color(0xFF388E3C), // Верхний цвет градиента
                        Color(0xFF1B5E20)  // Нижний цвет градиента
                    )
                )
            )
            .padding(16.dp) // Внутренние отступы по всему периметру
    ) {
        // Вертикальная компоновка для размещения всех элементов интерфейса
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally // Выравнивание по центру по горизонтали
        ) {
            // Заголовок экрана "Настройки"
            Text(
                text = "Настройки",
                fontSize = 36.sp, // Размер шрифта для выделения заголовка
                fontWeight = FontWeight.Bold, // Жирный шрифт для акцента
                color = Color.White, // Белый цвет текста для контраста с фоном
                modifier = Modifier.padding(vertical = 24.dp) // Отступы сверху и снизу
            )

            // Пространство между заголовком и основным контентом
            Spacer(modifier = Modifier.height(16.dp))

            // Поверхность для группировки настроек с тенью и скругленными углами
            Surface(
                modifier = Modifier
                    .fillMaxWidth() // Занимает всю ширину экрана
                    .padding(8.dp), // Отступы вокруг поверхности
                shadowElevation = 8.dp, // Тень для эффекта поднятия над фоном
                shape = RoundedCornerShape(16.dp), // Скругленные углы для эстетики
                color = Color.White // Белый фон для контраста с градиентом
            ) {
                // Внутренняя колонка для размещения элементов настроек
                Column(
                    modifier = Modifier.padding(24.dp) // Внутренние отступы внутри поверхности
                ) {
                    // Заголовок раздела "Уровень сложности" с кнопкой информации
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween, // Равномерное распределение элементов
                        verticalAlignment = Alignment.CenterVertically // Выравнивание по центру по вертикали
                    ) {
                        Text(
                            text = "Уровень сложности",
                            fontSize = 20.sp, // Размер шрифта для заголовка раздела
                            fontWeight = FontWeight.Medium, // Средний вес шрифта
                            color = Color.DarkGray // Темно-серый цвет для читаемости
                        )

                        // Кнопка для отображения информации об уровнях сложности
                        IconButton(
                            onClick = { showDifficultyInfo = !showDifficultyInfo } // Переключение видимости информации
                        ) {
                            Text(
                                text = "?", // Символ вопроса как индикатор информации
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2196F3) // Синий цвет для выделения
                            )
                        }
                    }

                    // Анимированное отображение информации об уровнях сложности
                    AnimatedVisibility(
                        visible = showDifficultyInfo, // Показывать, если showDifficultyInfo = true
                        enter = fadeIn(), // Анимация появления
                        exit = fadeOut() // Анимация исчезновения
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp)) // Светло-зеленый фон с углами
                                .padding(12.dp) // Внутренние отступы
                        ) {
                            Text(
                                text = "• Легкий: Медленная скорость, короткая змейка",
                                fontSize = 14.sp,
                                color = Color.DarkGray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "• Средний: Стандартная скорость, средняя длина",
                                fontSize = 14.sp,
                                color = Color.DarkGray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "• Сложный: Высокая скорость, препятствия",
                                fontSize = 14.sp,
                                color = Color.DarkGray
                            )
                        }
                    }

                    // Пространство перед карточками выбора сложности
                    Spacer(modifier = Modifier.height(16.dp))

                    // Горизонтальная компоновка для карточек выбора сложности
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween // Равномерное распределение карточек
                    ) {
                        // Карточка для уровня "Легкий"
                        DifficultyCard(
                            title = "Легкий",
                            color = Color(0xFF4CAF50), // Зеленый цвет для легкого уровня
                            isSelected = difficultyLevel == GameDifficulty.EASY, // Выделение, если выбрано
                            onClick = {
                                difficultyLevel = GameDifficulty.EASY // Установка уровня
                                onDifficultyChanged(GameDifficulty.EASY) // Передача изменения
                            }
                        )

                        // Карточка для уровня "Средний"
                        DifficultyCard(
                            title = "Средний",
                            color = Color(0xFFFFC107), // Желтый цвет для среднего уровня
                            isSelected = difficultyLevel == GameDifficulty.NORMAL,
                            onClick = {
                                difficultyLevel = GameDifficulty.NORMAL
                                onDifficultyChanged(GameDifficulty.NORMAL)
                            }
                        )

                        // Карточка для уровня "Сложный"
                        DifficultyCard(
                            title = "Сложный",
                            color = Color(0xFFF44336), // Красный цвет для сложного уровня
                            isSelected = difficultyLevel == GameDifficulty.HARD,
                            onClick = {
                                difficultyLevel = GameDifficulty.HARD
                                onDifficultyChanged(GameDifficulty.HARD)
                            }
                        )
                    }

                    // Пространство перед разделителем
                    Spacer(modifier = Modifier.height(24.dp))

                    // Горизонтальная линия-разделитель
                    Divider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = Color.LightGray // Светло-серый цвет для разделителя
                    )

                    // Настройка звука
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Звук",
                            fontSize = 18.sp,
                            color = Color.DarkGray,
                            modifier = Modifier.weight(1f) // Занимает доступное пространство
                        )
                        Switch(
                            checked = soundEnabled, // Состояние переключателя
                            onCheckedChange = { soundEnabled = it }, // Обновление состояния
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF4CAF50), // Зеленый цвет при активации
                                checkedTrackColor = Color(0xFF81C784) // Светло-зеленый трек
                            )
                        )
                    }

                    // Настройка вибрации
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Вибрация",
                            fontSize = 18.sp,
                            color = Color.DarkGray,
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = vibrationEnabled,
                            onCheckedChange = { vibrationEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF4CAF50),
                                checkedTrackColor = Color(0xFF81C784)
                            )
                        )
                    }
                }
            }

            // Пространство для выравнивания кнопки "Назад" внизу
            Spacer(modifier = Modifier.weight(1f))

            // Кнопка "Назад" для возврата на предыдущий экран
            Button(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .height(56.dp), // Фиксированная высота кнопки
                shape = RoundedCornerShape(28.dp), // Скругленные углы
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFFFFF) // Белый фон кнопки
                )
            ) {
                Text(
                    text = "Назад",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32) // Зеленый цвет текста
                )
            }
        }
    }
}

/**
 * Вспомогательная composable-функция для отображения карточки выбора уровня сложности.
 * Используется для визуального представления вариантов сложности с возможностью выбора.
 *
 * @param title Название уровня сложности (например, "Легкий", "Средний", "Сложный").
 * @param color Цвет фона карточки, соответствующий уровню сложности.
 * @param isSelected Флаг, указывающий, выбрана ли эта карточка в данный момент.
 * @param onClick Обратный вызов, вызываемый при нажатии на карточку для выбора уровня.
 */
@Composable
fun DifficultyCard(
    title: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Контейнер для карточки с заданными размерами и стилями
    Box(
        modifier = Modifier
            .width(80.dp) // Фиксированная ширина карточки
            .height(80.dp) // Фиксированная высота карточки
            .clip(RoundedCornerShape(12.dp)) // Скругленные углы
            .background(
                color.copy(alpha = if (isSelected) 1f else 0.6f) // Прозрачность при выборе
            )
            .border(
                width = if (isSelected) 3.dp else 0.dp, // Белая рамка при выборе
                color = Color.White,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick), // Обработка нажатия
        contentAlignment = Alignment.Center // Выравнивание текста по центру
    ) {
        Text(
            text = title,
            color = Color.White, // Белый цвет текста для контраста
            fontWeight = FontWeight.Bold, // Жирный шрифт для акцента
            fontSize = 16.sp // Размер шрифта текста
        )
    }
}