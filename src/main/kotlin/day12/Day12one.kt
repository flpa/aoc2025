import org.example.logger

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

fun main() {
    val input = sample.lines()

    val (presents, locations) = parseInput(input)
    logger.info { "Parsed ${presents.size} presents and ${locations.size} locations" }
    logger.debug { "Presents: $presents, Locations: $locations" }
    val presentedIndexed = presents.associateBy { it.id }

    // otherwise: generate the grid and try to fit the presents in all variants
    // stupid solution: try each present variant in each position. position = top-left corner of present
    val successfulLocations =
        locations.filter { location ->
            val requiredPresentInstances = location.requiredPresents.map { presentedIndexed.getValue(it) }
            if (location.totalSize < requiredPresentInstances.sumOf { it.filledCellsCount }) {
                logger.info { "Location $location is impossible due to insufficient size" }
                false
            } else {
                val emptyGrid = location.createEmptyGrid()
                logger.debug {
                    "Trying to fill this ${location.width}x${location.height} location:$emptyGrid\n\nwith these ${presents.size} presents: " +
                        requiredPresentInstances.joinToString("\n") { "${it.shape}" }
                }
            }

            // permutations is: if three presents, 000, 001, 002, 003... where 0,1,2,3 are the variants of present 3?

            val listOfVariants = requiredPresentInstances.map { it.allVariants() }

            true
        }

    logger.info { "${successfulLocations.size} locations are successful" }
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

data class Present(
    val id: Int,
    val shape: Grid,
) {
    val filledCellsCount = shape.cells.flatMap { it.toCharArray().toList() }.count { it == '#' }

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
}
