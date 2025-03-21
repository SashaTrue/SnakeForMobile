package com.example.snakegame

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GameLogicTest {

    @Test
    fun testMoveSnakeRight() {
        val snake = listOf(Position(10, 10))
        val foodPosition = Position(11, 10)
        var newFoodPosition: Position? = null
        var points = 0

        val newSnake = moveSnake(
            snake,
            Direction.RIGHT,
            foodPosition,
            gridSize = 20,
            obstacles = emptyList()
        ) { pos, pts ->
            newFoodPosition = pos
            points = pts
        }

        assertEquals(listOf(Position(11, 10), Position(10, 10)), newSnake)
        assertEquals(10, points)
        assertTrue(newFoodPosition != null)
    }

    @Test
    fun testGenerateFood() {
        val snake = listOf(Position(10, 10))
        val food = generateFood(20, snake, FoodType.REGULAR)
        assertTrue(food.position !in snake)
        assertEquals(FoodType.REGULAR, food.type)
    }
}