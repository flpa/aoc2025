package org.example.day3

import java.math.BigDecimal
import java.nio.file.Files
import java.nio.file.Path

fun main() {
    val input = sample.lines()
//    val input = Files.readAllLines(Path.of("src/main/resources/day3/input.txt"))

    var totalMax = BigDecimal.ZERO

    input.forEach { line ->

        val maxOutput = joltage2(line)

        println("$line has a max output of $maxOutput")
        totalMax += BigDecimal.valueOf(maxOutput.toLong())
    }

    println("Total of $totalMax")
}

fun joltage2(line: String): String {
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
