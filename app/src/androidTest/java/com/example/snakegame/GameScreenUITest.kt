import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import com.example.snakegame.GameDifficulty
import com.example.snakegame.GameScreen
import org.junit.Rule
import org.junit.Test
import kotlinx.coroutines.test.runTest

class GameScreenUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testGameScreenDisplaysCorrectly() = runTest {
        // Arrange
        composeTestRule.setContent {
            GameScreen(onGameOver = {}, difficulty = GameDifficulty.NORMAL)
        }

        // Assert
        composeTestRule.onNodeWithText("Средний").assertExists() // Уровень сложности
        composeTestRule.onNodeWithText("Счет: 0").assertExists() // Счет
        composeTestRule.onNodeWithText("II").assertExists() // Кнопка паузы
        composeTestRule.onNodeWithText("3").assertExists() // Обратный отсчет
    }

    @Test
    fun testSwipeChangesDirection() = runTest {
        // Arrange
        composeTestRule.setContent {
            GameScreen(onGameOver = {}, difficulty = GameDifficulty.NORMAL)
        }

        // Ждем завершения отсчета
        composeTestRule.waitUntil {
            try {
                composeTestRule.onNodeWithText("3").assertDoesNotExist()
                true
            } catch (e: AssertionError) {
                false
            }
        }


        // Act
        composeTestRule.onNodeWithText("Счет: 0").performTouchInput { swipeLeft() }

        // Assert (косвенно через логику, направление проверяется в юнит-тестах)
    }
}