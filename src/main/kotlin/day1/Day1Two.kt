package org.example.day1

import java.nio.file.Files
import java.nio.file.Path

fun main() {
    var position = 50

    val input = Files.readAllLines(Path.of("src/main/resources/Day1/input.txt"))
//    val input = sample.lines()

    var zeroes = 0

    for (line in input) {
        val direction = line[0]
        val distance = line.substring(1).toInt()

        var modifier = 0

        when (direction) {
            'L' -> modifier = -1
            'R' -> modifier = 1
        }
        var remainingDistance = distance

        while (remainingDistance > 0) {
            position += modifier
            remainingDistance--

            // Ensure position wraps around a 100-unit circular track
            position = (position + 100) % 100

            if (position == 0) {
                zeroes++
            }
        }

        println("After moving $direction$distance, new position: $position")
    }

    println("Total times at position 0: $zeroes")
}
