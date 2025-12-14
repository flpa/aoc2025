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

    logger.info { "Presents: $presents, Locations: $locations" }
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
                            shape = currentPresentShape.toList(),
                        )
                    logger.debug { "Present: $present" }
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
                    logger.debug { "Present location: $presentLocation" }
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
    val shape: List<String>,
) {
    val width: Int = shape.first().length
    val height: Int = shape.size
    val filledCellsCount = shape.flatMap { it.toCharArray().toList() }.count { it == '#' }

    fun allVariants(): Set<List<String>> {
        val variants = mutableSetOf<Present>()
        var currentShape = shape
        repeat(4) {
            currentShape = rotate90(currentShape)
            variants += Present(id, currentShape)
            val flipped = flipHorizontal(currentShape)
            variants += Present(id, flipped)
        }
        return variants.map { it.shape }.toSet()
    }

    private fun flipHorizontal(currentShape: List<String>): List<String> = currentShape.map { it.reversed() }
}

fun rotate90(grid: List<String>): List<String> {
    val rows = grid.size
    val cols = grid.first().length

    return (0 until cols).map { col ->
        buildString {
            for (row in rows - 1 downTo 0) {
                append(grid[row][col])
            }
        }
    }
}

data class PresentLocation(
    val width: Int,
    val height: Int,
    val requiredPresents: List<Int>,
) {
    val totalSize: Int = width * height
}
