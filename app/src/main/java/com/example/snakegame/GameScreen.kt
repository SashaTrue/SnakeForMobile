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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
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

val DarkGreen = Color(0xFF1B5E20)
val MediumGreen = Color(0xFF2E7D32)
val LightGreen = Color(0xFF43A047)
val AppleRed = Color(0xFFD32F2F)
val GoldenFood = Color(0xFFFFC107)

enum class FoodType {
    REGULAR,
    GOLDEN
}

data class Food(
    val position: Position,
    val type: FoodType = FoodType.REGULAR
)

data class Position(val x: Int, val y: Int)

enum class Direction {
    UP, DOWN, LEFT, RIGHT, NONE
}

enum class GameDifficulty(val speedMultiplier: Float, val foodFrequency: Int) {
    EASY(0.7f, 30),
    NORMAL(1.0f, 20),
    HARD(1.3f, 10)
}

@Composable
fun GameScreen(
    onGameOver: (Int) -> Unit,
    difficulty: GameDifficulty = GameDifficulty.NORMAL
) {
    val gridSize = 20
    val context = LocalContext.current

    var score by remember { mutableStateOf(0) }
    var snake by remember {
        mutableStateOf(
            when (difficulty) {
                GameDifficulty.EASY -> listOf(Position(gridSize / 2, gridSize / 2))
                GameDifficulty.NORMAL -> List(3) { Position(gridSize / 2 - it, gridSize / 2) }
                GameDifficulty.HARD -> List(5) { Position(gridSize / 2 - it, gridSize / 2) }
            }
        )
    }
    var food by remember { mutableStateOf(generateFood(gridSize, snake, FoodType.REGULAR)) }
    var direction by remember { mutableStateOf(Direction.NONE) }
    var gameRunning by remember { mutableStateOf(true) }
    var lastMovementTime by remember { mutableStateOf(System.currentTimeMillis()) }
    var gameSpeed by remember { mutableStateOf(300L) }
    var isPaused by remember { mutableStateOf(false) }
    var showCountdown by remember { mutableStateOf(true) }
    var countdown by remember { mutableStateOf(3) }

    val obstacles = remember {
        if (difficulty == GameDifficulty.HARD) {
            List(5) { Position(Random.nextInt(gridSize), Random.nextInt(gridSize)) }
                .filter { it !in snake && it != food.position }
        } else emptyList()
    }

    val foodScale by animateFloatAsState(
        targetValue = if (food.type == FoodType.GOLDEN) 1.2f else 1.0f,
        animationSpec = tween(durationMillis = 500),
        label = "foodScale"
    )

    val foodRotation by animateFloatAsState(
        targetValue = if (food.type == FoodType.GOLDEN) 360f else 0f,
        animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing)),
        label = "foodRotation"
    )

    LaunchedEffect(key1 = true) {
        // Сначала обратный отсчет
        while (countdown > 0) {
            delay(1000)
            countdown--
        }
        showCountdown = false
        // Устанавливаем начальное направление
        direction = Direction.RIGHT
        gameRunning = true
    }

    LaunchedEffect(key1 = showCountdown) {
        if (showCountdown) {
            while (countdown > 0) {
                delay(1000)
                countdown--
            }
            showCountdown = false
            gameRunning = true
            direction = Direction.RIGHT
            lastMovementTime = System.currentTimeMillis()
        }
    }

    LaunchedEffect(key1 = Unit) {
        while (true) {
            if (!gameRunning || isPaused || showCountdown) {
                delay(100)
                continue
            }

            val adjustedSpeed = (gameSpeed * difficulty.speedMultiplier).toLong()
            delay(adjustedSpeed)

            if (direction != Direction.NONE) {
                snake = moveSnake(snake, direction, food.position, gridSize, obstacles) { newFoodPosition, points ->
                    val foodType = if (Random.nextInt(100) < difficulty.foodFrequency) FoodType.GOLDEN else FoodType.REGULAR
                    food = Food(newFoodPosition, foodType)
                    val adjustedPoints = if (food.type == FoodType.GOLDEN) points * 3 else points
                    score += adjustedPoints
                    gameSpeed = maxOf(80L, 300L - (score / 5) * 5)

                    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    if (Build.VERSION.SDK_INT >= 26) {
                        vibrator.vibrate(android.os.VibrationEffect.createOneShot(100, android.os.VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        vibrator.vibrate(100)
                    }
                }

                if (isCollision(snake, gridSize, obstacles)) {
                    gameRunning = false
                    onGameOver(score)
                }
            }
        }
    }

    val gradientColors = when (difficulty) {
        GameDifficulty.EASY -> listOf(Color(0xFF4CAF50), Color(0xFF81C784))
        GameDifficulty.NORMAL -> listOf(Color(0xFFFFC107), Color(0xFFFFECB3))
        GameDifficulty.HARD -> listOf(Color(0xFFF44336), Color(0xFFEF9A9A))
    }

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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .shadow(8.dp, RoundedCornerShape(16.dp))
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
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cellSize = size.width / gridSize

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

                    drawSnake(snake, cellSize)

                    obstacles.forEach {
                        drawRect(
                            color = Color.Gray,
                            topLeft = Offset(it.x * cellSize, it.y * cellSize),
                            size = Size(cellSize, cellSize)
                        )
                    }

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
                        drawLine(
                            color = Color(0xFF795548),
                            start = Offset(centerX, centerY - radius),
                            end = Offset(centerX, centerY - radius * 1.3f),
                            strokeWidth = cellSize * 0.1f,
                            cap = StrokeCap.Round
                        )
                    } else {
                        drawCircle(
                            color = Color(0xFFFFFFFF),
                            center = Offset(centerX - radius * 0.3f, centerY - radius * 0.3f),
                            radius = radius * 0.15f
                        )
                    }
                }

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

