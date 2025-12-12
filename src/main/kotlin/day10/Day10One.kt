package org.example.day10

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.example.logger
import org.example.readDay
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.math.min

val sample = """
    [.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}
    [...#.] (0,2,3,4) (2,3) (0,4) (0,1,2) (1,2,3,4) {7,5,12,7,2}
    [.###.#] (0,1,2,3,4) (0,3,4) (0,1,2,4,5) (1,2) {10,11,11,5,10,5}
""".trimIndent()

data class Machine(
    val index: Int,
    val targetIndicatorLights: String,
    val indicatorLights: String = ".".repeat(targetIndicatorLights.length),
    val buttons: List<Button>,
    val solvedIndexes: List<Int> = findSolved(targetIndicatorLights, indicatorLights)
) {
    fun press(b: Button): Machine {
        val newLights = indicatorLights.mapIndexed { i, value ->
            if (b.affectedIndexes.contains(i)) {
                if (value == '.') '#' else '.'
            } else {
                value
            }
        }.joinToString("")

        return copy(indicatorLights = newLights, solvedIndexes = findSolved(targetIndicatorLights, newLights))
    }

    val isSolved
        get() = targetIndicatorLights == indicatorLights
}

private fun findSolved(
    targetIndicatorLights: String,
    indicatorLights: String
): List<Int> = targetIndicatorLights.mapIndexedNotNull { i, char ->
    if (indicatorLights[i] == char) i else null
}

data class Button(val affectedIndexes: List<Int>) {
    override fun toString(): String {
        return "($affectedIndexes)"
    }
}

@OptIn(ExperimentalAtomicApi::class)
fun main() {
    val input = readDay(10)
//    val input = sample.lines()

    // Problematic machines: [142]
    val debugMachines = listOf<Int>()

    val machines = input.mapIndexed { i, line ->
        val match = Regex("\\[([.#]+)] ((?:\\([0-9,]+\\) )+).*").matchEntire(line)!!
        println(match.groupValues)
        val indicatorString = match.groupValues[1]
        println(indicatorString)
        val buttonsString = match.groupValues[2]
        println(buttonsString)

        Machine(
            index = i,
            targetIndicatorLights = indicatorString,
            buttons = buttonsString.trim().split(' ')
                .map {
                    Button(it.removeSurrounding("(", ")").split(",").map { it.toInt() })
                })
    }

    logger.info { "Parsed ${machines.size} machines" }


    val totalShortest = AtomicLong(0L)
    val finishedMachines = AtomicInt(0)

    val runningMachines = CopyOnWriteArrayList<Int>()


    val watcher = Thread.startVirtualThread {
        Thread.sleep(30_000)
        println("Running machines: $runningMachines")
    }

    val machinesToProcess = if (debugMachines.isNotEmpty()) {
        machines.filter { debugMachines.contains(it.index) }
    } else {
        machines
    }

    machinesToProcess.coroutinedMap { machine ->
        println("Running machine ${machine.index}")
        runningMachines.add(machine.index)

        val shortestPerState = mutableMapOf<String, Int>()

        val paths = machine.buttons.map {
            SolutionPath(machine.press(it), listOf(it))
        }.toMutableList()

        var steps = 0L

        while (paths.isNotEmpty()) {
            ++steps
            val currentPath = paths.removeFirst()
            shortestPerState[currentPath.machine.indicatorLights] = min(
                shortestPerState.getOrDefault(currentPath.machine.indicatorLights, 99),
                currentPath.buttonPresses.size
            )

            logger.debug { "Exploring step $steps: $currentPath" }
            if (steps % 1_000_000 == 0L) {
                logger.info { "Reached $steps steps, ${paths.size} paths remaining" }
            }

            val currentBest = shortestPerState.getOrDefault(currentPath.machine.targetIndicatorLights, 99)

            if (currentPath.machine.isSolved) {
                logger.debug { "Machine is solved" }
                // If exploring further can only end up in a longer path, discard the path.
            } else if (currentBest > currentPath.buttonPresses.size + 1) {
                val pathsToAdd = currentPath.machine.buttons
                    .filter {
                        // Dont repeat the same button twice, or use a button that only affects solved indexes
                        it != currentPath.buttonPresses.last() //|| currentPath.machine.solvedIndexes.containsAll(it.affectedIndexes)
                        // Create a new solution path
                    }.map { button ->
                        SolutionPath(currentPath.machine.press(button), currentPath.buttonPresses + button)
                        // Skip solution paths if we reached the same state faster before
                    }.filter { newSolution ->
                        shortestPerState.getOrDefault(
                            newSolution.machine.indicatorLights,
                            99
                        ) > newSolution.buttonPresses.size
                    }

                paths.addAll(pathsToAdd)
            }
        }

        val result = shortestPerState[machine.targetIndicatorLights]!!.toLong()
        val currentResult = totalShortest.addAndFetch(result)
        val count = finishedMachines.addAndFetch(1)
        println("Finished machine ${machine.index} with a shortest path of length $result. $count/${machines.size} machines have finished.")
        println("Current result is $currentResult")
        runningMachines.remove(machine.index)
    }

    watcher.interrupt()

    println("Total shortest is: $totalShortest")
}

data class SolutionState(val indicatorLights: String, val buttonPressCount: Int)

data class SolutionPath(val machine: Machine, val buttonPresses: List<Button>)

fun <A, B> List<A>.coroutinedMap(f: suspend (A) -> B): List<B> = runBlocking {
    map { async(Dispatchers.Default) { f(it) } }.awaitAll()
}
/**
 * [.#.##.####] (0,2,5,6,7,8,9) (0,1,6,7) (3,9) (1,2,3,4,5,7) (0,1,2,3,4,6,7,9) (1,2,4,6,8) (0,3,6,8) (1,2,4,5,6,9) (5,7,9) (1,3,5,9) {162,163,150,168,146,31,164,179,12,174}:
 * [..........] es fehlen: 1,3,4,6,7,8,9
 */