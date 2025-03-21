package com.example.snakegame

import android.content.Context
import android.os.Build
import android.os.Vibrator
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.random.Random

// Цветовые константы, используемые в игре

/**
 * Темно-зеленый для головы змейки.
 */
val DarkGreen = Color(0xFF1B5E20) // Темно-зеленый для головы змейки

/**
 * Средний зеленый для тела змейки.
 */
val MediumGreen = Color(0xFF2E7D32)

/**
 * Светло-зеленый для хвоста змейки.
 */
val LightGreen = Color(0xFF43A047)

/**
 * Красный для обычной еды.
 */
val AppleRed = Color(0xFFD32F2F)

/**
 * Золотой для специальной еды.
 */
val GoldenFood = Color(0xFFFFC107)

/**
 * Перечисление типов еды в игре.
 * - REGULAR: обычная еда, добавляет стандартное количество очков.
 * - GOLDEN: золотая еда, добавляет больше очков.
 */
enum class FoodType {
    REGULAR,
    GOLDEN
}

/**
 * Класс данных для представления еды на игровом поле.
 *
 * @param position Позиция еды на сетке.
 * @param type Тип еды (обычная или золотая).
 */
data class Food(
    val position: Position,
    val type: FoodType = FoodType.REGULAR
)

/**
 * Класс данных для представления позиции на сетке игрового поля.
 *
 * @param x Координата по оси X.
 * @param y Координата по оси Y.
 */
data class Position(val x: Int, val y: Int)

/**
 * Перечисление направлений движения змейки.
 * - UP: вверх.
 * - DOWN: вниз.
 * - LEFT: влево.
 * - RIGHT: вправо.
 * - NONE: отсутствие движения (начальное состояние).
 */
enum class Direction {
    UP, DOWN, LEFT, RIGHT, NONE
}

/**
 * Перечисление уровней сложности игры.
 *
 * @param speedMultiplier Множитель скорости движения змейки.
 * @param foodFrequency Частота появления золотой еды (в процентах).
 */
enum class GameDifficulty(val speedMultiplier: Float, val foodFrequency: Int) {
    EASY(0.7f, 30), // Легкий уровень: медленная скорость, частая золотая еда
    NORMAL(1.0f, 20), // Средний уровень: стандартная скорость и частота
    HARD(1.3f, 10) // Сложный уровень: высокая скорость, редкая золотая еда
}

@Composable
        /**
         * Основной экран игры "Змейка".
         * Управляет игровым процессом, отображает поле, змейку, еду, препятствия и интерфейс.
         *
         * @param onGameOver Обратный вызов, вызываемый при окончании игры с передачей финального счета.
         * @param difficulty Уровень сложности игры, по умолчанию [GameDifficulty.NORMAL].
         * @exception VibrationUnavailable Если вибрация недоступна на устройстве, эффект вибрации при поедании еды не сработает, но игра продолжит функционировать без изменений.
         * @result После поедания обычной еды (🟢) змейка вырастет на 1 сегмент, а счет увеличится на 10 очков. После поедания золотой еды (🟡) змейка вырастет на 1 сегмент, а счет увеличится на 30 очков.
         * @usage
         * 1. Дождитесь окончания обратного отсчета (3-2-1-СТАРТ!).
         * 2. Проведите пальцем по экрану для управления змейкой (вверх, вниз, влево, вправо).
         * 3. Собирайте еду для роста и набора очков.
         * 4. Нажмите "II" для паузы, "▶" для возобновления.
         * @traceability См. `GameScreenUITest` для UI-тестов и `GameLogicTest` для тестов логики.
         * @priority High - Этот экран является ядром игрового процесса, критически важен для опыта пользователя.
         * @verification
         * - Проверка корректности движения змейки в заданном направлении (см. `testMoveSnakeRight` в `GameLogicTest`).
         * - Проверка увеличения счета: +10 для обычной еды, +30 для золотой (см. `GameScreenUITest`).
         */
