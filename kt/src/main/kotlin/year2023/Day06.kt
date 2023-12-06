package year2023

import aok.PuzDSL
import aoksp.AoKSolution

fun main() = solveDay(
    6
)

@AoKSolution
object Day06 : PuzDSL({
    part1 {
        val (times, distances) = lines.map { it.split(" ").mapNotNull(String::toIntOrNull) }
        (times zip distances)
            .map { (time, record) -> (1..<time).count { (time - it) * it > record } }
            .fold(1L, Long::times)
    }

    part2 {
        val (time, distance) = lines.map { it.replace(" ", "").substringAfter(':').toLong() }
        (1..<time).count { (time - it) * it > distance }
    }
})
