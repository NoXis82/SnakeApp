package ru.umarsh.snakeapp.data

data class State(
    val food: Pair<Int, Int>,
    val snake: List<Pair<Int, Int>>
)
