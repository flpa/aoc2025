package org.example.day9

data class Border(
    val from: Coords,
    val to: Coords,
)

fun main() {
//    val input = readDay(9)
    val input = sample.lines()

    val coords = input.map { line ->
        val (x, y) = line.split(",").map { it.toInt() }
        Coords(x, y)
    }

    val borders = coords.windowed(2, 1).map { Border(it[0], it[1]) }

    val friendlyCoords = coords.filter { (x, y) ->
        var borderAbove = false
        var borderBelow = false
        var borderLeft = false
        var borderRight = false

        borders.forEach { border ->
            val xRange = border.from.x..border.to.x
            val yRange = border.from.y..border.to.y
            if(xRange.contains(x) && y >= border.)
        }
    }

    println("Built friendly coords")

    val result = coords.map { start ->
        coords.filterNot { it == start }.map { Rectangle(start, it) }.maxBy { it.area }
    }.maxBy { it.area }


    println(result)
}