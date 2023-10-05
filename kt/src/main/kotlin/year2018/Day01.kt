package year2018

import aok.PuzDSL
import aoksp.AoKSolution

@AoKSolution
object Day01 : PuzDSL({
    part1 {
        lines.mapNotNull(String::toIntOrNull).sum()
    }

    part2 {
        generateSequence(lines.mapNotNull(String::toIntOrNull)) { it }
            .flatten()
            .runningFold(0, Int::plus)
            .filterNot(mutableSetOf<Int>()::add)
            .first()
    }
})

fun main(): Unit = solveDay(1)
