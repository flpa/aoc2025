package org.example.day7

import org.example.readDay

data class Grid(
    val singleLine: String,
    val height: Int,
    val width: Int
) {
    constructor(input: String) : this(
        input.lines().joinToString(""),
        input.lines().size,
        input.lines()[0].length
    )

    fun copyGrid(x: Int, y: Int, newVal: Char): Grid {
        return copy(singleLine = singleLine.replaceRange(y*width + x, y*width + x + 1, newVal.toString()))
    }

    operator fun get(y: Int): String {
        val start = y*width

        return singleLine.substring(start, start + width)
    }

}

fun main() {
//    val input = sample
    val input = readDay(7).joinToString("\n")

    val timelines = mutableSetOf<String>()

    val initialGrid = Grid(input)

    val queue = ArrayDeque(listOf(Task(0, initialGrid.width, initialGrid)))

    var counter = 0

    val debug = false

    do {
        counter++
//        if (counter % 1_000_000 == 0) {
//            println("At step $counter with ${timelines.size} finished timelines and ${queue.size} remaining tasks")
//        }
        val (oldY, oldX, field) = queue.removeFirst()

        if (debug && counter >= 2000)
            return

        var x = oldX + 1
        val y =
            if (x >= field.width) {
                x = 0
                oldY + 1
            } else {
                oldY
            }

        if (debug) {
            val displayGrid = field.copyGrid(x, y, 'C')
            val display = displayGrid.singleLine.chunked(displayGrid.width)
                .take(y + 1).joinToString("\n")

            println(display)
            println("")
        }

        if (y >= field.height) {
            // time to collect the timeline
            timelines.add(field.singleLine)
            continue
        }

        val beamAbove = listOf('S', '|').contains(field[y - 1][x])

        if (beamAbove) {
            when (field[y][x]) {
                '.' ->
                    queue.add(Task(y, x, field.copyGrid( x, y, '|')))

                '^' -> {
                    queue.add(Task(y, x, field.copyGrid( x - 1, y, '|')))
                    queue.add(Task(y, x, field.copyGrid( x + 1, y, '|')))
                }
            }
        } else {
            queue.add(Task(y, x, field))
        }

    } while (queue.isNotEmpty())

//    timelines.forEach {
//        println("\n")
//        println(it.joinToString("\n") { line -> line.joinToString("") })
//        println("\n")
//    }

    println(timelines.size)
}

data class Task(val oldY: Int, val oldX: Int, val field: Grid)