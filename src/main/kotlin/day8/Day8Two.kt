package org.example.day8

import org.example.readDay

fun main() {
    doTheThing2(readDay(8))
//    doTheThing2(sample.lines(), 10)
}

fun doTheThing2(input: List<String>) {
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

    while(true) {
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

        if (circuits.size == 1) {
            println("One circuit remains, last connection was $nextShortest")
            val result = nextShortest.from.x * nextShortest.to.x
            println("Result x1 * x2 = $result")
            break
        }
    }
}
//
//    val totalCircuits = circuits.size
//    println("Total circuits: $totalCircuits")
//
//    val topCircuits = circuits.map { it.nodes.size }.sortedDescending() .take(3)
//    println("Top Circuits: $topCircuits")
//    val result = topCircuits.reduce { acc, next -> acc * next }
//    println(result)