package org.example.day6

import java.math.BigDecimal
import java.nio.file.Files
import java.nio.file.Path

fun main() {
//    val input = sample.lines()
    val input = Files.readAllLines(Path.of("src/main/resources/day6/input.txt"))

    val lines = input.dropLast(1)
    val operators = input.last().split(Regex(" +"))
    var operatorIndex = 0
    val numbers = mutableListOf<BigDecimal>()

    var result = BigDecimal.ZERO

    fun calc() {
        val operator = operators[operatorIndex]
        val columnResult = if (operator == "*") {
            numbers.reduce { a, b -> a * b }
        } else {
            numbers.reduce { a, b -> a + b }
        }
        println(numbers.joinToString(" $operator ") + " = $columnResult")

        result += columnResult
        operatorIndex++
        numbers.clear()
    }

    (0..<lines[0].length).forEach { x ->
        val numberString = lines.mapNotNull { line ->
            line[x].let {
                if (it == ' ') {
                    null
                } else {
                    it
                }
            }
        }.joinToString("")

        // Reached the end of the column
        if (numberString.isBlank()) {
            calc()
        } else {
            numbers.add(numberString.toBigDecimal())
        }
    }

    // Calc the last column
    calc()

    println(result)
}
