@file:OptIn(ExperimentalAtomicApi::class)

package org.example.day11

import org.example.logger
import org.example.readDay
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.incrementAndFetch

val steps = AtomicLong(0L)
val startedThreads = AtomicLong(0L)
val finishedThreads = AtomicLong(0L)

fun main() {
//    val input = sample2.lines()
    val input = readDay(11)

    // TODO find all paths from you to out. backwards search, no repeats?

    val nodesById =
        input
            .map { line ->
                val (id, connectionsString) = line.split(":")
                val connections = connectionsString.trim().split(" ")
                Node(id, connections)
            }.plus(Node("out", listOf()))
            .associateBy { it.id }

    val successfulPaths = CopyOnWriteArrayList<Path>()

    fun explore(currentPath: Path) {
        startedThreads.incrementAndFetch()

        Thread.startVirtualThread {
            val step = steps.incrementAndFetch()
            logger.debug { "Step $step" }

            val currentNode = currentPath.nodes.last()
            if (currentNode.id == "out") {
                successfulPaths.add(currentPath)
            } else {
                currentNode.connections
                    .filterNot { connection -> currentPath.nodes.any { node -> node.id == connection } }
                    .map { currentPath.copy(nodes = currentPath.nodes + nodesById.getValue(it)) }
                    .forEach { explore(it) }
            }
            finishedThreads.incrementAndFetch()
        }
    }

    val startPath = Path(listOf(nodesById["svr"]!!))
    explore(startPath)

    do {
        Thread.sleep(1000)
    } while (startedThreads.load() > finishedThreads.load())

    logger.info { "Finished after running ${startedThreads.load()} threads" }

    logger.debug { successfulPaths.joinToString { "\n" } }
    logger.info { successfulPaths.filter { it.nodes.map { it.id }.containsAll(listOf("dac", "fft")) }.size }
}
