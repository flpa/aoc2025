package org.example.day2

import java.nio.file.Files
import java.nio.file.Path

val sample =
    "11-22,95-115,998-1012,1188511880-1188511890,222220-222224," +
        "1698522-1698528,446443-446449,38593856-38593862,565653-565659," +
        "824824821-824824827,2121212118-2121212124"

fun main() {
    var sum = 0L
    val input = Files.readAllLines(Path.of("src/main/resources/day2/input.txt")).first()

    val ranges = input.split(",")

    ranges.map { range ->
        val (start, end) = range.split("-").map { it.toLong() }

        val invalidForRange = mutableListOf<Long>()

        (start..end)
            .forEach {
                val itString = it.toString()

                if (itString.length % 2 == 0) {
                    val mid = itString.length / 2
                    val firstHalf = itString.substring(0, mid)
                    val secondHalf = itString.substring(mid)

                    if (firstHalf == secondHalf) {
                        invalidForRange.add(it)
                    }
                }
            }

        println("Range $range has invalid $invalidForRange")
        sum += invalidForRange.sum()
    }
    println(sum)
}
