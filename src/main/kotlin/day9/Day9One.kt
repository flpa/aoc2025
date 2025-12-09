package org.example.day9

import org.example.readDay
import java.math.BigDecimal
import kotlin.math.abs


val sample = """
7,1
11,1
11,7
9,7
9,5
2,5
2,3
7,3
""".trimIndent()

data class Rectangle(
    val from: Coords, val to: Coords,
    val area: BigDecimal = area(from, to)
)

data class Coords(val x: Int, val y: Int)

fun main() {
    val input = readDay(9)
//    val input = sample.lines()

    val coords = input.map { line ->
        val (x, y) = line.split(",").map { it.toInt() }
        Coords(x, y)
    }

    // lets start primitive and try all

    val result = coords.map { start ->
        coords.filterNot { it == start }.map { Rectangle(start, it) }.maxBy { it.area }
    }.maxBy { it.area }


    println(result)
}


fun area(a: Coords, b: Coords): BigDecimal {
    return BigDecimal.valueOf(abs(a.x - b.x) + 1L) * BigDecimal.valueOf(abs(a.y - b.y) + 1L)
}