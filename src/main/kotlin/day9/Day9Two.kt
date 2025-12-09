package org.example.day9

import org.example.readDay
import java.math.BigDecimal

data class Rectangle2(
    val from: Coords, val to: Coords,
    val area: BigDecimal = area(from, to)
) {
    fun listCoords(): List<Coords> {
        val (startX, endX) = if (from.x < to.x) {
            from.x to to.x
        } else to.x to from.x
        val (startY, endY) = if (from.y < to.y) {
            from.y to to.y
        } else to.y to from.y

        return (startX..endX).flatMap { x ->
            (startY..endY).map { y ->
                Coords(x, y)
            }
        }
    }
}

sealed class Border(
    open val from: Coords,
    open val to: Coords
) {
    data class HoriBorder(
        override val from: Coords,
        override val to: Coords,
        val y: Int,
    ) : Border(from, to)

    data class VertiBorder(
        override val from: Coords,
        override val to: Coords,
        val x: Int,
    ) : Border(from, to)
}

fun border(from: Coords, to: Coords): Border {
    return if (from.x == to.x) {
        Border.VertiBorder(from, to, from.x)
    } else if (from.y == to.y) {
        Border.HoriBorder(from, to, from.y)
    } else {
        throw IllegalArgumentException("Invalid border from $from to $to")
    }
}

fun main() {
    val debug = false

    val input = readDay(9)
//    val input = sample.lines()

    val cornerCords = input.map { line ->
        val (x, y) = line.split(",").map { it.toInt() }
        Coords(x, y)
    }

    val width = cornerCords.maxOf { it.x }
    val height = cornerCords.maxOf { it.y }

    val allCoords = (0..height + 1).flatMap { y ->
        (0..width + 1).flatMap { x ->
            listOf(Coords(x, y))
        }
    }.toList()

    println("Got all")

    val borders =
        cornerCords.windowed(2, 1).map { border(it[0], it[1]) } + border(cornerCords.last(), cornerCords.first())

    println("Got borders")

    val friendlyCoords = allCoords.filter { (x, y) ->
        var borderAbove = false
        var borderBelow = false
        var borderLeft = false
        var borderRight = false

        borders.forEach { border ->
            when (border) {
                is Border.HoriBorder -> {
                    val xRange = safeRange(border.from.x, border.to.x)
                    if (xRange.contains(x)) {
                        if (y <= border.y) {
                            borderBelow = true
                        }
                        if (y >= border.y) {
                            borderAbove = true
                        }
                    }
                }

                is Border.VertiBorder -> {
                    val yRange = safeRange(border.from.y, border.to.y)
                    if (yRange.contains(y)) {
                        if (x <= border.x) {
                            borderRight = true
                        }
                        if (x >= border.x) {
                            borderLeft = true
                        }
                    }
                }
            }

//            if (borderAbove && borderBelow && borderLeft && borderRight) {
//                return@forEach
//            }
        }

        // if the field is super tricky this doesnt work?
        borderAbove && borderBelow && borderLeft && borderRight
    }.toSet()

    println("Built friendly coords")
    if (debug) {
        println(friendlyCoords)
    }

    val allByArea = cornerCords.flatMap { start ->
        cornerCords.filterNot { it == start }.map { Rectangle2(start, it) }
    }.sortedByDescending { it.area }

    println("Got all by area")

    val result = allByArea
        .first {
            friendlyCoords.containsAll(it.listCoords())
        }

    if (debug) {
        val width = cornerCords.maxOf { it.x }
        val height = cornerCords.maxOf { it.y }

        allByArea.take(10).forEach {rect ->
            val field = (0..height + 1).map {
                (0..width + 1).map { "." }.toMutableList()
            }.toList()

            friendlyCoords.forEach { field[it.y][it.x] = "X" }

            cornerCords.forEach { field[it.y][it.x] = "#" }
//        field[1][9] = "?"

            val rectCoords = rect.listCoords()
            rectCoords.forEach { field[it.y][it.x] = "O" }

            println("\n")


            println(field.joinToString("\n") { it.joinToString("") })
        }
    }

    println(result)
}

// WTF?!
fun safeRange(a: Int, b: Int): IntRange {
    return if (a > b) b..a else a..b
}