package org.example.day11

import org.example.logger
import org.example.readDay

val sample = """
    aaa: you hhh
    you: bbb ccc
    bbb: ddd eee
    ccc: ddd eee fff
    ddd: ggg
    eee: out
    fff: out
    ggg: out
    hhh: ccc fff iii
    iii: out
""".trimIndent()

fun main() {
//    val input = sample.lines()
    val input = readDay(11)

    // TODO find all paths from you to out. backwards search, no repeats?

    val nodesById = input.map { line ->
        val (id, connectionsString) = line.split(":")
        val connections = connectionsString.trim().split(" ")
        Node(id, connections)
    }
        .plus(Node("out", listOf()))
        .associateBy { it.id }

    val paths = mutableListOf(Path(listOf(nodesById["you"]!!)))

    val successfulPaths = mutableListOf<Path>()

    // TODO eigentlich muss man jeden pfad nur einmal gehen

    while (paths.isNotEmpty()) {
        val currentPath = paths.removeFirst()

        val currentNode = currentPath.nodes.last()
        if (currentNode.id == "out") {
            successfulPaths.add(currentPath)
        } else {
            paths.addAll(
                currentNode.connections
                    .filterNot { connection -> currentPath.nodes.any { node -> node.id == connection } }
                    .map { currentPath.copy(nodes = currentPath.nodes + nodesById.getValue(it)) })
        }
    }

    logger.debug { successfulPaths.joinToString { "\n" } }
    logger.info { successfulPaths.size }
}

data class Node(
    val id: String,
    val connections: List<String>,
)

data class Path(
    val nodes: List<Node>,
)