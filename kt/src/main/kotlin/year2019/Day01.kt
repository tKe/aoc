package year2019

import aok.PuzDSL
import aoksp.AoKSolution

@AoKSolution
object Day01 : PuzDSL({
    part1 {
        lines.mapNotNull(String::toIntOrNull).sumOf { it / 3 - 2 }
    }

    part2 {
        lines.mapNotNull(String::toIntOrNull).sumOf { module ->
            generateSequence(module / 3 - 2) { it / 3 - 2 }
                    .takeWhile { it > 0 }
                    .sum()
        }
    }
})

fun main(): Unit = solveDay(1)
