package org.example.day8

import kotlin.math.pow
import kotlin.math.sqrt


val sample = """
    162,817,812
    57,618,57
    906,360,560
    592,479,940
    352,342,300
    466,668,158
    542,29,236
    431,825,988
    739,650,466
    52,470,668
    216,146,977
    819,987,18
    117,168,530
    805,96,715
    346,949,466
    970,615,88
    941,993,340
    862,61,35
    984,92,344
    425,690,689
""".trimIndent()

fun main() {
//    doTheThing(readDay(8), 1000)
    doTheThing(sample.lines(), 10)
}

fun doTheThing(input: List<String>, stepCount: Int) {
    val coords = input.map { line ->
        val (x, y, z) = line.split(",").map { it.toInt() }
        Coordinate(x, y, z)
    }

    println("Parsed all coords")

    val coordDistances = mutableListOf<CoordDistance>()

    coords.forEachIndexed { i, a ->
        coords.drop(i + 1).forEach { b ->
            coordDistances.add(CoordDistance(a, b, straightLineDistance(a, b)))
        }
    }

    coordDistances.sortBy { it.distance }

    println("Calculated all distances")
    val circuits = coords.map { Circuit(mutableSetOf(it)) }.toMutableList()

    (1..stepCount).forEach { step ->
        println("\nStep: $step")

        val nextShortest = coordDistances.removeFirst()

        val existingCircuit1 = circuits.first { it.nodes.contains(nextShortest.from) }
        val existingCircuit2 = circuits.first { it.nodes.contains(nextShortest.to) }

        println("Shortest is: $nextShortest")
        if (existingCircuit1 == existingCircuit2) {
            println("They already are in the same circuit, nothing happens")
        } else {
            existingCircuit1.nodes.addAll(existingCircuit2.nodes)
            circuits.remove(existingCircuit2)
        }

        println("Circuits: $circuits")
    }

    val totalCircuits = circuits.size
    println("Total circuits: $totalCircuits")

    val topCircuits = circuits.map { it.nodes.size }.sortedDescending() .take(3)
    println("Top Circuits: $topCircuits")
    val result = topCircuits.reduce { acc, next -> acc * next }
    println(result)
}

data class Circuit(val nodes: MutableSet<Coordinate>)

data class Coordinate(var x: Int, var y: Int, var z: Int) {
//    val connections = mutableListOf<Coordinate>()
}

data class CoordDistance(val from: Coordinate, val to: Coordinate, val distance: Double)

fun straightLineDistance(a: Coordinate, b: Coordinate): Double {
    return sqrt((b.x - a.x).pow2() + (b.y - a.y).pow2() + (b.z - a.z).pow2())
}

fun Int.pow2() = this.toDouble().pow(2)