private fun DrawScope.drawSnake(snake: List<Position>, cellSize: Float) {
    snake.forEachIndexed { index, position ->
        val centerX = (position.x * cellSize) + (cellSize / 2)
        val centerY = (position.y * cellSize) + (cellSize / 2)

        val segmentIndex = index.toFloat() / snake.size.toFloat()
        val segmentColor = when {
            index == 0 -> DarkGreen
            segmentIndex < 0.3f -> MediumGreen
            segmentIndex < 0.7f -> MediumGreen
            else -> LightGreen
        }

        val snakeRadius = if (index == 0) cellSize * 0.45f else cellSize * 0.4f

        if (index == 0) {
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
            drawCircle(
                color = Color.White,
                center = Offset(centerX - eyeOffset, centerY - eyeOffset),
                radius = snakeRadius * 0.2f
            )
            drawCircle(
                color = Color.White,
                center = Offset(centerX + eyeOffset, centerY - eyeOffset),
                radius = snakeRadius * 0.2f
            )

            drawCircle(
                color = Color.Black,
                center = Offset(centerX - eyeOffset, centerY - eyeOffset),
                radius = snakeRadius * 0.1f
            )
            drawCircle(
                color = Color.Black,
                center = Offset(centerX + eyeOffset, centerY - eyeOffset),
                radius = snakeRadius * 0.1f
            )
        } else {
            drawCircle(
                color = segmentColor,
                center = Offset(centerX, centerY),
                radius = snakeRadius
            )

            drawCircle(
                color = Color.White.copy(alpha = 0.3f),
                center = Offset(centerX - snakeRadius * 0.3f, centerY - snakeRadius * 0.3f),
                radius = snakeRadius * 0.2f
            )
        }
    }
}

private fun moveSnake(
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


    if (newHead in obstacles) return snake

    val newSnake = if (newHead.x == foodPosition.x && newHead.y == foodPosition.y) {
        val newFood = generateFood(gridSize, snake + newHead, FoodType.REGULAR)
        onFoodEaten(newFood.position, 10)
        listOf(newHead) + snake
    } else {
        listOf(newHead) + snake.dropLast(1)
    }

    return newSnake
}



private fun generateFood(gridSize: Int, snake: List<Position>, foodType: FoodType): Food {
    val allPositions = (0 until gridSize).flatMap { x ->
        (0 until gridSize).map { y -> Position(x, y) }
    }
    val availablePositions = allPositions.filter { it !in snake }
    return Food(
        position = availablePositions[Random.nextInt(availablePositions.size)],
        type = foodType
    )
}

private fun isCollision(snake: List<Position>, gridSize: Int, obstacles: List<Position>): Boolean {
    val head = snake.first()
    return snake.subList(1, snake.size).any { it.x == head.x && it.y == head.y } || head in obstacles
}