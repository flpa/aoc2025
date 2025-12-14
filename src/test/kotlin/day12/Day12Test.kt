package day12

import Present
import org.junit.jupiter.api.Test
import rotate90
import kotlin.test.assertEquals

class Day12Test {
    val present =
        Present(
            1,
            listOf(
                "####",
                "##..",
                "##..",
            ),
        )

    @Test
    fun testRotate90() {
        val rotated =
            rotate90(
                listOf(
                    "##",
                    "##",
                    "#.",
                    ".#",
                ),
            )

        assertEquals(
            listOf(
                ".###",
                "#.##",
            ),
            rotated,
        )
    }

    @Test
    fun testRotate90_2() {
        val rotated =
            rotate90(
                listOf(
                    "abcd",
                    "efgh",
                    "ijkl",
                ),
            )

        assertEquals(
            listOf(
                "iea",
                "jfb",
                "kgc",
                "lhd",
            ),
            rotated,
        )
    }

    @Test
    fun testPresentBaseProperties() {
        assertEquals(4, present.width)
        assertEquals(3, present.height)
        assertEquals(8, present.filledCellsCount)
    }

    @Test
    fun testPresentVariants() {
        val allVariants = present.allVariants()

        assertEquals(
            setOf(
                listOf(
                    "####",
                    "##..",
                    "##..",
                ),
                listOf(
                    "####",
                    "..##",
                    "..##",
                ),
            ),
            allVariants,
        )
    }
}
