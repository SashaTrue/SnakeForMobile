import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertTrue
import com.example.snakegame.StartScreen

class StartScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testStartScreenDisplaysCorrectly() {
        // Arrange
        composeTestRule.setContent {
            StartScreen(onStartGame = {}, onSettings = {}, onExit = {})
        }

        // Act & Assert
        composeTestRule.onNodeWithText("🐍").assertExists() // Логотип
        composeTestRule.onNodeWithText("Змейка").assertExists() // Название
        composeTestRule.onNodeWithText("Начать игру").assertExists() // Кнопка
        composeTestRule.onNodeWithText("Настройки").assertExists() // Кнопка
        composeTestRule.onNodeWithText("Выход").assertExists() // Кнопка
    }

    @Test
    fun testStartGameButtonTriggersCallback() {
        // Arrange
        var startGameCalled = false
        composeTestRule.setContent {
            StartScreen(
                onStartGame = { startGameCalled = true },
                onSettings = {},
                onExit = {}
            )
        }

        // Act
        composeTestRule.onNodeWithText("Начать игру").performClick()

        // Assert
        assertTrue("onStartGame should be called", startGameCalled)
    }

    @Test
    fun testSettingsButtonTriggersCallback() {
        // Arrange
        var settingsCalled = false
        composeTestRule.setContent {
            StartScreen(
                onStartGame = {},
                onSettings = { settingsCalled = true },
                onExit = {}
            )
        }

        // Act
        composeTestRule.onNodeWithText("Настройки").performClick()

        // Assert
        assertTrue("onSettings should be called", settingsCalled)
    }

    @Test
    fun testExitButtonTriggersCallback() {
        // Arrange
        var exitCalled = false
        composeTestRule.setContent {
            StartScreen(
                onStartGame = {},
                onSettings = {},
                onExit = { exitCalled = true }
            )
        }

        // Act
        composeTestRule.onNodeWithText("Выход").performClick()

        // Assert
        assertTrue("onExit should be called", exitCalled)
    }
}