package org.example

import io.github.oshai.kotlinlogging.KotlinLogging
import java.nio.file.Files
import java.nio.file.Path

fun readDay(day: Int): List<String> {
    return Files.readAllLines(Path.of("src/main/resources/day${day}/input.txt"))
}

val logger = KotlinLogging.logger {}
