import org.example.logger
import org.example.readDay

val sample = """
0:
###
##.
##.

1:
###
##.
.##

2:
.##
###
##.

3:
##.
###
##.

4:
###
#..
###

5:
###
.#.
###

4x4: 0 0 0 0 2 0
12x5: 1 0 1 0 2 2
12x5: 1 0 1 0 3 2
"""

fun <T> cartesianProduct(sets: List<Set<T>>): List<List<T>> {
    // Start with a list containing one empty combination
    return sets.fold(listOf(emptyList())) { accumulator, currentSet ->
        // For each existing combination, append each element of currentSet
        accumulator.flatMap { existingCombination ->
            currentSet.map { element -> existingCombination + element }
        }
    }
}

fun main() {
    val input = readDay(12)
//    val input = sample.lines()

    val (presents, locations) = parseInput(input)
    logger.info { "Parsed ${presents.size} presents and ${locations.size} locations" }
    logger.debug { "Presents: $presents, Locations: $locations" }
    val presentedIndexed = presents.associateBy { it.id }

    // otherwise: generate the grid and try to fit the presents in all variants
    // stupid solution: try each present variant in each position. position = top-left corner of present
    val successfulLocations =
        locations.filter { location ->
            val emptyGrid = location.createEmptyGrid()
            val requiredPresentInstances = location.requiredPresents.flatMapIndexed { presentIndex, howMany ->
                (0..<howMany).map { presentedIndexed.getValue(presentIndex) }
            }

            logger.debug {
                "Trying to fill this ${location.width}x${location.height} location:$emptyGrid\n\nwith these ${requiredPresentInstances.size} presents: " +
                        requiredPresentInstances.joinToString("\n") { "${it.shape}" }
            }
            val requiredCells = requiredPresentInstances.sumOf { it.filledCellsCount }
            if (location.totalSize < requiredCells) {
                logger.info { "Location $location is impossible due to insufficient size (space needed: $requiredCells, space: ${location.totalSize})" }
                false
            } else {
                val listOfVariants = requiredPresentInstances.map { it.allVariants() }

                // combinations is: if three presents, 000, 001, 002, 003... where 0,1,2,3 are the variants of present 3?
                val combinations = cartesianProduct(listOfVariants)

                logger.info { "${combinations.size} combinations for location" }

                doFit(emptyGrid, combinations)
            }
        }

    logger.info { "${successfulLocations.size} locations are successful" }
}

fun doFit(emptyGrid: Grid, combinations: List<List<Grid>>): Boolean {
    var j = 0
    combinations@ for(presentsToPlace in combinations) {
        j++
        logger.debug { "Trying combination $j/${combinations.size}: $presentsToPlace" }
        var combinationGrid = emptyGrid
        for (i in 0 until presentsToPlace.size) {
            val presentShape = presentsToPlace[i]
            val gridWithPresentPlaced = tryPlacePresent(combinationGrid, Present(i, presentShape))

            if (gridWithPresentPlaced == null) {
                logger.debug { "Couldn't place present, trying next combination" }
                continue@combinations
            } else {
                combinationGrid = gridWithPresentPlaced
            }
        }
        logger.info { "Combination $j was successful: $combinationGrid" }
        return true
    }

    logger.info { "No combinations remain" }
    return false
}

fun tryPlacePresent(grid: Grid, present: Present): Grid? {
    // TODO maybe fill in letters instead of #?
    for (x in 0..grid.width - present.shape.width) {
        for (y in 0..grid.height - present.shape.height) {
            val modifiedGrid = grid.tryFit(present, Coord(x, y))
            if (modifiedGrid != null) {
                logger.debug { "Placed a present: $modifiedGrid" }
                return modifiedGrid
            }
        }
    }
    return null
}

private fun parseInput(input: List<String>): Pair<List<Present>, List<PresentLocation>> {
    var currentPresentId: Int? = null
    var currentPresentShape = mutableListOf<String>()

    val presents = mutableListOf<Present>()
    val locations = mutableListOf<PresentLocation>()

    input.forEach { line ->
        if (line.isNotBlank()) {
            val splitResult = line.split(':')

            fun savePreviousPresent() {
                if (currentPresentId != null) {
                    val present =
                        Present(
                            id = currentPresentId!!,
                            shape = Grid(currentPresentShape.toList()),
                        )
                    presents += present
                }
                currentPresentId = null
            }

            if (splitResult.size == 2) {
                // Its a present header or a location spec
                if (splitResult[0].contains("x")) {
                    savePreviousPresent()
                    // location spec
                    val (sizePart, requiredPresentsPart) = splitResult
                    val (widthString, heightString) = sizePart.split('x')
                    val width = widthString.toInt()
                    val height = heightString.toInt()
                    val requiredPresents = requiredPresentsPart.trim().split(" ").map { it.toInt() }

                    val presentLocation =
                        PresentLocation(
                            width = width,
                            height = height,
                            requiredPresents = requiredPresents,
                        )
                    locations += presentLocation
                } else {
                    // present header
                    // save previous present if any
                    savePreviousPresent()
                    // start new present
                    currentPresentId = splitResult[0].toInt()
                    currentPresentShape = mutableListOf()
                }
            } else {
                currentPresentShape += line
            }
        }
    }
    return Pair(presents.toList(), locations.toList())
}

data class Coord(val x: Int, val y: Int)

data class Present(
    val id: Int,
    val shape: Grid,
) {
    val filledCells = shape.cells.flatMapIndexed { y, line ->
        line.mapIndexedNotNull { x, cell ->
            if (cell == '#') {
                Coord(x, y)
            } else {
                null
            }
        }
    }

    val filledCellsCount = filledCells.size

    fun allVariants(): Set<Grid> {
        val variants = mutableSetOf<Present>()
        var currentShape = shape
        repeat(4) {
            // note: flip vertically is the same as rotate 180 + flip horizontal
            currentShape = rotate90(currentShape)
            variants += Present(id, currentShape)
            val flipped = flipHorizontal(currentShape)
            variants += Present(id, flipped)
        }
        return variants.map { it.shape }.toSet()
    }
}

fun rotate90(grid2: Grid): Grid {
    val grid = grid2.cells
    val rows = grid.size
    val cols = grid.first().length

    val cells =
        (0 until cols).map { col ->
            buildString {
                for (row in rows - 1 downTo 0) {
                    append(grid[row][col])
                }
            }
        }
    return Grid(cells)
}

fun flipHorizontal(grid: Grid): Grid = Grid(grid.cells.map { it.reversed() })

data class PresentLocation(
    val width: Int,
    val height: Int,
    val requiredPresents: List<Int>,
) {
    val totalSize: Int = width * height

    fun createEmptyGrid(): Grid = Grid(List(height) { ".".repeat(width) })
}

data class Grid(
    val cells: List<String>,
) {
    val width: Int = cells.first().length
    val height: Int = cells.size

    override fun toString(): String = "\n" + cells.joinToString("\n")

    constructor(vararg cells: String) : this(cells.toList())

    fun tryFit(present: Present, at: Coord): Grid? {
        val newCells = cells.map { it.toMutableList() }.toMutableList()
        present.filledCells.forEach { cell ->
            val y = cell.y + at.y
            val x = cell.x + at.x
            if (newCells.getOrNull(y)?.getOrNull(x) != '.') {
                return null
            }
            newCells[y][x] = present.id.toString()[0]
        }
        return Grid(newCells.map { it.joinToString("") })
    }
}
