package org.example.day1

import java.nio.file.Files
import java.nio.file.Path

val sample =
    """
    L68
    L30
    R48
    L5
    R60
    L55
    L1
    L99
    R14
    L82
    """.trimIndent()

fun main() {
    var position = 50

    val input = Files.readAllLines(Path.of("src/main/resources/Day1/input.txt"))

    var zeroes = 0

    for (line in input) {
        val direction = line[0]
        val distance = line.substring(1).toInt()

        when (direction) {
            'L' -> position -= distance
            'R' -> position += distance
        }

        // Ensure position wraps around a 100-unit circular track
        position = (position + 100) % 100

        println("After moving $direction$distance, new position: $position")

        if (position == 0) {
            zeroes++
        }
    }

    println("Total times at position 0: $zeroes")
}
