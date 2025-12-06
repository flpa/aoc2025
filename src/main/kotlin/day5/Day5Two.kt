package org.example.day5

import java.math.BigDecimal
import java.nio.file.Files
import java.nio.file.Path
import kotlin.math.max
import kotlin.math.min

fun main() {
//    val input = sample.lines()
    val input = Files.readAllLines(Path.of("src/main/resources/day5/input.txt"))

    val ranges = input.takeWhile { it.isNotEmpty() }.map { rangeString ->
        val (a, b) = rangeString.split("-").map { it.toLong() }
        a..b
    }.toMutableSet()

    loop@ do {
        val copy = ranges.toSet()
        copy.forEach { range1 ->
            val range2 = copy.filterNot { it == range1 }.find {
                it.contains(range1.first) || it.contains(range1.last) || range1.contains(it.first) || range1.contains(it.last)
            }

            if (range2 != null) {
                ranges.remove(range1)
                ranges.remove(range2)
                ranges += min(range1.first, range2.first)..max(range1.last, range2.last)
                continue@loop
            }
        }
    } while (ranges.size != copy.size)

    println("The merged ranges are ${ranges.toList()}")

    val total = ranges.sumOf { BigDecimal.valueOf(it.last - it.first+1) }

    println(total)
}

// Bruteforce fails with 8gig RAM, maybe lets do another option

//fun main() {
////    val input = sample.lines()
//    val input = Files.readAllLines(Path.of("src/main/resources/day5/input.txt"))
//
//    val freshness = mutableSetOf<Long>()
//
//    input.takeWhile { it.isNotEmpty() }.map { rangeString ->
//        val (a, b) = rangeString.split("-").map { it.toLong() }
//        freshness.addAll(a..b)
//    }
//
//    println(freshness.size)
//}
