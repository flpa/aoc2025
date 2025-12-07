package org.example.day7

import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit

val threads = CopyOnWriteArrayList<Thread>()

fun main() {
//    val input = sample.lines()
    val input = Files.readAllLines(Path.of("src/main/resources/day7/input.txt"))

    val field = input.map { line -> line.toMutableList() }

    val timelines = mutableSetOf<List<List<Char>>>()

    quantumThings(0, field[0].size, field, timelines)

//    threads.forEach { it.join() }

    Thread.sleep(TimeUnit.MINUTES.toMillis(5))

    timelines.forEach {
        println("\n")
        println(it.joinToString("\n") { line -> line.joinToString("") })
        println("\n")
    }

    println(timelines.size)
}

fun quantumThings(oldY: Int, oldX: Int, field: List<List<Char>>, timelines: MutableSet<List<List<Char>>>) {
//    println("Recurse y=$oldY, x=$oldX")
    threads += Thread.startVirtualThread {

        var x = oldX + 1
        val y =
            if (x >= field[0].size) {
                x = 0
                oldY + 1
            } else {
                oldY
            }

        if (y >= field.size) {
            // time to collect the timeline
            timelines.add(field)
            return@startVirtualThread
        }

        val beamAbove = listOf('S', '|').contains(field[y - 1][x])

        if (beamAbove) {
            when (field[y][x]) {
                '.' ->
                    quantumThings(y, x, copyGrid(field, x, y, '|'), timelines)

                '^' -> {
                    quantumThings(y, x, copyGrid(field, x - 1, y, '|'), timelines)
                    quantumThings(y, x, copyGrid(field, x + 1, y, '|'), timelines)
                }
            }
        } else {
            quantumThings(y, x, field, timelines)
        }
    }
}

fun copyGrid(grid: List<List<Char>>, x: Int, y: Int, newVal: Char): List<List<Char>> {
    return grid.mapIndexed { innerY, line ->
        line.mapIndexed { innerX, cell ->
            if (innerX == x && innerY == y) {
                newVal
            } else {
                cell
            }
        }
    }
}
