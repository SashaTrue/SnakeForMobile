package com.example.snakegame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.snakegame.ui.theme.SnakeGameTheme

/**
 * Главная активность приложения "Змейка".
 * Эта активность является точкой входа в приложение и настраивает пользовательский интерфейс с использованием Jetpack Compose.
 */
class MainActivity : ComponentActivity() {
    /**
     * Вызывается при первом создании активности.
     * Инициализирует интерфейс, устанавливая содержимое в [SnakeGameApp], обёрнутое в [SnakeGameTheme] и [Surface].
     *
     * @param savedInstanceState Если не null, этот объект содержит ранее сохранённое состояние активности.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SnakeGameTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SnakeGameApp()
                }
            }
        }
    }
}

/**
 * Главная функция-компонент, которая настраивает навигацию и интерфейс приложения "Змейка".
 * Использует [NavHost] для управления навигацией между различными экранами: начальным экраном, экраном настроек, игровым экраном и экраном окончания игры.
 */
@Composable
fun SnakeGameApp() {
    val navController = rememberNavController()

    /**
     * Текущая сложность игры, хранимая как изменяемое состояние.
     * Это состояние используется для передачи выбранной сложности в [GameScreen].
     */
    var currentDifficulty by remember { mutableStateOf(GameDifficulty.NORMAL) } // Состояние сложности

    /**
     * Настраивает хост навигации для приложения.
     * Определяет граф навигации с маршрутами к различным экранам, начиная с "start_screen".
     */
    NavHost(navController = navController, startDestination = "start_screen") {
        composable(
            "start_screen",
            enterTransition = { fadeIn(animationSpec = tween(500)) },
            exitTransition = { fadeOut(animationSpec = tween(500)) }
        ) {
            StartScreen(
                onStartGame = { navController.navigate("game_screen") },
                onSettings = { navController.navigate("settings_screen") },
                onExit = { android.os.Process.killProcess(android.os.Process.myPid()) }
            )
        }
        composable(
            "settings_screen",
            enterTransition = { fadeIn(animationSpec = tween(500)) },
            exitTransition = { fadeOut(animationSpec = tween(500)) }
        ) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                currentDifficulty = currentDifficulty,
                onDifficultyChanged = { newDifficulty ->
                    currentDifficulty = newDifficulty
                }
            )
        }
        composable(
            "game_screen",
            enterTransition = { fadeIn(animationSpec = tween(500)) },
            exitTransition = { fadeOut(animationSpec = tween(500)) }
        ) {
            GameScreen(
                onGameOver = { score ->
                    navController.navigate("game_over_screen/$score") {
                        popUpTo("start_screen") { inclusive = false }
                    }
                },
                difficulty = currentDifficulty // Передаем текущую сложность
            )
        }
        composable(
            "game_over_screen/{score}",
            arguments = listOf(navArgument("score") { type = NavType.IntType }),
            enterTransition = { fadeIn(animationSpec = tween(500)) },
            exitTransition = { fadeOut(animationSpec = tween(500)) }
        ) { backStackEntry ->
            val score = backStackEntry.arguments?.getInt("score") ?: 0
            GameOverScreen(
                score = score,
                onTryAgain = {
                    navController.navigate("game_screen") {
                        popUpTo("game_over_screen/{score}") { inclusive = true }
                    }
                },
                onBackToMenu = {
                    navController.navigate("start_screen") {
                        popUpTo("start_screen") { inclusive = true }
                    }
                }
            )
        }
    }
}