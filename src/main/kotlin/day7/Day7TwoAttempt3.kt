package org.example.day7

import org.example.readDay
import java.math.BigDecimal
import java.util.*

data class Timeline(
    val x: Int,
    val y: Int,
//    var log: String
)

/*
Idea: Don't simulate the grid, just follow the beam.
 */
fun main() {
    val input = sample.lines()
//    val input = readDay(7)

    val field = input.map { line -> line.toMutableList() }

    val objectPool = field.mapIndexed { y, line -> line.mapIndexed { x, _ -> Timeline(x, y) } }

    val timelines = LinkedList<Timeline>()

    timelines.add(objectPool[1][field[0].indexOf('S')])

    var finishedTimelines = 0L

    var steps = BigDecimal.ZERO

    do {
        var current = timelines.removeFirst()
        while (current.y < field.size) {
            steps++
            current.apply {
                when (field[y][x]) {
                    '.' -> {
                        val newY = y + 1
                        if (newY >= field.size) {
                            finishedTimelines++
                            if (finishedTimelines % 10_000_000 == 0L) {
                                println("Finished timeline $finishedTimelines, ${timelines.size} remain")
                            }
                            break
                        }
                        current = objectPool[newY][x]
//                        log += 'S'
                    }

                    '^' -> {
                        timelines.add(objectPool[y][x + 1])
                        current = objectPool[y][x - 1]
//                        timelines.add(Timeline(x = x+1, y = y, log = log+"E"))
//                        log += 'W'
                    }
                }
            }
        }
    } while (timelines.isNotEmpty())

    println("Finished timelines: $finishedTimelines in $steps steps")
}
