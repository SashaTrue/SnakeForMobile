package com.example.snakegame

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import org.junit.Rule
import org.junit.Test

class SnakeGameAppTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testNavigationFromStartToSettings() {
        composeTestRule.setContent {
            SnakeGameApp()
        }

        composeTestRule.onNodeWithText("Настройки").performClick()
        composeTestRule.onNodeWithText("Настройки").assertExists() // Заголовок экрана настроек
    }

    @Test
    fun testNavigationFromStartToGame() {
        composeTestRule.setContent {
            SnakeGameApp()
        }

        composeTestRule.onNodeWithText("Начать игру").performClick()
        composeTestRule.onNodeWithText("Счет: 0").assertExists() // Экран игры
    }

    @Test
    fun testNavigationFromGameToGameOver() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "game_screen") {
                composable("game_screen") {
                    GameScreen(
                        onGameOver = { score ->
                            navController.navigate("game_over_screen/$score")
                        },
                        difficulty = GameDifficulty.NORMAL
                    )
                }
                composable(
                    "game_over_screen/{score}",
                    arguments = listOf(navArgument("score") { type = NavType.IntType })
                ) { backStackEntry ->
                    GameOverScreen(
                        score = backStackEntry.arguments?.getInt("score") ?: 0,
                        onTryAgain = {},
                        onBackToMenu = {}
                    )
                }
            }
            // Симуляция окончания игры
            navController.navigate("game_over_screen/200")
        }

        composeTestRule.onNodeWithText("Игра окончена!").assertExists()
        composeTestRule.onNodeWithText("Ваш счет: 200").assertExists()
    }

    @Test
    fun testNavigationFromGameOverToStartScreen() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "game_over_screen/100") {
                composable(
                    "game_over_screen/{score}",
                    arguments = listOf(navArgument("score") { type = NavType.IntType })
                ) { backStackEntry ->
                    GameOverScreen(
                        score = backStackEntry.arguments?.getInt("score") ?: 0,
                        onTryAgain = {},
                        onBackToMenu = { navController.navigate("start_screen") }
                    )
                }
                composable("start_screen") {
                    StartScreen(
                        onStartGame = {},
                        onSettings = {},
                        onExit = {}
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Вернуться в меню").performClick()
        composeTestRule.onNodeWithText("Начать игру").assertExists() // Начальный экран
    }
}