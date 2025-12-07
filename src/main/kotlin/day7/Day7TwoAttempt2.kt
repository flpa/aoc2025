package org.example.day7

import org.example.readDay

fun main() {
//    val input = sample.lines()
    val input = readDay(7)

    val field = input.map { line -> line.map { it.toString() }.toMutableList() }

    (1..<field.size).forEach { y ->
        var lineQuantumCounter = 1
        (0..<field[y].size).forEach { x ->
//            val printField = field.map { line -> line.toMutableList() }
//            printField[y][x] = "C"
//            println(printField.take(y + 1).map { it.joinToString("\t") }.joinToString("\n"))
//            println("")

            val above = field[y - 1][x]
            if (!listOf(".", "^").contains(above)) {
                when (field[y][x]) {
                    "^" -> {
                        field[y][x - 1] =
                            (field[y][x - 1] + above + "-" + "y$y#" + (lineQuantumCounter++)).replace(".", "")
                        field[y][x + 1] =
                            (field[y][x + 1] + above + "-" + "y$y#" + (lineQuantumCounter++)).replace(".", "")
                    } else -> field[y][x] = above
                }
            }
        }
    }

//    println(field.joinToString("\n") { line -> line.joinToString("\t") })

    val uniqueIds = field.last().flatMap { it.split("S").filterNot { listOf("", ".").contains(it) } }.distinct()

    println(uniqueIds)
    println((uniqueIds.size +1)* 2)
}