fun GameScreen(
    onGameOver: (Int) -> Unit,
    difficulty: GameDifficulty = GameDifficulty.NORMAL
) {
    val gridSize = 20 // Размер сетки игрового поля (20x20 клеток)
    val context = LocalContext.current // Контекст для доступа к системным сервисам (вибрация)

    // Состояния игры
    var score by remember { mutableStateOf(0) } // Текущий счет игрока
    var snake by remember {
        mutableStateOf(
            when (difficulty) {
                GameDifficulty.EASY -> listOf(Position(gridSize / 2, gridSize / 2)) // Начальная длина 1 для легкого уровня
                GameDifficulty.NORMAL -> List(3) { Position(gridSize / 2 - it, gridSize / 2) } // Длина 3 для среднего
                GameDifficulty.HARD -> List(5) { Position(gridSize / 2 - it, gridSize / 2) } // Длина 5 для сложного
            }
        )
    } // Позиции сегментов змейки
    var food by remember { mutableStateOf(generateFood(gridSize, snake, FoodType.REGULAR)) } // Позиция и тип еды
    var direction by remember { mutableStateOf(Direction.NONE) } // Текущее направление движения
    var gameRunning by remember { mutableStateOf(true) } // Состояние активности игры
    var gameSpeed by remember { mutableStateOf(300L) } // Скорость игры в миллисекундах
    var isPaused by remember { mutableStateOf(false) } // Состояние паузы
    var showCountdown by remember { mutableStateOf(true) } // Отображение обратного отсчета
    var countdown by remember { mutableStateOf(3) } // Значение обратного отсчета

    // Препятствия для сложного уровня
    val obstacles = remember {
        if (difficulty == GameDifficulty.HARD) {
            List(5) { Position(Random.nextInt(gridSize), Random.nextInt(gridSize)) }
                .filter { it !in snake && it != food.position } // Препятствия не пересекаются с змейкой и едой
        } else emptyList()
    }

    // Анимация масштаба для золотой еды
    val foodScale by animateFloatAsState(
        targetValue = if (food.type == FoodType.GOLDEN) 1.2f else 1.0f,
        animationSpec = tween(durationMillis = 500),
        label = "foodScale"
    )

    // Эффект для обратного отсчета при запуске игры
    LaunchedEffect(key1 = true) {
        while (countdown > 0) {
            delay(1000) // Задержка 1 секунда
            countdown--
        }
        showCountdown = false
        direction = Direction.RIGHT // Начальное движение вправо
        gameRunning = true
    }

    // Эффект для управления движением змейки
    LaunchedEffect(key1 = Unit) {
        while (true) {
            if (!gameRunning || isPaused || showCountdown) {
                delay(100) // Пауза при неактивной игре
                continue
            }

            val adjustedSpeed = (gameSpeed * difficulty.speedMultiplier).toLong() // Скорость с учетом сложности
            delay(adjustedSpeed)

            if (direction != Direction.NONE) {
                snake = moveSnake(snake, direction, food.position, gridSize, obstacles) { newFoodPosition, points ->
                    val foodType = if (Random.nextInt(100) < difficulty.foodFrequency) FoodType.GOLDEN else FoodType.REGULAR
                    food = Food(newFoodPosition, foodType)
                    val adjustedPoints = if (food.type == FoodType.GOLDEN) points * 3 else points // Золотая еда дает больше очков
                    score += adjustedPoints
                    gameSpeed = maxOf(80L, 300L - (score / 5) * 5) // Увеличение скорости с ростом счета

                    // Вибрация при поедании еды
                    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    if (Build.VERSION.SDK_INT >= 26) {
                        vibrator.vibrate(android.os.VibrationEffect.createOneShot(100, android.os.VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        vibrator.vibrate(100)
                    }
                }

                // Проверка на столкновение
                if (isCollision(snake, gridSize, obstacles)) {
                    gameRunning = false
                    onGameOver(score)
                }
            }
        }
    }

    // Цвета градиента для фона в зависимости от сложности
    val gradientColors = when (difficulty) {
        GameDifficulty.EASY -> listOf(Color(0xFF4CAF50), Color(0xFF81C784)) // Зеленый градиент для легкого уровня
        GameDifficulty.NORMAL -> listOf(Color(0xFFFFC107), Color(0xFFFFECB3)) // Желтый градиент для среднего
        GameDifficulty.HARD -> listOf(Color(0xFFF44336), Color(0xFFEF9A9A)) // Красный градиент для сложного
    }

    // Основной контейнер экрана
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Панель с информацией и кнопкой паузы
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Индикатор уровня сложности
                Box(
                    modifier = Modifier
                        .background(
                            color = when (difficulty) {
                                GameDifficulty.EASY -> Color(0xFF4CAF50)
                                GameDifficulty.NORMAL -> Color(0xFFFFC107)
                                GameDifficulty.HARD -> Color(0xFFF44336)
                            },
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = when (difficulty) {
                            GameDifficulty.EASY -> "Легкий"
                            GameDifficulty.NORMAL -> "Средний"
                            GameDifficulty.HARD -> "Сложный"
                        },
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Отображение счета
                Box(
                    modifier = Modifier
                        .background(Color(0xFF2196F3), RoundedCornerShape(8.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Счет: $score",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Кнопка паузы/возобновления
                Button(
                    onClick = { isPaused = !isPaused },
                    modifier = Modifier.padding(4.dp),
                    shape = CircleShape
                ) {
                    Text(
                        text = if (isPaused) "▶" else "II",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Игровое поле
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f) // Квадратное поле
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.verticalGradient(gradientColors))
                    .border(2.dp, Color(0xFFB0BEC5), RoundedCornerShape(16.dp))
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            if (!isPaused && !showCountdown) {
                                change.consume()
                                val (x, y) = dragAmount
                                when {
                                    abs(x) > abs(y) -> {
                                        if (x > 0 && direction != Direction.LEFT) direction = Direction.RIGHT
                                        else if (x < 0 && direction != Direction.RIGHT) direction = Direction.LEFT
                                    }
                                    else -> {
                                        if (y > 0 && direction != Direction.UP) direction = Direction.DOWN
                                        else if (y < 0 && direction != Direction.DOWN) direction = Direction.UP
                                    }
                                }
                            }
                        }
                    }
            ) {
                // Отрисовка игрового поля
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cellSize = size.width / gridSize

                    // Отрисовка сетки
                    for (i in 0 until gridSize) {
                        for (j in 0 until gridSize) {
                            val isEvenCell = (i + j) % 2 == 0
                            drawRect(
                                color = if (isEvenCell) Color(0xFFD7CCC8) else Color(0xFFEFEBE9),
                                topLeft = Offset(i * cellSize, j * cellSize),
                                size = Size(cellSize, cellSize),
                                style = Stroke(width = 1.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }
                    }

                    // Отрисовка змейки
                    drawSnake(snake, cellSize)

                    // Отрисовка препятствий
                    obstacles.forEach {
                        drawRect(
                            color = Color.Gray,
                            topLeft = Offset(it.x * cellSize, it.y * cellSize),
                            size = Size(cellSize, cellSize)
                        )
                    }

                    // Отрисовка еды
                    val foodPos = food.position
                    val centerX = (foodPos.x * cellSize) + (cellSize / 2)
                    val centerY = (foodPos.y * cellSize) + (cellSize / 2)
                    val radius = (cellSize / 2) * foodScale * 0.8f
                    val foodColor = if (food.type == FoodType.GOLDEN) GoldenFood else AppleRed

                    drawCircle(
                        color = foodColor,
                        center = Offset(centerX, centerY),
                        radius = radius,
                        style = Stroke(width = 2.dp.toPx())
                    )

                    if (food.type == FoodType.REGULAR) {
                        // Стебель для обычной еды
                        drawLine(
                            color = Color(0xFF795548),
                            start = Offset(centerX, centerY - radius),
                            end = Offset(centerX, centerY - radius * 1.3f),
                            strokeWidth = cellSize * 0.1f,
                            cap = StrokeCap.Round
                        )
                    } else {
                        // Блеск для золотой еды
                        drawCircle(
                            color = Color(0xFFFFFFFF),
                            center = Offset(centerX - radius * 0.3f, centerY - radius * 0.3f),
                            radius = radius * 0.15f
                        )
                    }
                }

                // Обратный отсчет
                if (showCountdown) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0x99000000)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (countdown > 0) countdown.toString() else "СТАРТ!",
                            color = Color.White,
                            fontSize = 64.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Экран паузы
                if (isPaused && !showCountdown) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0x99000000)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "ПАУЗА",
                            color = Color.White,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Инструкции по управлению
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Проведите пальцем для управления змейкой\n🟢 - Обычная еда (+10 очков)\n🟡 - Золотая еда (+30 очков)",
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                modifier = Modifier.alpha(0.7f)
            )
        }
    }
}

/**
 * Вспомогательная функция для отрисовки змейки на холсте.
 *
 * @param snake Список позиций сегментов змейки.
 * @param cellSize Размер одной ячейки сетки в пикселях.
 */
private fun DrawScope.drawSnake(snake: List<Position>, cellSize: Float) {
    snake.forEachIndexed { index, position ->
        val centerX = (position.x * cellSize) + (cellSize / 2)
        val centerY = (position.y * cellSize) + (cellSize / 2)
        val segmentIndex = index.toFloat() / snake.size.toFloat()
        val segmentColor = when {
            index == 0 -> DarkGreen // Голова
            segmentIndex < 0.3f -> MediumGreen // Начало тела
            segmentIndex < 0.7f -> MediumGreen // Середина тела
            else -> LightGreen // Хвост
        }
        val snakeRadius = if (index == 0) cellSize * 0.45f else cellSize * 0.4f

        if (index == 0) {
            // Отрисовка головы с градиентом
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(DarkGreen, MediumGreen),
                    center = Offset(centerX, centerY),
                    radius = snakeRadius * 1.2f
                ),
                center = Offset(centerX, centerY),
                radius = snakeRadius
            )
            val eyeOffset = snakeRadius * 0.4f
            // Глаза
            drawCircle(color = Color.White, center = Offset(centerX - eyeOffset, centerY - eyeOffset), radius = snakeRadius * 0.2f)
            drawCircle(color = Color.White, center = Offset(centerX + eyeOffset, centerY - eyeOffset), radius = snakeRadius * 0.2f)
            drawCircle(color = Color.Black, center = Offset(centerX - eyeOffset, centerY - eyeOffset), radius = snakeRadius * 0.1f)
            drawCircle(color = Color.Black, center = Offset(centerX + eyeOffset, centerY - eyeOffset), radius = snakeRadius * 0.1f)
        } else {
            // Отрисовка тела
            drawCircle(color = segmentColor, center = Offset(centerX, centerY), radius = snakeRadius)
            drawCircle(
                color = Color.White.copy(alpha = 0.3f),
                center = Offset(centerX - snakeRadius * 0.3f, centerY - snakeRadius * 0.3f),
                radius = snakeRadius * 0.2f
            )
        }
    }
}

