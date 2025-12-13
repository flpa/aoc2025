package org.example.day11

import org.example.logger
import org.example.readDay

val sample2 = """
svr: aaa bbb
aaa: fft
fft: ccc
bbb: tty
tty: ccc
ccc: ddd eee
ddd: hub
hub: fff
eee: dac
dac: fff
fff: ggg hhh
ggg: out
hhh: out
""".trimIndent()

fun main() {
//    val input = sample2.lines()
    val input = readDay(11)

    // TODO find all paths from you to out. backwards search, no repeats?

    val nodesById = input.map { line ->
        val (id, connectionsString) = line.split(":")
        val connections = connectionsString.trim().split(" ")
        Node(id, connections)
    }
        .plus(Node("out", listOf()))
        .associateBy { it.id }

    val paths = mutableListOf(Path(listOf(nodesById["svr"]!!)))

    val successfulPaths = mutableListOf<Path>()

    // TODO eigentlich muss man jeden pfad nur einmal gehen

    var steps = 0L

    while (paths.isNotEmpty()) {
        steps++
        val currentPath = paths.removeFirst()
        if(steps % 1_000_000 == 0L) {
            logger.info{ "Step $steps with ${paths.size} paths remaining" }
        }

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
    logger.info { successfulPaths.filter { it.nodes.map { it.id }.containsAll(listOf("dac", "fft")) } .size }
}