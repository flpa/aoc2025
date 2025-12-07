package org.example.day6

import java.math.BigDecimal
import java.nio.file.Files
import java.nio.file.Path

val sample =
    """
123 328  51 64 
 45 64  387 23 
  6 98  215 314
*   +   *   +  
    """.trimIndent()

fun main() {
//    val input = sample.lines()
    val input = Files.readAllLines(Path.of("src/main/resources/day6/input.txt"))

    val lines = input.dropLast(1).map { line -> line.trim().split(Regex(" +")).map { BigDecimal.valueOf(it.toLong()) } }
    val operators = input.last().split(Regex(" +"))

    val result = (0..<lines[0].size).map { x ->
        val numbers = lines.map { it[x] }
        val operator = operators[x]
        val columnResult = if(operator == "*") {
            numbers.reduce { a, b -> a * b }
        } else {
            numbers.reduce { a, b -> a + b }
        }

        println(numbers.joinToString(" $operator ") + " = $columnResult")
        columnResult
    }.sumOf { it }

    println(result)
}
