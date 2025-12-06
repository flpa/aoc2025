package day3

import org.example.day3.joltage
import org.example.day3.joltage2
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import kotlin.test.assertEquals

class Day3Test {
    @Test
    fun joltage1() {
        val result =
            joltage("2225223322223234232321542252323222232322222321351322123227212331322222451232322231233322245353422214")

        assertEquals("75", result)
    }

    @Test
    fun joltage2() {
        val result =
            joltage2(
                "2225223322223234232321542252323222232322222321351322123227212331322222451232322231233322245353422214",
                2,
            )

        assertEquals("75", result)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "987654321111111:987654321111",
            "811111111111119:811111111119",
            "234234234234278:434234234278",
            "818181911112111:888911112111",
        ],
        delimiter = ':',
    )
    fun joltage2All(
        input: String,
        expected: String,
    ) {
        val result = joltage2(input, 12)

        assertEquals(expected, result)
    }
}
