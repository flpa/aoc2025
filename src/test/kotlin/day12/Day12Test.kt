package day12

import Coord
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
                Grid(
                    "####",
                    "##..",
                    "##..",
                ),
                // base flipped
                Grid(
                    "####",
                    "..##",
                    "..##",
                ),
                // rotated 90
                Grid(
                    "###",
                    "###",
                    "..#",
                    "..#",
                ),
                // rotated 90 flipped
                Grid(
                    "###",
                    "###",
                    "#..",
                    "#..",
                ),
                // rotated 180
                Grid(
                    "..##",
                    "..##",
                    "####",
                ),
                // rotated 180 flipped
                Grid(
                    "##..",
                    "##..",
                    "####",
                ),
                // rotated 270
                Grid(
                    "#..",
                    "#..",
                    "###",
                    "###",
                ),
                // rotated 270 flipped
                Grid(
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

    @Test
    fun testFilledGridCells1() {
        assertThat(
            Present(
                1, Grid(
                    "##",
                    ".."
                )
            ).filledCells
        ).containsOnly(
            Coord(0, 0),
            Coord(1, 0)
        )
    }

    @Test
    fun testFilledGridCells2() {
        assertThat(
            Present(
                1, Grid(
                    "#.",
                    ".#"
                )
            ).filledCells
        ).containsOnly(
            Coord(0, 0),
            Coord(1, 1)
        )
    }

    @Test
    fun testTryFit_zeroZero() {
        assertThat(
            Grid(
                "..",
                ".."
            ).tryFit(
                Present(
                    1, Grid(
                        "#",
                        "#"
                    )
                ), Coord(0, 0)
            )
        ).isEqualTo(
            Grid(
                "#.",
                "#."
            )
        )
    }

    @Test
    fun testTryFit_zeroOne() {
        assertThat(
            Grid(
                "..",
                ".."
            ).tryFit(
                Present(
                    1, Grid(
                        "#",
                        "#"
                    )
                ), Coord(1, 0)
            )
        ).isEqualTo(
            Grid(
                ".#",
                ".#"
            )
        )
    }

    @Test
    fun testTryFitDiagonal() {
        assertThat(
            Grid(
                "..",
                ".."
            ).tryFit(
                Present(
                    1, Grid(
                        "#.",
                        ".#"
                    )
                ), Coord(0, 0)
            )
        ).isEqualTo(
            Grid(
                "#.",
                ".#"
            )
        )
    }

    @Test
    fun testTryFitNoSpace() {
        assertThat(
            Grid(
                "..",
                "#."
            ).tryFit(
                Present(
                    1, Grid(
                        "#.",
                        "#."
                    )
                ), Coord(0, 0)
            )
        ).isNull()
    }

    @Test
    fun testTryFitTooBig() {
        assertThat(
            Grid(
                "..",
                ".."
            ).tryFit(
                Present(
                    1, Grid(
                        "#.",
                        "#."
                    )
                ), Coord(0, 1)
            )
        ).isNull()
    }
}
