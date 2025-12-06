package day3

import org.example.day3.joltage
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day3Test {
    @Test
    fun joltage1() {
        val result =
            joltage("2225223322223234232321542252323222232322222321351322123227212331322222451232322231233322245353422214")

        assertEquals("75", result)
    }
}
