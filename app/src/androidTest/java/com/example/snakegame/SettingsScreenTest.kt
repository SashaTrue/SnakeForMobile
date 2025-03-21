import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.snakegame.GameDifficulty
import com.example.snakegame.SettingsScreen
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testSettingsScreenDisplaysCorrectly() {
        // Arrange
        composeTestRule.setContent {
            SettingsScreen(onBack = {}, currentDifficulty = GameDifficulty.NORMAL, onDifficultyChanged = {})
        }

        // Assert
        composeTestRule.onNodeWithText("Настройки").assertExists()
        composeTestRule.onNodeWithText("?").assertExists()
        composeTestRule.onNodeWithText("Легкий").assertExists()
        composeTestRule.onNodeWithText("Средний").assertExists()
        composeTestRule.onNodeWithText("Сложный").assertExists()
        composeTestRule.onNodeWithText("Звук").assertExists()
        composeTestRule.onNodeWithText("Вибрация").assertExists()
        composeTestRule.onNodeWithText("Назад").assertExists()
    }

    @Test
    fun testDifficultyChangeTriggersCallback() {
        // Arrange
        var selectedDifficulty: GameDifficulty? = null
        composeTestRule.setContent {
            SettingsScreen(
                onBack = {},
                currentDifficulty = GameDifficulty.NORMAL,
                onDifficultyChanged = { selectedDifficulty = it }
            )
        }

        // Act
        composeTestRule.onNodeWithText("Сложный").performClick()

        // Assert
        assertEquals(GameDifficulty.HARD, selectedDifficulty)
    }

    @Test
    fun testBackButtonTriggersCallback() {
        // Arrange
        var backCalled = false
        composeTestRule.setContent {
            SettingsScreen(
                onBack = { backCalled = true },
                currentDifficulty = GameDifficulty.NORMAL,
                onDifficultyChanged = {}
            )
        }

        // Act
        composeTestRule.onNodeWithText("Назад").performClick()

        // Assert
        assertTrue("onBack should be called", backCalled)
    }
}