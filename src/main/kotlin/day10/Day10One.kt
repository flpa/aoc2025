package org.example.day10

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.example.readDay
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi

val sample = """
    [.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}
    [...#.] (0,2,3,4) (2,3) (0,4) (0,1,2) (1,2,3,4) {7,5,12,7,2}
    [.###.#] (0,1,2,3,4) (0,3,4) (0,1,2,4,5) (1,2) {10,11,11,5,10,5}
""".trimIndent()

data class Machine(
    val index: Int,
    val targetIndicatorLights: String,
    val buttons: List<Button>,
    val indicatorLights: String = ".".repeat(targetIndicatorLights.length),
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

    println("Parsed ${machines.size} machines")

//    println(machines)
    // Problematic machines: [46, 61, 142, 143, 162]

    val totalShortest = AtomicLong(0L)
    val finishedMachines = AtomicInt(0)

    val runningMachines = CopyOnWriteArrayList<Int>()


    val watcher = Thread.startVirtualThread {
        Thread.sleep(30_000)
        println("Running machines: $runningMachines")
    }

    machines.coroutinedMap { machine ->
        println("Running machine ${machine.index}")
        runningMachines.add(machine.index)

        // to prevent repeating actions
        val knownActions = mutableSetOf<SolutionState>()
        var shortestPath: SolutionPath? = null

        val paths = machine.buttons.map {
            SolutionPath(machine.press(it), listOf(it))
        }.toMutableList()

        var steps = 0L

        while (paths.isNotEmpty()) {
            ++steps
            val currentPath = paths.removeFirst()

//            println("Exploring step ${++steps}: $currentPath")

            if (currentPath.machine.isSolved) {
//                println("Machine is solved")
                if (shortestPath == null || shortestPath.buttonPresses.size < currentPath.buttonPresses.size) {
                    shortestPath = currentPath
//                    println("Its the shortest")
                }
                // If exploring further can only end up in a longer path, discard the path.
            } else if (shortestPath == null || shortestPath.buttonPresses.size > currentPath.buttonPresses.size + 1) {
                val pathsToAdd = currentPath.machine.buttons
                    .filter {
                        // Dont repeat the same button twice, or use a button that only affects solved indexes
                        it != currentPath.buttonPresses.last() || currentPath.machine.solvedIndexes.containsAll(it.affectedIndexes)
                        // Create a new solution path
                    }.map { button ->
                        SolutionPath(currentPath.machine.press(button), currentPath.buttonPresses + button)
                        // Skip solution paths if we reached the same state faster before
                    }.filter { newSolution ->
                        knownActions.none { knownAction ->
                            knownAction.indicatorLights == newSolution.machine.indicatorLights
                                    && knownAction.buttonPresses.size < newSolution.buttonPresses.size
                        }
                    }

                paths.addAll(pathsToAdd)
                knownActions.addAll(pathsToAdd.map { SolutionState(it.machine.indicatorLights, it.buttonPresses) })
            }
        }

//        println("Shorted path is: $shortestPath")
        val currentResult = totalShortest.addAndFetch(shortestPath!!.buttonPresses.size.toLong())
        val count = finishedMachines.addAndFetch(1)
        println("Finished machine ${machine.index}. $count/${machines.size} machines have finished.")
        println("Current result is $currentResult")
        runningMachines.remove(machine.index)
    }

    watcher.interrupt()

    println("Total shortest is: $totalShortest")
}

data class SolutionState(val indicatorLights: String, val buttonPresses: List<Button>)

data class SolutionPath(val machine: Machine, val buttonPresses: List<Button>)

fun <A, B> List<A>.coroutinedMap(f: suspend (A) -> B): List<B> = runBlocking {
    map { async(Dispatchers.Default) { f(it) } }.awaitAll()
}