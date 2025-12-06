package org.example.day2

import java.math.BigDecimal
import java.nio.file.Files
import java.nio.file.Path

fun main() {
    var sum = BigDecimal.ZERO
    val input = Files.readAllLines(Path.of("src/main/resources/day2/input.txt")).first()
//    val input = sample

    val ranges = input.split(",")

    ranges.map { range ->
        val (start, end) = range.split("-").map { it.toLong() }

        val invalidForRange = mutableSetOf<Long>()

        (start..end)
            .forEach { number ->
                val itString = number.toString()

                val mid = itString.length / 2

                (1..mid).forEach {
                    if (itString.chunked(it).distinct().size == 1) {
                        invalidForRange.add(number)
                    }
                }
            }

        println("Range $range has invalid $invalidForRange")
        sum += BigDecimal.valueOf(invalidForRange.sum())
    }
    println(sum)
}
