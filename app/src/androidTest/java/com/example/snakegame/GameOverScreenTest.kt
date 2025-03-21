package com.example.snakegame

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertTrue

class GameOverScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testGameOverScreenDisplaysCorrectly() {
        // Arrange
        composeTestRule.setContent {
            GameOverScreen(score = 150, onTryAgain = {}, onBackToMenu = {})
        }

        // Act & Assert
        composeTestRule.onNodeWithText("Игра окончена!").assertExists() // Проверка сообщения
        composeTestRule.onNodeWithText("Ваш счет: 150").assertExists() // Проверка счета
        composeTestRule.onNodeWithText("Попробовать снова").assertExists() // Проверка кнопки
        composeTestRule.onNodeWithText("Вернуться в меню").assertExists() // Проверка кнопки
    }

    @Test
    fun testTryAgainButtonTriggersCallback() {
        // Arrange
        var tryAgainCalled = false
        composeTestRule.setContent {
            GameOverScreen(
                score = 150,
                onTryAgain = { tryAgainCalled = true },
                onBackToMenu = {}
            )
        }

        // Act
        composeTestRule.onNodeWithText("Попробовать снова").performClick()

        // Assert
        assertTrue("Обратный вызов onTryAgain должен быть вызван", tryAgainCalled)
    }

    @Test
    fun testBackToMenuButtonTriggersCallback() {
        // Arrange
        var backToMenuCalled = false
        composeTestRule.setContent {
            GameOverScreen(
                score = 150,
                onTryAgain = {},
                onBackToMenu = { backToMenuCalled = true }
            )
        }

        // Act
        composeTestRule.onNodeWithText("Вернуться в меню").performClick()

        // Assert
        assertTrue("Обратный вызов onBackToMenu должен быть вызван", backToMenuCalled)
    }
}