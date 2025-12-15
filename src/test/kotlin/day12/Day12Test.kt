package day12

import Grid
import Present
import flipHorizontal
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import rotate90
import kotlin.test.assertEquals

class Day12Test {
    val present =
        Present(
            1,
            Grid(
                "####",
                "##..",
                "##..",
            ),
        )

    @Test
    fun testRotate90() {
        val rotated =
            rotate90(
                Grid(
                    "##",
                    "##",
                    "#.",
                    ".#",
                ),
            )

        assertEquals(
            Grid(
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
                Grid(
                    "abcd",
                    "efgh",
                    "ijkl",
                ),
            )

        assertEquals(
            Grid(
                "iea",
                "jfb",
                "kgc",
                "lhd",
            ),
            rotated,
        )
    }

    @Test
    fun testFlipHorizontal() {
        val flipped =
            flipHorizontal(
                Grid(
                    "####",
                    "##..",
                    "##..",
                ),
            )

        assertEquals(
            Grid(
                "####",
                "..##",
                "..##",
            ),
            flipped,
        )
    }

    @Test
    fun testPresentBaseProperties() {
        assertEquals(4, present.shape.width)
        assertEquals(3, present.shape.height)
        assertEquals(8, present.filledCellsCount)
    }

    @Test
    fun testPresentVariants() {
        val allVariants = present.allVariants()

        val expected =
            setOf(
                // base
                listOf(
                    "####",
                    "##..",
                    "##..",
                ),
                // base flipped
                listOf(
                    "####",
                    "..##",
                    "..##",
                ),
                // rotated 90
                listOf(
                    "###",
                    "###",
                    "..#",
                    "..#",
                ),
                // rotated 90 flipped
                listOf(
                    "###",
                    "###",
                    "#..",
                    "#..",
                ),
                // rotated 180
                listOf(
                    "..##",
                    "..##",
                    "####",
                ),
                // rotated 180 flipped
                listOf(
                    "##..",
                    "##..",
                    "####",
                ),
                // rotated 270
                listOf(
                    "#..",
                    "#..",
                    "###",
                    "###",
                ),
                // rotated 270 flipped
                listOf(
                    "..#",
                    "..#",
                    "###",
                    "###",
                ),
            )

        assertThat(allVariants)
            .hasSameSizeAs(expected)
            .containsExactlyInAnyOrder(*expected.toTypedArray())
    }
}
