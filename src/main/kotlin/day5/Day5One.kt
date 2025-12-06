package org.example.day5

import java.nio.file.Files
import java.nio.file.Path

val sample =
    """
3-5
10-14
16-20
12-18

1
5
8
11
17
32
    """.trimIndent()

fun main() {
    val input = sample.lines()
//    val input = Files.readAllLines(Path.of("src/main/resources/day5/input.txt"))

    val field = input.map { line -> line.toCharArray().toList() }

    fun check(
        x: Int,
        y: Int,
    ): Boolean {
        if (x < 0 || y < 0 || y >= field.size || x >= field[y].size) {
            return false
        }
        return field[y][x] == '@'
    }

    val positions = mutableListOf<Pair<Int, Int>>()

    (0..<field.size).forEach { y ->
        val columns = field[y]
        (0..<columns.size).forEach { x ->
            if (field[y][x] != '@') {
                return@forEach
            }

            val neighborRolls =
                listOf(
                    check(x - 1, y - 1),
                    check(x, y - 1),
                    check(x + 1, y - 1),
                    check(x - 1, y),
                    check(x + 1, y),
                    check(x - 1, y + 1),
                    check(x, y + 1),
                    check(x + 1, y + 1),
                ).count { it }

            if (neighborRolls < 4) {
                positions += Pair(y, x)
            }
        }
    }

    println("Total score of ${positions.size}")
    println("The positions are $positions")

//    println("Field debug:")
//    positions.forEach {
//        field[it.first][it.second] = true
//    }
}
