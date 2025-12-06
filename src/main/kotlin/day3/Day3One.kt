package org.example.day3

import java.nio.file.Files
import java.nio.file.Path

val sample =
    """
    987654321111111
    811111111111119
    234234234234278
    818181911112111
    """.trimIndent()

fun main() {
//    val input = sample.lines()
    val input = Files.readAllLines(Path.of("src/main/resources/day3/input.txt"))

    var totalMax = 0

    input.forEach { line ->

        val maxOutput = joltage(line)

        println("$line has a max output of $maxOutput")
        totalMax += maxOutput.toInt()
    }

    println("Total of $totalMax")
}

fun joltage(line: String): String {
    val indexes = mutableListOf<Int>()

    val maxDigit = line.maxBy { it.digitToInt() }
    val maxIndex = line.indexOf(maxDigit)

    indexes += maxIndex

    var indexModifier = 0

    val restLine: String =
        if (maxIndex == line.length - 1) {
            // If its the last, we need the max of the rest as first digit
            line.take(line.length - 1)
        } else {
            // Otherwise we need the highest number after the maxIndex
            indexModifier = maxIndex + 1
            line.substring(maxIndex + 1)
        }
    val secondMaxDigit = restLine.maxBy { it.digitToInt() }
    val secondIndex = restLine.indexOf(secondMaxDigit) + indexModifier
    indexes += secondIndex

    return indexes.sorted().map { line[it] }.joinToString("")
}