/**
 * Функция для перемещения змейки в заданном направлении.
 *
 * @param snake Текущий список позиций змейки.
 * @param direction Направление движения.
 * @param foodPosition Позиция еды.
 * @param gridSize Размер сетки.
 * @param obstacles Список позиций препятствий.
 * @param onFoodEaten Обратный вызов при поедании еды с новой позицией еды и очками.
 * @return Новый список позиций змейки.
 */
fun moveSnake(
    snake: List<Position>,
    direction: Direction,
    foodPosition: Position,
    gridSize: Int,
    obstacles: List<Position>,
    onFoodEaten: (Position, Int) -> Unit
): List<Position> {
    val head = snake.first()
    val newHead = when (direction) {
        Direction.UP -> Position(head.x, (head.y - 1 + gridSize) % gridSize)
        Direction.DOWN -> Position(head.x, (head.y + 1) % gridSize)
        Direction.LEFT -> Position((head.x - 1 + gridSize) % gridSize, head.y)
        Direction.RIGHT -> Position((head.x + 1) % gridSize, head.y)
        Direction.NONE -> head
    }

    if (newHead in obstacles) return snake // Столкновение с препятствием

    return if (newHead.x == foodPosition.x && newHead.y == foodPosition.y) {
        val newFood = generateFood(gridSize, snake + newHead, FoodType.REGULAR)
        onFoodEaten(newFood.position, 10)
        listOf(newHead) + snake // Рост змейки
    } else {
        listOf(newHead) + snake.dropLast(1) // Перемещение без роста
    }
}

/**
 * Генерация новой еды на случайной незанятой позиции.
 *
 * @param gridSize Размер сетки.
 * @param snake Список позиций змейки.
 * @param foodType Тип еды.
 * @return Новый объект [Food].
 */
fun generateFood(gridSize: Int, snake: List<Position>, foodType: FoodType): Food {
    val allPositions = (0 until gridSize).flatMap { x -> (0 until gridSize).map { y -> Position(x, y) } }
    val availablePositions = allPositions.filter { it !in snake }
    return Food(position = availablePositions[Random.nextInt(availablePositions.size)], type = foodType)
}

/**
 * Проверка столкновения змейки с собой или препятствиями.
 *
 * @param snake Список позиций змейки.
 * @param gridSize Размер сетки.
 * @param obstacles Список позиций препятствий.
 * @return true, если есть столкновение, иначе false.
 */
private fun isCollision(snake: List<Position>, gridSize: Int, obstacles: List<Position>): Boolean {
    val head = snake.first()
    return snake.subList(1, snake.size).any { it.x == head.x && it.y == head.y } || head in obstacles
}