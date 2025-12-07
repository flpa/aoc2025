package org.example.day7

import java.nio.file.Files
import java.nio.file.Path

val sample =
    """
.......S.......
...............
.......^.......
...............
......^.^......
...............
.....^.^.^.....
...............
....^.^...^....
...............
...^.^...^.^...
...............
..^...^.....^..
...............
.^.^.^.^.^...^.
...............
    """.trimIndent()

fun main() {
//    val input = sample.lines()
    val input = Files.readAllLines(Path.of("src/main/resources/day7/input.txt"))

    val field = input.map { line -> line.toMutableList() }

    var splits = 0

    (1..<field.size).forEach { y ->
        (0..<field[y].size).forEach { x ->
            val beamAbove = listOf('S', '|').contains(field[y - 1][x])

            if (beamAbove) {
                when (field[y][x]) {
                    '.' -> field[y][x] = '|'
                    '^' -> {
                        field[y][x - 1] = '|'
                        field[y][x + 1] = '|'
                        splits++
                    }
                }
            }
        }
    }

    println(field.joinToString("\n") { line -> line.joinToString("") })
    println(splits)
}
