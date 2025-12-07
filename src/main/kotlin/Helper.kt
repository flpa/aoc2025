package org.example

import java.nio.file.Files
import java.nio.file.Path

fun readDay(day: Int): List<String> {
    return Files.readAllLines(Path.of("src/main/resources/day${day}/input.txt"))
}