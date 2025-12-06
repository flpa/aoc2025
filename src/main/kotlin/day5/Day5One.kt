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
//    val input = sample.lines()
    val input = Files.readAllLines(Path.of("src/main/resources/day5/input.txt"))

    val ranges = input.takeWhile { it.isNotEmpty() }.map { rangeString ->
        val (a, b) = rangeString.split("-").map { it.toLong() }
        a..b
    }

    val result = input
        .takeLastWhile { it.isNotEmpty() }
        .map { it.toLong() }
        .count { ranges.any { range -> range.contains(it) } }

    println(result)
}
