package ru.umarsh.snakeapp.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.umarsh.snakeapp.data.State
import java.util.*

class Game(private val scope: CoroutineScope) {

    private val mutex = Mutex()

    private val mutableState = MutableStateFlow(
        State(
            food = Pair(5, 5),
            snake = listOf(Pair(7, 7))

        )
    )
    val state: Flow<State> = mutableState

    var move = Pair(1, 0)
        set(value) {
            scope.launch {
                mutex.withLock {
                    field = value
                }
            }
        }

    init {
        scope.launch {
            var snakeLength = 4
            while (true) {
                delay(150)
                mutableState.update {
                    val newPosition = it.snake.first().let { poz ->
                        mutex.withLock {
                            Pair(
                                (poz.first + move.first + BOARD_SIZE) % BOARD_SIZE,
                                (poz.second + move.second + BOARD_SIZE) % BOARD_SIZE
                            )
                        }
                    }

                    if (newPosition == it.food) {
                        snakeLength++
                    }

                    if (it.snake.contains(newPosition)) {
                        snakeLength = 4
                    }

                    it.copy(
                        snake = listOf(newPosition) + it.snake.take(snakeLength - 1),
                        food = if (newPosition == it.food) Pair(
                            Random().nextInt(BOARD_SIZE),
                            Random().nextInt(BOARD_SIZE)
                        ) else it.food
                    )
                }
            }
        }
    }

    companion object {
        const val BOARD_SIZE = 16
    }
}