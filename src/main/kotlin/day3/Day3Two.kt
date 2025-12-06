package org.example.day3

import java.math.BigDecimal
import java.nio.file.Files
import java.nio.file.Path

fun main() {
//    val input = sample.lines()
    val input = Files.readAllLines(Path.of("src/main/resources/day3/input.txt"))

    var totalMax = BigDecimal.ZERO

    input.forEach { line ->

        val maxOutput = joltage2(line, 12)

        println("$line has a max output of $maxOutput")
        totalMax += BigDecimal.valueOf(maxOutput.toLong())
    }

    println("Total of $totalMax")
}

fun joltage2(
    line: String,
    count: Int,
    result: String = "",
): String =
    if (count == 0) {
        result
    } else {
        val nextCount = count - 1
        // We can only look at those digits that allow enough space for the remaining digits
        val maxDigit = line.take(line.length - nextCount).maxBy { it }
        val maxIndex = line.indexOf(maxDigit)

        joltage2(
            line = line.substring(maxIndex + 1),
            count = nextCount,
            result = result + maxDigit,
        )
    }
